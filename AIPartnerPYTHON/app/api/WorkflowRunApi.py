from fastapi import APIRouter, Depends
from app.services.WorkflowRunService import WorkflowRunService
from app.models.WorkflowRun import WorkflowRun

router = APIRouter(prefix="/workflow", tags=["Workflow"])

@router.post("/run")
async def create_workflow_run(data: WorkflowRun, service: WorkflowRunService = Depends(WorkflowRunService)):
    print(data)
    return service.handle_workflow_run(data)
