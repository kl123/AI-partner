import requests

def send_post_request(url: str, headers: dict = None, data: dict = None, json: dict = None, timeout: int = 10) -> dict:
    """
    发送POST请求到指定URL
    :param url: 请求的URL
    :param headers: 请求头字典
    :param data: 表单数据（会被自动编码为application/x-www-form-urlencoded）
    :param json: JSON数据（会被自动设置Content-Type为application/json）
    :param timeout: 超时时间（秒）
    :return: 包含响应状态码、响应头、响应内容的字典，或包含错误信息的字典
    """
    try:
        response = requests.post(
            url=url,
            headers=headers,
            data=data,
            json=json,
            timeout=timeout
        )
        return {
            "status_code": response.status_code,
            "headers": dict(response.headers),
            "content": response.json() if response.headers.get('Content-Type') == 'application/json' else response.text
        }
    except requests.exceptions.RequestException as e:
        return {
            "error": str(e),
            "status_code": None,
            "headers": None,
            "content": None
        }