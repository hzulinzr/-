import scrapy
from scrapy import Request

from Spider.items import PachongItem


class JuZiSpider(scrapy.spiders.Spider):
    name = "dmoz"
    allowed_domains = ["duanwenxue.com"]
    start_urls = ["https://www.duanwenxue.com/duanwen/mingyan/"]
    i = 2

    def parse(self, response):
        for li in response.xpath("//div[@class='list-short-article']/ul/li/p"):
            item = PachongItem()
            item["content"] = li.xpath('a/text()').extract()
            if item["content"][0][-1:] == " ":
                item["content"] = item["content"][0][:-1]
            else:
                item["content"] = item["content"][0]

            yield item

        for i in range(2, 20):
            i = str(i)
            url = "https://www.duanwenxue.com/duanwen/mingyan/list_" + i + ".html"
            yield Request(url, callback=self.parse)
