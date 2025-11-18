#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
下载 Qwen2.5-1.5B-Instruct 模型脚本
"""

from modelscope import snapshot_download
import os

def download_qwen_model():
    """下载 Qwen2.5-1.5B-Instruct 模型到指定路径"""
    
    script_dir = os.path.dirname(os.path.abspath(__file__))
    target_dir = script_dir
    
    # 确保目标目录存在
    os.makedirs(target_dir, exist_ok=True)
    
    print(f"开始下载 Qwen2.5-1.5B-Instruct 模型到: {target_dir}")
    print("这可能需要一些时间，请耐心等待...")
    
    try:
        # 下载模型
        model_dir = snapshot_download(
            model_id='Qwen/Qwen2.5-1.5B-Instruct',
            cache_dir=target_dir,
        )
        
        print(f"✅ 模型下载完成！")
        print(f"模型路径: {model_dir}")
        
        # 列出下载的文件
        print("\n📁 下载的文件:")
        for root, dirs, files in os.walk(model_dir):
            level = root.replace(model_dir, '').count(os.sep)
            indent = ' ' * 2 * level
            print(f"{indent}{os.path.basename(root)}/")
            subindent = ' ' * 2 * (level + 1)
            for file in files[:5]:  # 只显示前5个文件
                print(f"{subindent}{file}")
            if len(files) > 5:
                print(f"{subindent}... 还有 {len(files) - 5} 个文件")
        
        return model_dir
        
    except Exception as e:
        print(f"❌ 下载失败: {str(e)}")
        return None

if __name__ == "__main__":
    download_qwen_model()