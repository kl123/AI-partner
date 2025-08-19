# You must run `pip install PyJWT cryptography` to install the PyJWT and the cryptography packages in order to use this script.

#!/usr/bin/env python3
import sys
import time
import uuid

import jwt


# ... existing code ...

def generate_jwt(aud:str,iss:str,kid:str):
    # 读取私钥文件内容（修改路径为绝对路径或正确相对路径）
    import os
    # 获取当前脚本所在目录（jwtUtils.py 所在目录）
    script_dir = os.path.dirname(os.path.abspath(__file__))
    private_key_path = os.path.join(script_dir, 'private_key.pem')  # 拼接绝对路径
    with open(private_key_path, 'r') as f:
        signing_key = f.read()
# ... existing code ...

    '''
    -----BEGIN PRIVATE KEY-----
    xxxxxxxxxxxxxxxxxx
    -----END PRIVATE KEY-----
    '''

    payload = {
        'iat': int(time.time()),
        'exp': int(time.time()) + 600,
        "jti": str(uuid.uuid4()),
        'aud': aud,   # 替换为实际的coze api domain
        'iss': iss  # 替换为你的实际 Coze App ID
    }

    headers = {
        'kid': kid  # 替换为你的实际 Coze App 公钥指纹
    }

    # Create JWT with headers
    encoded_jwt = jwt.encode(payload, signing_key, algorithm='RS256', headers=headers)

    print(f"JWT: {encoded_jwt}")

    return encoded_jwt
