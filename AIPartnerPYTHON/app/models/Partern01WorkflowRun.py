from pydantic import BaseModel
from typing import List

class Partern01(BaseModel):
    concept: str  # 数据结构概念名称
    time: int  # 学习时长（单位：小时）
    week: List[int]  # 学习周次（如[1,3,4,5]表示第1、3、4、5周学习）

class Partern01WorkflowRun(BaseModel):
    need: Partern01  # 嵌套的学习需求数据
