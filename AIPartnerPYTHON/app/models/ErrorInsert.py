from pydantic import BaseModel
from typing import List


class ErrorInsert(BaseModel):
    correct_answer: str
    error_answer: str
    question: str
    reason: str
    username: str

class ErrorInsertWorkflowRun(BaseModel):
    input: ErrorInsert
