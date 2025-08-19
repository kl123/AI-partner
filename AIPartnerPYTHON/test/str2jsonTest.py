# 当原始数据是字符串时（包含转义字符）
import json

# 如果字符串来自字典的str()转换
original_dict = {"key": "value"}
wrong_str = str(original_dict)  # 会包含单引号，不是合法JSON
correct_str = json.dumps(original_dict)  # 生成合法JSON字符串
