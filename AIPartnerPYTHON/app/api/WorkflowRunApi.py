from fastapi import APIRouter, Depends

from app.models.WorkflowRun.ErrorInsert import ErrorInsertWorkflowRun
from app.models.WorkflowRun.analysis import analysis
from app.models.WorkflowRun.data import dataWorkflowRun
from app.services.WorkflowRunService import WorkflowRunService
from app.models.WorkflowRun.Partern01WorkflowRun import Partern01WorkflowRun
from app.models.WorkflowRun.TestAI import TestAIWorkflowRun
from app.models.WorkflowRun.class_table import classTable
import json

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
    return result

@router.post("/classTable")
async def select_data_workflow_run(
    data: classTable,
    service: WorkflowRunService = Depends(WorkflowRunService)
):
    print(json.loads(data.json()))
    workflow_id = '7545724509002596404'
    result = service.handle_workflow_run(data, workflow_id)

    return result

@router.post("/analysis")
async def analysis_workflow_run(
    data: analysis,
    service: WorkflowRunService = Depends(WorkflowRunService)
):
    print(json.loads(data.json()))
    workflow_id = '7545038641094180899'
    result = service.handle_workflow_run(data, workflow_id)
    return result
