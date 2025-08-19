from pydantic import BaseModel
from typing import List

class Need(BaseModel):
    concept: str  # 数据结构概念名称
    time: int  # 学习时长（单位：小时）
    week: List[int]  # 学习周次（如[1,3,4,5]表示第1、3、4、5周学习）

class WorkflowRun(BaseModel):
    need: Need  # 嵌套的学习需求数据
