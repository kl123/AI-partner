from pydantic import BaseModel


class TestAI(BaseModel):
    description: str  # 内容描述
    difficulty: int  # 难度等级
    display_name: str  # 显示名称
    nmb: list[int]  # 编号序列
    subject: str  # 学科名称
    type: list[str]  # 试题类型


class TestAIWorkflowRun(BaseModel):
    need: TestAI  # 嵌套的学习需求数据