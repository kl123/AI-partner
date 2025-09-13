from fastapi import APIRouter, Depends, UploadFile, File

# 导入OSS工具类
from app.core.oss.OssUtils import OssUtils
from app.models.ModelCall.AIemotion import AIEmotion
from app.models.ModelCall.TextToAudio import TextToAudio
from app.services.ModelCallService.AIEmotionService import AIEmotionService
from app.services.ModelCallService.AudioToTextService import ModelCallService, call_model
from app.services.ModelCallService.TextToAudioMp3Service import textToAudioMp3

router = APIRouter(prefix="/modelCall", tags=["ModelCall"])

@router.post("/AudioToText")
async def audio_to_text_endpoint(
    file: UploadFile = File(...),
    modelCallService: ModelCallService = Depends()
):
    """
    音频转文本接口，同时将音频文件上传到OSS
    
    Args:
        file: 上传的音频文件
        
    Returns:
        dict: 包含上传成功信息和文件URL
        :param file:
        :param modelCallService:
    """
    try:
        # 创建OSS工具类实例
        oss_utils = OssUtils()
        
        # 读取文件内容并上传到OSS
        # 注意：这里我们使用文件内容的字节流进行上传
        content = await file.read()
        file_url = oss_utils.upload_file(content, file.filename)
        print(file_url)
        return call_model(file_url)
        # 可以在这里添加音频转文本的逻辑
        
        # return {
        #     "code": 200,
        #     "message": "文件上传成功",
        #     "data": {
        #         "file_url": file_url,
        #         "filename": file.filename,
        #         "content_type": file.content_type
        #     }
        # }
    except Exception as e:
        return {
            "code": 500,
            "message": f"文件上传失败: {str(e)}",
            "data": None
        }
    finally:
        # 关闭文件
        await file.close()


@router.post("/TextToAudio")
async def text_to_audio_endpoint(data: TextToAudio):
    return TextToAudio(text=textToAudioMp3(data.text))


@router.post("/AIEmotion")
async def ai_emotion_endpoint(data: AIEmotion,modelCallService: AIEmotionService = Depends(AIEmotionService)):
    return modelCallService.ai_emotion(data.emotion)

