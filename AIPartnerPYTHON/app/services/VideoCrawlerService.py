import requests
from bs4 import BeautifulSoup
from app.models.VideoCrawler.VideoCrawler import VideoCrawler


def simple_crawler(url,headers):
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

def VideoCrawler_run(data,video_crawler_list: list[VideoCrawler]):
    # 示例 URL，可替换为你想爬取的网址
    # 搜索数据结构课程的URL（示例为B站搜索结果页）
    target_url = "https://search.bilibili.com/all?keyword=" + data["title"]
    headers = {
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"
    }
    result = simple_crawler(target_url,headers)
    if result:
        count = 0
        print("爬取内容示例（前 500 字符）:")
        # 解析视频列表（需根据B站实际HTML结构调整选择器）
        video_items = result.find_all('div', class_='bili-video-card__info--right')  # 示例选择器
        image_items = result.find_all('div',class_="bili-video-card__image")
        for item,image_item in zip(video_items,image_items):
            count += 1
            if count > 5:
                break
            try:
                # 提取视频URL
                link_tag = item.find('a')
                video_url = link_tag['href'] if link_tag else '无URL'
                link_img = image_item.find('img')
                video_image = "https:" + link_img['src'] if link_img else '无图片'
                video_name = link_img['alt'] if link_img else '无名称'
                link_author = item.find('span',class_='bili-video-card__info--author')
                video_author = link_author.text if link_author else '无作者'
                link_date = item.find('span',class_='bili-video-card__info--date')
                video_date = link_date.text if link_date else '无时间'
                link_duration = image_item.find('span',class_='bili-video-card__stats__duration')
                video_duration = link_duration.text if link_duration else '无时长'
                if "https" not in video_url:
                    url = "https:" + video_url
                    # 表示当前url视频下去爬取视频简介
                    result_introduction = simple_crawler(url,headers)
                    video_introduction_item = result_introduction.find_all('div', class_='basic-desc-info')
                    video_introduction = video_introduction_item[0].text
                    video_like_item = result_introduction.find_all('span',class_='video-like-info video-toolbar-item-text')
                    video_like = video_like_item[0].text
                    video_coins_item = result_introduction.find_all('span',class_='video-coin-info video-toolbar-item-text')
                    video_coins = video_coins_item[0].text
                    video_collection_item = result_introduction.find_all('span',class_='video-fav-info video-toolbar-item-text')
                    video_collection = video_collection_item[0].text
                    video_forward_item = result_introduction.find_all('span',class_='video-share-info video-toolbar-item-text')
                    video_forward = video_forward_item[0].text
                    view_views_item = result_introduction.find_all('div',class_='view-text')
                    view_views = view_views_item[0].text
                    # print(f"\033[31m视频url: {url}\033[0m")  # 红色文本
                    # print(f"\033[32m视频简介: {video_introduction}\033[0m")  # 绿色文本
                    # print(f"\033[33m视频名称: {video_name}\033[0m")  # 黄色文本
                    # print(f"\033[34m视频图片: {video_image}\033[0m")  # 蓝色文本
                    # print(f"\033[35m视频作者: {video_author}\033[0m")  # 紫色文本
                    # print(f"\033[36m视频时间: {video_date}\033[0m")  # 青色文本
                    # print(f"\033[37m视频时长: {video_duration}\033[0m")  # 白色文本
                    # print(f"\033[31m视频点赞: {video_like}\033[0m")  # 红色文本
                    # print(f"\033[32m视频收藏: {video_collection}\033[0m")  # 绿色文本
                    # print(f"\033[33m视频转发: {video_forward}\033[0m")  # 黄色文本
                    # print(f"\033[34m视频硬币: {video_coins}\033[0m")  # 蓝色文本
                    # print(f"\033[35m视频播放: {view_views}\033[0m")  # 紫色文本
                    # print("--------------------------------------------------")
                    video_crawler = VideoCrawler(
                        video_name=video_name,
                        video_url=url,
                        video_image=video_image,
                        video_description=video_introduction,
                        video_author=video_author,
                        video_date=video_date,
                        video_duration=video_duration,
                        video_like=video_like,
                        video_collection=video_collection,
                        video_coins=video_coins,
                        video_forward=video_forward,
                        video_views=view_views
                    )
                    video_crawler_list.append(video_crawler)
            except:
                continue


        return video_crawler_list


class VideoCrawlerService:
    def handle_video_crawler(self, data: dict,video_crawler_list: list[VideoCrawler]):
        video_crawler_list = VideoCrawler_run(data,video_crawler_list)
        return video_crawler_list
