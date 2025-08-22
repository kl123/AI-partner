from pydantic import BaseModel

class VideoCrawler(BaseModel):
    video_name: str
    video_url: str
    video_image:str
    video_description: str
    video_author: str
    video_date: str
    video_duration:str
    video_like: str
    video_collection: str
    video_coins: str
    video_forward:str
    video_views: str

