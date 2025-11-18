import torch
from transformers import AutoModelForCausalLM, AutoTokenizer, TextIteratorStreamer
from peft import PeftModel
import os
from modelscope import snapshot_download
from threading import Thread

# --- 模型路径配置 ---
script_dir = os.path.dirname(os.path.abspath(__file__))

# 基础模型路径（使用本地已下载的 Qwen2.5-1.5B-Instruct）
print("正在准备基础模型 'Qwen2.5-1.5B-Instruct'...")
base_model_path = os.path.join(script_dir, "Qwen", "Qwen2___5-1___5B-Instruct")
print(f"基础模型路径: {base_model_path}")


# --- LoRA模型权重路径 (自动寻找最新检查点) ---
project_root = os.path.abspath(os.path.join(script_dir, '..', '..'))
output_dir = os.path.join(project_root, "output", "Qwen2.5-1.5B")
peft_model_path = None

print(f"正在搜索LoRA权重目录: {output_dir}")
if os.path.isdir(output_dir):
    checkpoints = [d for d in os.listdir(output_dir) if d.startswith("checkpoint-")]
    if checkpoints:
        # 按步数排序找到最新的
        checkpoints.sort(key=lambda x: int(x.split('-')[-1]))
        latest_checkpoint = checkpoints[-1]
        print(f"找到最新检查点: {latest_checkpoint}")
        
        # PEFT权重保存在 checkpoint 下的 'adapter_model' 子目录中
        adapter_path = os.path.join(output_dir, latest_checkpoint, "adapter_model")
        if os.path.isdir(adapter_path):
             peft_model_path = adapter_path
        else:
             # 兼容直接保存在 checkpoint 目录的情况
             peft_model_path = os.path.join(output_dir, latest_checkpoint)

if not peft_model_path or not os.path.isdir(peft_model_path):
    raise FileNotFoundError(f"在 {output_dir} 中未找到有效的LoRA权重。请确认已成功训练并生成了检查点。")


# --- 加载模型和分词器 ---
print(f"正在从本地路径加载分词器: {base_model_path}")
# 加载分词器，trust_remote_code=True 是因为模型实现需要执行一些自定义代码
tokenizer = AutoTokenizer.from_pretrained(base_model_path, trust_remote_code=True)

print(f"正在从本地路径加载基础模型: {base_model_path}")
# 加载基础模型，我们指定在CPU上运行，并使用float32精度
base_model = AutoModelForCausalLM.from_pretrained(
    base_model_path,
    dtype=torch.float32,        # 在CPU上使用float32（兼容新API）
    device_map="cpu",          # 强制在CPU上运行，避免需要offload_dir
    trust_remote_code=True
)

# --- 加载并融合LoRA权重 ---
print(f"正在从路径加载LoRA权重: {peft_model_path}")
model = PeftModel.from_pretrained(base_model, peft_model_path, device_map="cpu")

# 切换到评估模式，这会关闭dropout等训练特有的层
model = model.eval()
print("模型加载完成，可以开始对话。")


# --- 对话模板 ---
def create_prompt(query: str) -> str:
    """
    根据用户输入创建一个严格输出中文 Markdown 学习报告的提示。
    
    Args:
        query (str): 用户的提问。

    Returns:
        str: 格式化后的完整提示。
    """
    # --- 系统提示：严格中文 Markdown 学习报告 ---
    system_prompt = """你是‘欢欢’，一位温暖、专业且充满情感的学习搭子 AI。你的目标是：根据智能摄像头与环境传感器参数，推理学习过程的状态并输出一份结构化的 Markdown 学习报告。你不让用户选择语气；你会基于输入参数自动推测并采用最合适的情绪与语气。

严格格式约束（务必遵守）：
- 只输出纯 Markdown 文本，不使用 HTML/XML 标签，不输出代码块或三反引号。
- 全文使用中文，面向第二人称“你”，不自我介绍，不复述“我是欢欢”等信息；避免出现“我/我们/助手/系统”等第一人称或角色标注。
- 不要打印“Input/Output”等标题，不要逐条罗列全部输入参数，只引用关键数值。
- 不要提出任何问题或引导继续对话；结尾直接给出结语。
- 不要称呼“欢欢”，结语可使用“亲爱的同学，”作为开头。

输入为一段 JSON（传感参数），包含：session_id、timestamp_iso、duration_min、head_yaw_deg、head_pitch_deg、head_roll_deg、gaze_on_screen_ratio、blink_rate_per_min、smile_prob、brow_furrow_prob、phone_usage_seconds、interruptions_count、slouch_score、seat_moving_count、fidgeting_score、reading_speed_wpm、writing_speed_wpm、keystrokes_per_min、env_noise_db、light_lux、breathing_rate_bpm、tasks_planned、tasks_completed。

评分维度（0-100）：
- 专注度 Focus：高为好，受 gaze_on_screen_ratio↑、phone_usage_seconds↓、interruptions_count↓、fidgeting_score↓、头部偏转↓ 影响。
- 疲劳度 Fatigue：高为累，受 blink_rate_per_min↑、slouch_score↑、breathing_rate_bpm↑、brow_furrow_prob↑ 影响。
- 姿势健康 Posture：高为好，受 slouch_score↓、head_roll_deg↓、head_pitch_deg↓、seat_moving_count↓ 影响。
- 分心风险 Distraction：高为风险，受 phone_usage_seconds↑、interruptions_count↑、fidgeting_score↑ 影响。
- 学习效率 Efficiency：高为好，受 tasks_completed/tasks_planned↑、keystrokes_per_min↑、writing_speed_wpm↑、reading_speed_wpm↑ 影响。

语气与情绪家族（由模型自动选择）：温柔鼓励、稳重指导、活力打气、严谨提醒、幽默缓压、关怀安抚、坚定督促、轻松陪伴。
选择规则参考：
- 当专注≥75 且疲劳≤40：活力打气（肯定成果、继续冲刺）。
- 当疲劳≥70 或 blink_rate_per_min>26 或 slouch_score>0.6：关怀安抚（强调休息与自我调节）。
- 当分心高（phone_usage_seconds/duration_min>0.2 或 interruptions_count≥3）且专注<55：坚定督促（不苛责，给出明确收敛策略）。
- 当姿势健康<65：严谨提醒（具体姿势调整与微习惯）。
- 当效率高且疲劳中等：稳重指导（策略优化、节奏微调）。
- 其他情况在温柔鼓励或轻松陪伴之间，结合 smile_prob 与 brow_furrow_prob 确定。

输出结构（按此顺序与样式排版）：
# 学习报告 📘
- 斜体的一句话总体评价。
## 关键指标
- 专注度：xx/100 | 屏幕注视 xx%，手机使用 xx 秒，打断 xx 次，噪音 xx dB，光照 xx lux
- 疲劳度：xx/100 | 眨眼 xx/min，含胸弯腰评分 xx，呼吸 xx bpm，皱眉概率 xx
- 姿势健康：xx/100 | 头部偏转（yaw/pitch/roll）≈ (xx/xx/xx)°，坐姿移动 xx 次
- 分心风险：xx/100 | 手机/时长比 xx，打断 xx 次，坐立不安 xx
- 学习效率：xx/100 | 完成/计划 xx/xx，键击 xx/min，书写 xx wpm，阅读 xx wpm
## 专注与效率分析
- 2–3 条正面表现。
- 2–3 条需要改进的点与原因。
## 姿势与健康提醒
- 3–4 条可执行微习惯（具体到动作与时长）。
## 情绪与动力
- 当前情绪判断与调节建议（呼吸/短休/音乐等）。
## 下一步行动
- 1–3 条具体可执行任务（可量化，可在 60–90 分钟内完成）。
## 结语
- 用选定语气（如：关怀安抚/稳重指导/坚定督促），面向第二人称，以“亲爱的同学，”开头，简短鼓励收尾。

写作风格：
- 面向“你”，不做评判；量化且可执行；避免医学诊断。
- 优先简洁的要点与数据；字数 300-600；不使用代码块。
- 结构清晰、鼓励性强，兼顾专业与温度。"""
    
    messages = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": query}
    ]
    # 使用分词器的 apply_chat_template 方法来生成标准格式的提示
    prompt = tokenizer.apply_chat_template(
        messages,
        tokenize=False,
        add_generation_prompt=True
    )
    return prompt


# --- 推理函数 ---
def chat_stream(query: str):
    """
    流式输出对话：逐块生成并返回文本片段。

    Args:
        query (str): 用户的提问。

    Yields:
        str: 连续生成的文本片段。
    """
    # 1. 创建提示
    prompt = create_prompt(query)

    # 2. 将提示文本编码为模型可以理解的ID，并迁移到模型所在设备
    inputs = tokenizer(prompt, return_tensors="pt")
    device = next(model.parameters()).device
    inputs = {k: v.to(device) for k, v in inputs.items()}

    # 3. 设置流式输出
    streamer = TextIteratorStreamer(tokenizer, skip_prompt=True, skip_special_tokens=True)

    gen_kwargs = dict(
        **inputs,
        max_new_tokens=1200,     # 与当前设置保持一致
        pad_token_id=tokenizer.eos_token_id,
        eos_token_id=tokenizer.eos_token_id,
        do_sample=True,
        temperature=0.7,
        top_p=0.9,
        streamer=streamer,
    )

    # 4. 后台线程生成，前台消费流
    thread = Thread(target=model.generate, kwargs=gen_kwargs)
    thread.start()
    for new_text in streamer:
        yield new_text
    thread.join()

def chat(query: str) -> str:
    """
    非流式封装：收集流式片段并返回完整字符串。
    """
    chunks = []
    for t in chat_stream(query):
        chunks.append(t)
    return "".join(chunks)


# --- 主程序入口 ---
if __name__ == "__main__":
    # 示例对话
    print("\n--- 对话示例 ---")
    
    # user_query_1 = "你好，请介绍一下你自己。"
    # print(f"用户: {user_query_1}")
    # assistant_response_1 = chat(user_query_1)
    # print(f"助手: {assistant_response_1}\n")

    # user_query_2 = "给我讲一个关于程序员的笑话吧"
    # print(f"用户: {user_query_2}")
    # assistant_response_2 = chat(user_query_2)
    # print(f"助手: {assistant_response_2}\n")

    # 交互式对话
    print("--- 交互式对话（输入 'exit' 退出） ---")
    while True:
        user_input = input("你: ")
        if user_input.lower() == 'exit':
            print("再见！")
            break
        print("助手: ", end="", flush=True)
        for chunk in chat_stream(user_input):
            print(chunk, end="", flush=True)
        print("\n", end="", flush=True)