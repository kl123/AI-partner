from fastapi import APIRouter, Depends
from app.services.WorkflowRunService import WorkflowRunService
from app.models.Partern01WorkflowRun import Partern01WorkflowRun
from app.models.TestAI import TestAIWorkflowRun
import ast

router = APIRouter(prefix="/workflow", tags=["Workflow"])

@router.post("/run")
async def create_workflow_run(data: Partern01WorkflowRun, service: WorkflowRunService = Depends(WorkflowRunService)):
    workflow_id = "7539521480850898998"
    return service.handle_workflow_run(data,workflow_id)


@router.post("/TestAIrun")
async def create_testai_workflow_run(data:TestAIWorkflowRun, service: WorkflowRunService = Depends(WorkflowRunService)):
    workflow_id = '7541655529606316075'
    result = service.handle_workflow_run(data,workflow_id)
    result['data'] = eval(result['data'])
    return result
