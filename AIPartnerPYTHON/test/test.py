import requests
from bs4 import BeautifulSoup

def simple_crawler(url):
    try:
        # 发送 HTTP 请求
        response = requests.get(url, headers=headers)
        # 检查响应状态码
        response.raise_for_status()
        # 设置响应内容的编码
        response.encoding = response.apparent_encoding
        # 使用 BeautifulSoup 解析 HTML
        soup = BeautifulSoup(response.text, 'html.parser')
        return soup
    except requests.RequestException as e:
        print(f"请求出错: {e}")
        return None

if __name__ == "__main__":
    # 示例 URL，可替换为你想爬取的网址
    # 搜索数据结构课程的URL（示例为B站搜索结果页）
    target_url = "https://search.bilibili.com/all?keyword=数据结构"
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
    }
    result = simple_crawler(target_url)
    if result:
        print("爬取内容示例（前 500 字符）:")
        # 解析视频列表（需根据B站实际HTML结构调整选择器）
        video_items = result.find_all('div', class_='bili-video-card__info--right')  # 示例选择器
        for item in video_items:
            # 提取视频URL
            link_tag = item.find('a')
            video_url = link_tag['href'] if link_tag else '无URL'
            if "https" not in video_url:
                url = "https:" + video_url
                # 表示当前url视频下去爬取视频简介
                result_introduction = simple_crawler(url)
                video_introduction_item = result_introduction.find_all('div', class_='basic-desc-info')
                video_introduction = video_introduction_item[0].text
                print(f"\033[31m视频url: {url}\033[0m")  # 红色文本
                print(f"\033[32m视频简介: {video_introduction}\033[0m")  # 绿色文本
                print("--------------------------------------------------")
            
