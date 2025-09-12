from pydantic import BaseModel


class TextToAudio(BaseModel):
    text: str
