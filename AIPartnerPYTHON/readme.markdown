├── app
│   ├── api
│   │   ├── __init__.py
│   │   └── v1
│   │       ├── __init__.py
│   │       └── endpoints.py
│   ├── core
│   │   ├── __init__.py
│   │   ├── config.py
│   │   ├── database.py
│   │   ├── security.py
│   │   └── utils.py
│   ├── models
│   │   ├── __init__.py
│   │   └── user.py
│   ├── services
│   │   ├── __init__.py
│   │   └── user_service.py
│   ├── __init__.py
│   ├── main.py
│   └── settings.py
└── tests
    ├── __init__.py
    ├── conftest.py
    └── end_to_end
        ├── __init__.py
        └── test_endpoints.py


app 目录下有以下几个子目录和文件（类比Spring Boot分层结构说明）：

### api 目录 - 对应Spring Boot的Controller层
存放API相关代码（如不同版本的路由定义、视图函数），类似Spring Boot中`@RestController`的作用，负责处理HTTP请求和接口响应。

### core 目录 - 对应Spring Boot的Core/Config模块
存放核心代码（配置文件、数据库连接、安全机制、通用工具函数），类似Spring Boot中`@Configuration`类或公共工具类的定位，提供基础能力支持。

### models 目录 - 对应Spring Boot的Entity层
存放数据模型定义（每个模型一个Python文件），类似Spring Boot中`@Entity`注解的实体类，描述数据库表与对象的映射关系。

### services 目录 - 对应Spring Boot的Service层
存放业务逻辑代码（按模型/功能划分），类似Spring Boot中`@Service`类的作用，封装具体的业务规则和逻辑处理。

### main.py - 对应Spring Boot的启动类
FastAPI应用的入口文件（定义启动逻辑），类似Spring Boot中`@SpringBootApplication`注解的主类，负责初始化应用并启动服务。

### settings.py - 对应Spring Boot的application.yml
存放应用配置（如密钥、数据库连接字符串），类似Spring Boot的`application.yml`，集中管理环境变量和参数配置。



tests 目录是用于存放测试代码的目录，按照功能或模块划分子目录。
conftest.py 是存放测试配置和共享的测试数据的文件。

