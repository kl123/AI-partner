from fastapi import APIRouter, Depends

from app.models.ErrorInsert import ErrorInsertWorkflowRun
from app.models.data import dataWorkflowRun
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

@router.post("/ErrorInsertRun")
async def add_ErrorInsert_workflow_run(data:ErrorInsertWorkflowRun, service: WorkflowRunService = Depends(WorkflowRunService)):
    workflow_id = '7545716300380651574'
    result = service.handle_workflow_run(data,workflow_id)
    # result['input'] = eval(result['input'])
    return result

@router.post("/data")
async def select_data_workflow_run(data:dataWorkflowRun, service: WorkflowRunService = Depends(WorkflowRunService)):
    workflow_id = '7544411217345069091'
    result = service.handle_workflow_run(data,workflow_id)
    print(result)
    # result['output'] = eval(result['output'])
    return result
