# 扣子智能体文档创建API使用说明

## 接口概述
本接口用于向扣子智能体平台上传文档，支持PDF、TXT、DOC、DOCX等格式的文件。

## 接口地址
```
POST http://localhost:8081/api/coze/document/create
```

## 请求示例

### 使用PDF文件
```json
{
  "document_bases": [
    {
      "name": "高等代数 上 丘维声编着(1).pdf",
      "source_info": {
        "file_base64": "JVBERi0xLjUgDSXi48/TDQoxIDAgb2JqDTw8DS9MZW5ndGggMiAwIFINL1R5cGUgL1hPYmplY3QNL1N1YnR5cGUgL0ltYWdlDS9OYW1lIC9JbTENL1dpZHRoIDk4Mw0vSGVpZ2h0IDE0NDcNL0JpdHNQZXJDb21wb25lbnQgOA0vQ29sb3JTcGFjZSAvRGV2aWNlUkdCDS9GaWx0ZXIgL0RDVERlY29kZSA+Pg1zdHJlYW0K...",
        "file_type": "pdf"
      }
    }
  ],
  "chunk_strategy": {
    "chunk_type": 0
  },
  "format_type": 0
}
```

### 使用文本文件
```json
{
  "document_bases": [
    {
      "name": "example.txt",
      "source_info": {
        "file_base64": "5rWL6K+V5LiA5LiL5ZOm",
        "file_type": "txt"
      }
    }
  ],
  "chunk_strategy": {
    "chunk_type": 0
  },
  "format_type": 0
}
```

## 参数说明

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| document_bases | Array | 是 | 文档基础信息列表 |
| chunk_strategy | Object | 是 | 分块策略 |
| format_type | Integer | 是 | 知识库类型：0-文本类型，2-图片类型 |

### DocumentBase 参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | String | 是 | 文件名称 |
| source_info | Object | 是 | 文件的元数据信息 |

### SourceInfo 参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file_base64 | String | 是 | 本地文件的Base64编码 |
| file_type | String | 是 | 文件格式：pdf、txt、doc、docx |
| document_source | String | 否 | 文档来源 |

### ChunkStrategy 参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| chunk_type | Integer | 是 | 分块类型，固定值为0 |

## 响应格式
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    // 扣子API返回的数据
  }
}
```

## 文件Base64编码方法

### PowerShell方法
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("文件路径"))
```

### Java方法
```java
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

String base64 = Base64.getEncoder().encodeToString(
    Files.readAllBytes(Paths.get("文件路径"))
);
```

## 注意事项
1. 文件大小限制：建议单个文件不超过10MB
2. 支持的文件格式：PDF、TXT、DOC、DOCX
3. Base64编码后的字符串会比原文件大约增加33%的大小
4. 接口已配置跳过JWT验证，可直接调用
5. dataset_id已在代码中固定设置，无需在请求中传递

## 测试命令
```powershell
# 使用PowerShell测试
Invoke-WebRequest -Uri "http://localhost:8081/api/coze/document/create" -Method POST -Headers @{"Content-Type"="application/json"} -Body (Get-Content "test_coze_api.json" -Raw)
```

## 错误处理
- 401 Unauthorized: 扣子API token无效或过期
- 400 Bad Request: 请求参数格式错误
- 500 Internal Server Error: 服务器内部错误