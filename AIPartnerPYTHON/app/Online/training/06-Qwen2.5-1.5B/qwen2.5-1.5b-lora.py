import os
import json
import torch
from transformers import AutoModelForCausalLM, AutoTokenizer, GenerationConfig, TrainingArguments, Trainer, TrainerCallback, TrainerState, TrainerControl
from peft import LoraConfig, TaskType, get_peft_model, PeftModel
from modelscope import snapshot_download

# 兼容transformers默认checkpoint命名与权重文件名
PREFIX_CHECKPOINT_DIR = "checkpoint"
SAFE_WEIGHTS_NAME = "model.safetensors"


# 从本地读取数据集（鲁棒路径解析与回退）
script_dir = os.path.dirname(os.path.abspath(__file__))

def find_file_upwards(filename: str, start_dir: str, max_up: int = 6):
    cur = start_dir
    for _ in range(max_up + 1):
        candidate = os.path.join(cur, filename)
        if os.path.exists(candidate):
            return candidate
        parent = os.path.dirname(cur)
        if parent == cur:
            break
        cur = parent
    return None

local_dataset_path = find_file_upwards("huanhuan.json", script_dir)
if not local_dataset_path:
    raise FileNotFoundError("未找到本地数据集文件: 请将 huanhuan.json 放置在脚本目录或其上层目录中")


# 使用本地已下载的 Qwen2.5-1.5B-Instruct 模型
model_dir = os.path.join(script_dir, "Qwen", "Qwen2___5-1___5B-Instruct")


# 实例化tokenizer
tokenizer = AutoTokenizer.from_pretrained(model_dir, use_fast=False, trust_remote_code=True)
tokenizer.pad_token = tokenizer.eos_token
tokenizer.padding_side = 'right'


# 定义数据处理逻辑（适配 huanhuan.json 的 messages 格式）
def process_func(system_prompt: str, input_json_str: str, assistant_markdown: str):
    MAX_SEQ_LENGTH = int(os.environ.get("MAX_SEQ_LENGTH", "256"))
    # 构建 System / User / Assistant 三段，并只监督 Assistant 段
    sys_fmt = tokenizer(f"System: {system_prompt}\n\n", add_special_tokens=False)
    usr_fmt = tokenizer(f"User: {input_json_str}\n\n", add_special_tokens=False)
    rsp_fmt = tokenizer(f"Assistant: {assistant_markdown}", add_special_tokens=False)

    input_ids = sys_fmt["input_ids"] + usr_fmt["input_ids"] + rsp_fmt["input_ids"] + [tokenizer.pad_token_id]
    attention_mask = sys_fmt["attention_mask"] + usr_fmt["attention_mask"] + rsp_fmt["attention_mask"] + [1]
    prefix_len = len(sys_fmt["input_ids"]) + len(usr_fmt["input_ids"])
    labels = [-100] * prefix_len + rsp_fmt["input_ids"] + [tokenizer.pad_token_id]

    # 截断 / 填充到固定长度
    if len(input_ids) > MAX_SEQ_LENGTH:
        input_ids = input_ids[:MAX_SEQ_LENGTH]
        attention_mask = attention_mask[:MAX_SEQ_LENGTH]
        labels = labels[:MAX_SEQ_LENGTH]

    # 如不足最大长度，则进行填充
    padding_length = MAX_SEQ_LENGTH - len(input_ids)
    input_ids = input_ids + [tokenizer.pad_token_id] * padding_length
    attention_mask = attention_mask + [0] * padding_length  # 填充的 attention_mask 为 0
    labels = labels + [-100] * padding_length  # 填充的 label 为 -100
    
    return input_ids, attention_mask, labels


train_dataset = []
# 解析 huanhuan.json（messages 格式）
with open(local_dataset_path, 'r', encoding='utf-8') as f:
    raw = json.load(f)

if not isinstance(raw, dict):
    raise ValueError("huanhuan.json 顶层应为字典，包含 meta/system_prompt 与 examples 列表")


system_prompt = str(raw.get('meta', {}).get('system_prompt', '')).strip()
examples = raw.get('examples', [])
if not system_prompt:
    # 如未提供，使用简化的默认系统提示
    system_prompt = "你是‘欢欢’，根据传感参数生成结构化的 Markdown 学习报告，语气由模型自动选择。"

# 更新 system_prompt 以包含新的指令
system_prompt += "\n\n你的任务是提供一次性的、完整的学习报告。在报告的结尾，直接给出结语，不要提出任何问题，也不要引导用户进行下一步的对话。"


if not isinstance(examples, list) or not examples:
    raise ValueError("huanhuan.json 中 examples 列表为空或格式不正确")

# 选取部分样本进行演示训练（可通过环境变量覆盖）
MAX_TRAIN_SAMPLES = int(os.environ.get("MAX_TRAIN_SAMPLES", "2"))
selected = []
for ex in examples:
    if isinstance(ex, dict) and isinstance(ex.get('input'), dict) and isinstance(ex.get('assistant_markdown'), str):
        selected.append(ex)
    if len(selected) >= MAX_TRAIN_SAMPLES:
        break

if not selected:
    raise ValueError("examples 中未找到包含 input(dict) 与 assistant_markdown(str) 的有效样本")

# 构建训练数据集
for ex in selected:
    user_json = json.dumps(ex['input'], ensure_ascii=False)
    output_text = ex['assistant_markdown']
    input_ids, attention_mask, labels = process_func(system_prompt, user_json, output_text)
    train_dataset.append({
        "input_ids": input_ids,
        "attention_mask": attention_mask,
        "labels": labels,
    })

# 打印一个样本的前若干字符以确认模板（避免过长输出）
decoded = tokenizer.decode(train_dataset[0]["input_ids"]) if train_dataset else ""
print(decoded[:200] + ("..." if len(decoded) > 200 else ""))


# 实例化base model（本地加载）
base_model = AutoModelForCausalLM.from_pretrained(model_dir, trust_remote_code=True)
try:
    base_model.generation_config = GenerationConfig.from_pretrained(model_dir, trust_remote_code=True)
except Exception:
    base_model.generation_config = GenerationConfig()

base_model.generation_config.pad_token_id = tokenizer.pad_token_id

# LoRA配置
config = LoraConfig(
    task_type=TaskType.CAUSAL_LM, 
    target_modules=["q_proj", "k_proj", "v_proj", "o_proj", "gate_proj", "up_proj", "down_proj"],
    inference_mode=False,  # 训练模式
    r=16,  # Lora 秩 (从 4 提升到 16)
    lora_alpha=32,  # Lora alaph，具体作用参见 Lora 原理
    lora_dropout=0.1  # Dropout 比例
)

# 实例化LoRA模型
model = get_peft_model(base_model, config)
# 打印参与训练的参数比例（PEFT接口）
try:
    model.print_trainable_parameters()
except Exception:
    pass

# Data collator：将列表转为张量，便于批量训练
def data_collator(features):
    import torch
    input_ids = torch.tensor([f["input_ids"] for f in features], dtype=torch.long)
    attention_mask = torch.tensor([f["attention_mask"] for f in features], dtype=torch.long)
    labels = torch.tensor([f["labels"] for f in features], dtype=torch.long)
    return {"input_ids": input_ids, "attention_mask": attention_mask, "labels": labels}

# 简易顺序 DataLoader：不依赖 mindtorch 的 DataLoader，避免其随机数问题
class SimpleSequentialDataLoader:
    def __init__(self, dataset, batch_size, collate_fn):
        self.dataset = dataset
        self.batch_size = batch_size
        self.collate_fn = collate_fn

    def __iter__(self):
        batch = []
        for item in self.dataset:
            batch.append(item)
            if len(batch) == self.batch_size:
                yield self.collate_fn(batch)
                batch = []
        if batch:
            yield self.collate_fn(batch)

    def __len__(self):
        import math
        return math.ceil(len(self.dataset) / self.batch_size)

# 固定顺序的 Trainer，覆盖 DataLoader 获取逻辑，彻底绕过 mindtorch 的随机逻辑
class FixedOrderTrainer(Trainer):
    def get_train_dataloader(self):
        if self.train_dataset is None:
            raise ValueError("Trainer 需要提供 train_dataset 用于训练")
        return SimpleSequentialDataLoader(
            dataset=self.train_dataset,
            batch_size=self.args.train_batch_size,
            collate_fn=self.data_collator,
        )

# Callback函数，随save_steps定义的步数保存LoRA adapter权重
class SavePeftModelCallback(TrainerCallback):
    def on_save(
        self,
        args: TrainingArguments,
        state: TrainerState,
        control: TrainerControl,
        **kwargs,
    ): 
        # LoRA adapter权重保存路径
        checkpoint_folder = os.path.join(
            args.output_dir, f"{PREFIX_CHECKPOINT_DIR}-{state.global_step}"
        )       

        # 保存LoRA adapter权重
        peft_model_path = os.path.join(checkpoint_folder, "adapter_model")
        kwargs["model"].save_pretrained(peft_model_path, safe_serialization=True)

        # 移除额外保存的base model权重，节约空间
        for fname in [SAFE_WEIGHTS_NAME, "pytorch_model.bin"]:
            fpath = os.path.join(checkpoint_folder, fname)
            os.remove(fpath) if os.path.exists(fpath) else None

        return control


# 训练超参
args = TrainingArguments(
    output_dir="./output/Qwen2.5-1.5B",  # 输出保存路径
    per_device_train_batch_size=1,  # batch size
    logging_steps=1,  # 每多少步记录一次训练日志
    num_train_epochs=16,  # epoch数
    save_steps=3,  # 每多少步保存一次权重
    remove_unused_columns=False,  # 数据集为自定义字典，避免被清理
    gradient_checkpointing=False,
    max_grad_norm=1.0,
    seed=42,
    use_cpu=True,
)

# 定义Trainer
trainer = FixedOrderTrainer(
    model=model,
    args=args,
    train_dataset=train_dataset,
    data_collator=data_collator,
    callbacks=[SavePeftModelCallback()],
)

# 启动微调
trainer.train()

# 简单推理验证（在本机上运行）
latest_adapter = None
output_dir = "./output/Qwen2.5-1.5B"
if os.path.isdir(output_dir):
    ckpts = [d for d in os.listdir(output_dir) if d.startswith(PREFIX_CHECKPOINT_DIR)]
    if ckpts:
        ckpts.sort(key=lambda x: int(x.split("-")[-1]))
        latest_adapter = os.path.join(output_dir, ckpts[-1], "adapter_model")

if latest_adapter and os.path.isdir(latest_adapter):
    infer_base = AutoModelForCausalLM.from_pretrained(model_dir, trust_remote_code=True)
    infer_model = PeftModel.from_pretrained(infer_base, latest_adapter)
    infer_model.generation_config = base_model.generation_config
    # 推理模型切换到 CPU
    infer_model.to('cpu')
    # 使用同一系统提示与示例输入进行推理
    try:
        infer_input = selected[0]['input'] if selected else {}
    except Exception:
        infer_input = {}
    prompt = f"System: {system_prompt}\n\nUser: {json.dumps(infer_input, ensure_ascii=False)}\n\nAssistant: "
    inputs = tokenizer(prompt, return_tensors="pt")
    # 将输入张量迁移到 CPU
    inputs = {k: v.to('cpu') for k, v in inputs.items()}
    outputs = infer_model.generate(input_ids=inputs["input_ids"], max_new_tokens=64)
    print(tokenizer.decode(outputs[0].tolist()))