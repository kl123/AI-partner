from pydantic import BaseModel


class TestAI(BaseModel):
    display_name: str  # 内容描述
    difficulty: int  # 难度等级
    subject: str  # 显示名称
    response: str  # 学科名称
    TorF: str  # 试题类型
    choice:str
    description:str


class TestAIWorkflowRun(BaseModel):
    need: TestAI  # 嵌套的学习需求数据