from pydantic import BaseModel


class AIEmotion(BaseModel):
    emotion: str