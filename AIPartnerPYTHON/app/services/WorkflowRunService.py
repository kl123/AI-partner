from msilib.schema import ServiceControl

from app.models import Partern01WorkflowRun
from app.core.http.httpUtils import send_post_request
from app.core.jwt.jwtUtils import generate_jwt
import json
from typing import TypeVar

T = TypeVar('T')

def run(coze_api_token:str,workflow_id:str,data:T):
    """
    This example describes how to use the workflow interface to chat.
    """

    import os
    # Our official coze sdk for Python [cozepy](https://github.com/coze-dev/coze-py)
    from cozepy import COZE_CN_BASE_URL

    # Get an access_token through personal access token or oauth.
    coze_api_token = f'{coze_api_token}'
    # The default access is api.coze.com, but if you need to access api.coze.cn,
    # please use base_url to configure the api endpoint to access
    coze_api_base = COZE_CN_BASE_URL

    from cozepy import Coze, TokenAuth, Message, ChatStatus, MessageContentType  # noqa

    # Init the Coze client through the access_token.
    coze = Coze(auth=TokenAuth(token=coze_api_token), base_url=coze_api_base)

    # Create a workflow instance in Coze, copy the last number from the web link as the workflow's ID.
    workflow_id = f'{workflow_id}'

    # Call the coze.workflows.runs.create method to create a workflow run. The create method
    # is a non-streaming chat and will return a WorkflowRunResult class.
    print(json.loads(data.json()))
    workflow = coze.workflows.runs.create(
        workflow_id=workflow_id,
        parameters=json.loads(data.json())
    )
    # print("workflow.data", workflow.data)
    return json.loads(workflow.data)

class WorkflowRunService:
    def handle_workflow_run(self, data: T,workflow_id:str) -> Partern01WorkflowRun:
        # 这里添加实际业务逻辑（如调用外部服务、数据处理等）
        jwt = generate_jwt("api.coze.cn","1122791637818","5Cis-hmnZAsHbOG2mDcRmm793QheCzrj2Apj3r_qEUI")
        headers = {
            "Authorization":f"Bearer {jwt}",
            "Content-Type":"application/json"
        }
        url = "https://api.coze.cn/api/permission/oauth2/token"
        json = {
            "grant_type":"urn:ietf:params:oauth:grant-type:jwt-bearer"
        }
        request = send_post_request(url,headers=headers,json=json)
        content = request["content"]
        access_token = content["access_token"]
        return run(access_token,workflow_id,data)

