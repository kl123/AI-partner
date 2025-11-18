from fastapi import APIRouter, Depends
from app.core.database import get_db

router = APIRouter(prefix="/db", tags=["DB"])

@router.get("/ping")
async def ping(conn=Depends(get_db)):
    with conn.cursor() as cur:
        cur.execute("SELECT 1")
        row = cur.fetchone()
        return {"code": 200, "data": row[0]}