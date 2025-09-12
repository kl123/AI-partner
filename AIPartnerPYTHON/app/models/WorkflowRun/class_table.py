from pydantic import BaseModel
from fastapi import UploadFile

class classTableFile(BaseModel):
    image: UploadFile  # 使用UploadFile类型来处理文件上传

class classTable(BaseModel):
    image: str  # 图片路径
