from fastapi import APIRouter, Depends, UploadFile, File
from fastapi.responses import StreamingResponse
import importlib.util
import os
import json
from datetime import datetime
import asyncio
from pydantic import BaseModel

# 导入OSS工具类
from app.core.oss.OssUtils import OssUtils
from app.models.ModelCall.AIemotion import AIEmotion
from app.models.ModelCall.TextToAudio import TextToAudio
from app.services.ModelCallService.AIEmotionService import AIEmotionService
from app.services.ModelCallService.AudioToTextService import ModelCallService, call_model
from app.services.ModelCallService.TextToAudioMp3Service import textToAudioMp3
from app.core.database import get_db
from app.models.ModelCall.HuanhuanPayload import HuanhuanPayload

router = APIRouter(prefix="/modelCall", tags=["ModelCall"])
def _parse_dt(s: str):
    if not isinstance(s, str) or not s:
        return None
    try:
        dt = datetime.fromisoformat(s.replace("Z", "+00:00"))
        if getattr(dt, "tzinfo", None):
            dt = dt.replace(tzinfo=None)
        return dt
    except Exception:
        pass
    try:
        return datetime.strptime(s, "%Y-%m-%d %H:%M:%S")
    except Exception:
        pass
    try:
        return datetime.strptime(s, "%Y-%m-%d %H:%M")
    except Exception:
        pass
    try:
        return datetime.strptime(s.replace("T", " ").replace("Z", ""), "%Y-%m-%d %H:%M:%S")
    except Exception:
        pass
    try:
        return datetime.strptime(s, "%Y/%m/%d %H:%M:%S")
    except Exception:
        pass
    try:
        return datetime.strptime(s, "%Y/%m/%d %H:%M")
    except Exception:
        return None

def _sanitize_db_text(s: str) -> str:
    try:
        return "".join(ch for ch in s or "" if ord(ch) < 0x10000)
    except Exception:
        return (s or "").encode("utf-8", "ignore").decode("utf-8", "ignore")

_qwen_module = None
_STREAM_QUEUES: dict[str, asyncio.Queue] = {}

def _load_qwen_module():
    global _qwen_module
    if _qwen_module is None:
        base_dir = os.path.dirname(os.path.dirname(__file__))
        inference_path = os.path.join(base_dir, "Online", "training", "06-Qwen2.5-1.5B", "inference.py")
        spec = importlib.util.spec_from_file_location("qwen_inference", inference_path)
        module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(module)
        _qwen_module = module
    return _qwen_module

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
    result = await modelCallService.ai_emotion(data.emotion)
    return {
        "code": 200,
        "message": "success",
        "data":  result
    }

@router.post("/LANStreamReport")
async def lan_stream_report(data: HuanhuanPayload, dev_id: str | None = None, start_time: str | None = None, conn=Depends(get_db)):
    module = _load_qwen_module()
    params = data.dict()
    query = json.dumps(params, ensure_ascii=False)
    chunks = []
    stream_id = None
    if dev_id and start_time:
        stream_id = f"{dev_id}|{start_time}"
        if stream_id not in _STREAM_QUEUES:
            _STREAM_QUEUES[stream_id] = asyncio.Queue(maxsize=1000)
    def mmss(s: int) -> str:
        m = max(s, 0) // 60
        r = max(s, 0) % 60
        return f"{m:02d}:{r:02d}"
    def sse_iter():
        for chunk in module.chat_stream(query):
            chunks.append(chunk)
            if stream_id:
                try:
                    _STREAM_QUEUES[stream_id].put_nowait(chunk)
                except Exception:
                    pass
            yield f"data: {chunk}\n\n"
        markdown = "".join(chunks)
        try:
            start_dt = _parse_dt(start_time)
            if not start_dt:
                start_dt = _parse_dt(str(getattr(data, "timestamp_iso", "")))
            markdown_db = _sanitize_db_text(markdown)
            total_min = float(data.duration_min or 0)
            total_sec = int(total_min * 60)
            phone_sec = int(data.phone_usage_seconds or 0)
            study_sec = max(total_sec - phone_sec, 0)
            sleep_sec = 0
            walk_sec = 0
            attention_sec = 0
            dev_key = dev_id
            monitor_id = None
            if dev_key and start_dt:
                try:
                    try:
                        conn.ping(reconnect=True)
                    except Exception:
                        pass
                    with conn.cursor() as cur:
                        cur.execute(
                            "SELECT id FROM study_monitor WHERE dev_id=%s AND start_time=%s ORDER BY id DESC LIMIT 1",
                            (dev_key, start_dt),
                        )
                        row = cur.fetchone()
                        if not row:
                            cur.execute(
                                "SELECT id FROM study_monitor WHERE dev_id=%s AND ABS(TIMESTAMPDIFF(SECOND, start_time, %s))<=300 ORDER BY ABS(TIMESTAMPDIFF(SECOND, start_time, %s)) ASC, id DESC LIMIT 1",
                                (dev_key, start_dt, start_dt),
                            )
                            row = cur.fetchone()
                        if row:
                            monitor_id = row[0]
                        else:
                            cur.execute(
                                "INSERT INTO study_monitor (total_time, sleep_time, study_time, walk_time, phone_time, attention_time, dev_id, start_time) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
                                (
                                    total_min,
                                    mmss(sleep_sec),
                                    mmss(study_sec),
                                    mmss(walk_sec),
                                    mmss(phone_sec),
                                    mmss(attention_sec),
                                    dev_key,
                                    start_dt,
                                ),
                            )
                            monitor_id = cur.lastrowid
                        try:
                            conn.commit()
                        except Exception:
                            pass
                except Exception:
                    monitor_id = None
            try:
                try:
                    conn.ping(reconnect=True)
                except Exception:
                    pass
                with conn.cursor() as cur:
                    cur.execute(
                        "INSERT INTO study_markdown (stydy_monitor_id, markdown_value) VALUES (%s,%s)",
                        (monitor_id, markdown_db),
                    )
                try:
                    conn.commit()
                except Exception:
                    pass
                print(f"study_markdown inserted: monitor_id={monitor_id}, size={len(markdown_db)}")
            except Exception as db_err:
                print(f"study_markdown insert error: {db_err}")
                yield f"event: error\ndata: DB save failed: {str(db_err)}\n\n"
            if stream_id:
                try:
                    _STREAM_QUEUES[stream_id].put_nowait("__END__")
                except Exception:
                    pass
            print(f"LANStreamReport finished: dev_id={dev_key}, start_time={start_time}, monitor_id={monitor_id}, chars={len(markdown)}")
        except Exception as e:
            yield f"event: error\ndata: {str(e)}\n\n"
    return StreamingResponse(sse_iter(), media_type="text/event-stream")

@router.get("/LANStreamGet")
async def lan_stream_get(dev_id: str, start_time: str):
    sid = f"{dev_id}|{start_time}"
    if sid not in _STREAM_QUEUES:
        _STREAM_QUEUES[sid] = asyncio.Queue(maxsize=1000)
    queue = _STREAM_QUEUES[sid]
    async def agen():
        while True:
            item = await queue.get()
            if item == "__END__":
                print(f"LANStreamGet end: dev_id={dev_id}, start_time={start_time}")
                yield "event: end\ndata: done\n\n"
                break
            yield f"data: {item}\n\n"
    return StreamingResponse(agen(), media_type="text/event-stream")

@router.get("/MarkdownByKey")
async def markdown_by_key(dev_id: str, start_time: str, conn=Depends(get_db)):
    try:
        dt = _parse_dt(start_time)
        if not dt:
            return {"code": 400, "message": "start_time格式错误", "data": None}
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM study_monitor WHERE dev_id=%s AND start_time=%s ORDER BY id DESC LIMIT 1",
                (dev_id, dt),
            )
            row = cur.fetchone()
            if not row:
                cur.execute(
                    "SELECT id FROM study_monitor WHERE dev_id=%s AND ABS(TIMESTAMPDIFF(SECOND, start_time, %s))<=300 ORDER BY ABS(TIMESTAMPDIFF(SECOND, start_time, %s)) ASC, id DESC LIMIT 1",
                    (dev_id, dt, dt),
                )
                row = cur.fetchone()
            if not row:
                return {"code": 404, "message": "记录不存在", "data": None}
            monitor_id = row[0]
        with conn.cursor() as cur:
            cur.execute(
                "SELECT markdown_value FROM study_markdown WHERE stydy_monitor_id=%s ORDER BY id DESC LIMIT 1",
                (monitor_id,),
            )
            mrow = cur.fetchone()
            if not mrow or not (mrow[0] or "").strip():
                return {"code": 204, "message": "报告为空或尚未生成", "data": ""}
            return {"code": 200, "message": "success", "data": mrow[0]}
    except Exception as e:
        return {"code": 500, "message": f"数据库错误: {str(e)}", "data": None}

class MarkdownDirect(BaseModel):
    markdown_value: str | None = None
    duration_min: float | None = None
    phone_usage_seconds: int | None = None

@router.post("/LANStreamBypass")
async def lan_stream_bypass(dev_id: str, start_time: str, data: MarkdownDirect, conn=Depends(get_db)):
    dt = _parse_dt(start_time)
    if not dt:
        return {"code": 400, "message": "start_time格式错误", "data": None}
    total_min = float(data.duration_min or 0)
    total_sec = int(total_min * 60)
    phone_sec = int(data.phone_usage_seconds or 0)
    study_sec = max(total_sec - phone_sec, 0)
    md = data.markdown_value or ""
    monitor_id = None
    try:
        try:
            conn.ping(reconnect=True)
        except Exception:
            pass
        with conn.cursor() as cur:
            cur.execute(
                "SELECT id FROM study_monitor WHERE dev_id=%s AND start_time=%s ORDER BY id DESC LIMIT 1",
                (dev_id, dt),
            )
            row = cur.fetchone()
            if not row:
                cur.execute(
                    "SELECT id FROM study_monitor WHERE dev_id=%s AND ABS(TIMESTAMPDIFF(SECOND, start_time, %s))<=300 ORDER BY ABS(TIMESTAMPDIFF(SECOND, start_time, %s)) ASC, id DESC LIMIT 1",
                    (dev_id, dt, dt),
                )
                row = cur.fetchone()
            if row:
                monitor_id = row[0]
            else:
                with conn.cursor() as cur2:
                    cur2.execute(
                        "INSERT INTO study_monitor (total_time, sleep_time, study_time, walk_time, phone_time, attention_time, dev_id, start_time) VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
                        (
                            total_min,
                            "00:00",
                            f"{study_sec//60:02d}:{study_sec%60:02d}",
                            "00:00",
                            f"{phone_sec//60:02d}:{phone_sec%60:02d}",
                            "00:00",
                            dev_id,
                            dt,
                        ),
                    )
                    monitor_id = cur2.lastrowid
        try:
            conn.commit()
        except Exception:
            pass
        try:
            conn.ping(reconnect=True)
        except Exception:
            pass
        with conn.cursor() as cur:
            cur.execute(
                "INSERT INTO study_markdown (stydy_monitor_id, markdown_value) VALUES (%s,%s)",
                (monitor_id, md),
            )
        try:
            conn.commit()
        except Exception:
            pass
        print(f"LANStreamBypass saved: dev_id={dev_id}, start_time={start_time}, monitor_id={monitor_id}, size={len(md)}")
        return {"code": 200, "message": "success", "data": {"stydy_monitor_id": monitor_id, "markdown_len": len(md)}}
    except Exception as e:
        print(f"LANStreamBypass error: {e}")
        return {"code": 500, "message": f"数据库错误: {str(e)}", "data": None}

