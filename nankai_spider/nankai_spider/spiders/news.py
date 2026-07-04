import scrapy
from nankai_spider.items import NankaiSpiderItem
import re

class NankaiAllSpider(scrapy.Spider):
    name = "nankai_all" 

    allowed_domains = ["nankai.edu.cn"]
    
    start_urls = [
        "https://www.nankai.edu.cn/",     
        "https://news.nankai.edu.cn/",     
        "https://jwc.nankai.edu.cn/",       
       "https://cc.nankai.edu.cn/",       
        "https://lib.nankai.edu.cn/" ,      
        "https://xgb.nankai.edu.cn/",
        "https://bbs.nankai.edu.cn/",
    "https://my.nankai.edu.cn/",
    "https://online.nankai.edu.cn/",
    "https://xs.nankai.edu.cn/",
    "https://dag.nankai.edu.cn/",
    "https://museum.nankai.edu.cn/",
    "https://hqbzb.nankai.edu.cn/",
    "https://hr.nankai.edu.cn/",
    "https://tw.nankai.edu.cn/",
    "https://tuanwei.nankai.edu.cn/",
    "https://czfw.nankai.edu.cn/",
    "https://cfc.nankai.edu.cn/",
    "https://ccse.nankai.edu.cn/",
    "https://ime.nankai.edu.cn/",
    "https://riem.nankai.edu.cn/",
    "https://japan.nankai.edu.cn/",
    "https://rus.nankai.edu.cn/",
    "https://middleeast.nankai.edu.cn/",
    "https://latin.nankai.edu.cn/",
    "https://iec.nankai.edu.cn/",
    "https://hanyu.nankai.edu.cn/",
    "https://nce.nankai.edu.cn/",
    "https://tvu.nankai.edu.cn/",
    "https://training.nankai.edu.cn/",
    "https://wxb.nankai.edu.cn/",
    "https://kjc.nankai.edu.cn/",
    "https://xc.nankai.edu.cn/",
    "https://xcb.nankai.edu.cn/",
    "https://xgb.nankai.edu.cn/",
    "https://ylzx.nankai.edu.cn/",
    "https://fz.nankai.edu.cn/",
    "https://zcc.nankai.edu.cn/",
    "https://cwc.nankai.edu.cn/",
    "https://audit.nankai.edu.cn/",
     "https://zhu.nankai.edu.cn/",
    "https://marx.nankai.edu.cn/",
    "https://chinese.nankai.edu.cn/",
    "https://art.nankai.edu.cn/",
    "https://music.nankai.edu.cn/",
    "https://dance.nankai.edu.cn/",
    "https://film.nankai.edu.cn/",
    "https://design.nankai.edu.cn/",
    "https://material.nankai.edu.cn/",
    "https://se.nankai.edu.cn/",
    "https://crypto.nankai.edu.cn/",
    "https://eee.nankai.edu.cn/",
    "https://bio.nankai.edu.cn/",
    "https://geo.nankai.edu.cn/",
    "https://psych.nankai.edu.cn/",
    "https://sustech.nankai.edu.cn/",
    "https://bohai.nankai.edu.cn/",
    "https://ydy.nankai.edu.cn/",
    "https://nmri.nankai.edu.cn/",
    "https://ici.nankai.edu.cn/",
    ]

    def parse(self, response):
        item = NankaiSpiderItem()
        item['url'] = response.url

        item['title'] = response.css('title::text').get(default='').strip()

        texts = response.xpath('//body//text()[not(ancestor::script) and not(ancestor::style)]').getall()

        clean_text = " ".join([t.strip() for t in texts if t.strip()])
        item['content'] = clean_text

        item['source'] = "南开校内资源" 
        item['date'] = "" 
        item['file_type'] = 'html'
        item['anchor_texts'] = response.css('a::text').getall()

        if item['title'] and len(item['content']) > 50:
            yield item
        for a_tag in response.css('a'):
            link = a_tag.css('::attr(href)').get(default='')
            text = a_tag.css('::text').get(default='').strip()
            
            if re.search(r'\.(pdf|doc|docx|xls|xlsx)$', link, re.IGNORECASE):
                doc_item = NankaiSpiderItem()
                doc_item['url'] = response.urljoin(link) 
                doc_item['title'] = text if text else "未命名文档"
                doc_item['content'] = "" 
                doc_item['date'] = item['date'] 
                doc_item['source'] = item['source']
                doc_item['anchor_texts'] = []
                
                doc_item['file_type'] = link.split('.')[-1].lower() 
                
                yield doc_item
        all_links = response.css('a::attr(href)').getall()
        for link in all_links:
            if not link.startswith(('javascript:', 'mailto:')) and not re.search(r'\.(pdf|doc|docx|xls|xlsx|rar|zip|jpg|jpeg|png|gif|mp4)$', link, re.IGNORECASE):
                yield response.follow(link, callback=self.parse)