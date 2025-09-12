import oss2
import uuid
import os

class OssUtils:
    """
    阿里云 OSS 工具类
    """
    def __init__(self):
        # OSS 配置信息
        self.endpoint = "https://oss-cn-hangzhou.aliyuncs.com"
        self.access_key_id = "LTAI5tBrsrPoPUydFRvauVxP"
        self.access_key_secret = "ozuTaaKpIrvdkLclzF5eXtIgm5cB25"
        self.bucket_name = "web-goose"
        
        # 创建OSS客户端
        self.auth = oss2.Auth(self.access_key_id, self.access_key_secret)
        self.bucket = oss2.Bucket(self.auth, self.endpoint, self.bucket_name)
    
    def upload_file(self, file_obj, original_filename=None):
        """
        实现上传文件到OSS
        
        Args:
            file_obj: 文件对象或文件路径
            original_filename: 原始文件名（可选，当file_obj不是文件路径时使用）
            
        Returns:
            str: 上传后的文件URL
        """
        # 处理文件输入
        if isinstance(file_obj, str) and os.path.exists(file_obj):
            # 如果传入的是文件路径
            if not os.path.isfile(file_obj):
                raise IOError(f"Invalid file: {file_obj}")
            file_path = file_obj
            original_filename = original_filename or os.path.basename(file_path)
            
            # 生成唯一文件名
            file_extension = os.path.splitext(original_filename)[1]
            file_name = f"{uuid.uuid4()}{file_extension}"
            
            # 上传文件
            self.bucket.put_object_from_file(file_name, file_path)
        else:
            # 如果传入的是文件对象
            if original_filename is None:
                raise ValueError("original_filename must be provided when file_obj is not a file path")
            
            # 生成唯一文件名
            file_extension = os.path.splitext(original_filename)[1]
            file_name = f"{uuid.uuid4()}{file_extension}"
            
            # 上传文件
            self.bucket.put_object(file_name, file_obj)
        
        # 构建文件访问URL
        url = self._build_file_url(file_name)
        return url
    
    def delete_file(self, file_name):
        """
        实现删除OSS上的文件
        
        Args:
            file_name: OSS上的文件名
        """
        self.bucket.delete_object(file_name)
    
    def _build_file_url(self, file_name):
        """
        构建文件访问URL
        
        Args:
            file_name: OSS上的文件名
            
        Returns:
            str: 文件访问URL
        """
        # 分割endpoint获取协议和域名部分
        protocol, domain = self.endpoint.split('//')
        return f"{protocol}//{self.bucket_name}.{domain}/{file_name}"