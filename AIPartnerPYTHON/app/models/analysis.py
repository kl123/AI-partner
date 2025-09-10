from pydantic import BaseModel
from fastapi import UploadFile

class analysis(BaseModel):
    input: str  # 图片路径
