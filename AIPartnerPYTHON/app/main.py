from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware  # ✅ 导入 CORS 中间件
from app.api.WorkflowRunApi import router

app = FastAPI()

# ✅ 添加跨域中间件
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],        # 允许所有域名（开发环境可用，生产慎用）
    allow_credentials=False,    # 如果不需要 cookies，设为 False
    allow_methods=["*"],        # 允许所有 HTTP 方法（POST、GET、PUT 等）
    allow_headers=["*"],        # 允许所有请求头
)

# 注册路由
app.include_router(router)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8085)