from fastapi import UploadFile, File
from pydantic import BaseModel

class AudioToText(BaseModel):
    audio_data: str  # Base64 encoded audio content
    language: str = 'zh-CN'  # Default to Chinese
    format: str = 'wav'  # Audio format
    sample_rate: int = 16000  # Sampling rate


class AudioToTextRequest(BaseModel):
    file: UploadFile=File(...)