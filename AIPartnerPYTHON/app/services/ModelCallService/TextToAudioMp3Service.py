# -*- coding: utf-8 -*-
# @Project : tob_service
# @Company : ByteDance
# @Time    : 2025/7/10 19:01
# @Author  : SiNian
# @FileName: TTSv3HttpDemo.py
# @IDE: PyCharm
# @Motto：  I,with no mountain to rely on,am the mountain myself.
import requests
import json
import base64
import os
from app.core.oss.OssUtils import OssUtils

# python版本：==3.11

# -------------客户需要填写的参数----------------
appID = "6383384267"
accessKey = "2holRpaoJMEStqLHrlS_cGW50TzVwdKK"
resourceID = "volc.service_type.10029"
# text = "这是一段测试文本，用于测试字节大模型语音合成http单向流式接口效果。"
# ---------------请求地址----------------------
url = "https://openspeech.bytedance.com/api/v3/tts/unidirectional"


def tts_http_stream(url, headers, params):
    session = requests.Session()
    # try:
    print('请求的url:', url)
    print('请求的headers:', headers)
    print('请求的params:\n', params)
    response = session.post(url, headers=headers, json=params, stream=True)
    print(response)
    # 打印response headers
    print(f"code: {response.status_code} header: {response.headers}")
    logid = response.headers.get('X-Tt-Logid')
    print(f"X-Tt-Logid: {logid}")

    # 添加打印响应体内容以便调试403错误
    if response.status_code != 200:
        try:
            error_response = response.json()
            print(f"错误响应内容: {error_response}")
        except:
            print(f"无法解析响应内容: {response.text}")

    # 用于存储音频数据
    audio_data = bytearray()
    total_audio_size = 0
    for chunk in response.iter_lines(decode_unicode=True):
        if not chunk:
            continue
        data = json.loads(chunk)

        if data.get("code", 0) == 0 and "data" in data and data["data"]:
            chunk_audio = base64.b64decode(data["data"])
            audio_size = len(chunk_audio)
            total_audio_size += audio_size
            audio_data.extend(chunk_audio)
            continue
        if data.get("code", 0) == 0 and "sentence" in data and data["sentence"]:
            print("sentence_data:", data)
            continue
        if data.get("code", 0) == 20000000:
            break
        if data.get("code", 0) > 0:
            print(f"error response:{data}")
            break
    
    print(f"音频数据大小: {len(audio_data) / 1024:.2f} KB")
    
    # 返回音频字节流数据
    return bytes(audio_data) if audio_data else None

    # except Exception as e:
    #     print(f"请求失败: {e}")
    #     return None
    # finally:
    #     response.close()
    #     session.close()


def textToAudioMp3(text: str) -> str:
    # ---------------请求地址----------------------
    headers = {
        "X-Api-App-Id": appID,
        "X-Api-Access-Key": accessKey,
        "X-Api-Resource-Id": resourceID,
        "X-Api-App-Key": "aGjiRDfUWi",
        "Content-Type": "application/json",
        "Connection": "keep-alive"
    }

    payload = {
        "user": {
            "uid": "12345"
        },
        "req_params": {
            "text": text,
            "speaker": "zh_female_roumeinvyou_emo_v2_mars_bigtts",
            "audio_params": {
                "format": "mp3",
                "sample_rate": 24000
            },
        }
    }

    # 获取音频字节流数据
    audio_bytes = tts_http_stream(url=url, headers=headers, params=payload)
    
    if not audio_bytes:
        print("未获取到音频数据，上传失败")
        return None
    
    # 上传到OSS
    oss_utils = OssUtils()
    try:
        # 直接上传字节流到OSS并获取URL
        # 使用原始文件名作为参数
        file_url = oss_utils.upload_file(audio_bytes, original_filename="tts_generated.mp3")
        print(f"文件已上传到OSS，URL: {file_url}")
        
        return file_url
    except Exception as e:
        print(f"上传OSS失败: {e}")
        # 如果上传失败，返回None或抛出异常
        return None