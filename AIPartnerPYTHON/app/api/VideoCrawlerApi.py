from fastapi import APIRouter, Depends
from app.services.VideoCrawlerService import VideoCrawlerService
from app.models.VideoCrawler import VideoCrawler

router = APIRouter(prefix="/videoCrawler", tags=["VideoCrawler"])

@router.post("/run")
async def create_workflow_run(data: dict, service: VideoCrawlerService = Depends(VideoCrawlerService)):
    video_crawler_list: list[VideoCrawler] = []
    List = service.handle_video_crawler(data,video_crawler_list)
    return List
