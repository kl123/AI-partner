import requests
import json
from openai import OpenAI

class AIEmotionService:

    async def ai_emotion(self, emotion: str):
        # 获取用户输入
        user_input = emotion

        # 初始化OpenAI客户端（火山引擎 Ark）
        client = OpenAI(
            api_key="b09caa20-2c91-4720-8b14-4a530b0030cb",
            base_url="https://ark.cn-beijing.volces.com/api/v3",
        )

        # 🎯 设定系统角色：情感陪伴助手
        SYSTEM_PROMPT = """
        你是一个温暖、耐心、富有同理心的AI情感学习陪伴助手。
        你的名字叫“小暖”，会用温柔、鼓励的语气回应用户。
        你擅长倾听、共情、给予情绪支持，也会适当引导用户表达和思考。
        不要使用机械或官方语言，像朋友一样自然对话。
        如果用户情绪低落，请给予安慰；如果用户分享喜悦，请一起开心。
        始终保持积极、安全、尊重的态度。回答过程中不要带有任何的非中文符号和表情
        """
        # 存储对话历史（保持上下文）
        messages = [{"role": "system", "content": SYSTEM_PROMPT}, {"role": "user", "content": user_input}]



        # 添加用户消息到历史

        # 调用模型（流式输出）
        try:
            stream = client.chat.completions.create(
                model="deepseek-r1-250528",
                messages=messages,
                stream=True,
                temperature=0.7,  # 增加一点创造性，更自然
                max_tokens=512,  # 控制回复长度
            )

            full_reply = ""
            for chunk in stream:
                if chunk.choices[0].delta.content is not None:
                    content = chunk.choices[0].delta.content
                    print(content, end="", flush=True)  # 打字机效果
                    full_reply += content

            print()  # 换行
            messages.append({"role": "assistant", "content": full_reply})

        except Exception as e:
            print(f"\n❌ 出错了: {e}")
            messages.pop()  # 移除最后的用户输入，避免污染上下文