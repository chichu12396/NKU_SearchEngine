import scrapy

class NankaiSpiderItem(scrapy.Item):
    url = scrapy.Field()
    title = scrapy.Field()
    date = scrapy.Field()
    source = scrapy.Field()
    content = scrapy.Field()
    anchor_texts = scrapy.Field()
    file_type = scrapy.Field()