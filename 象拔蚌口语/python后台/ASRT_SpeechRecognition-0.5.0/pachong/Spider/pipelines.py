# -*- coding: utf-8 -*-

# Define your item pipelines here
#
# Don't forget to add your pipeline to the ITEM_PIPELINES setting
# See: https://docs.scrapy.org/en/latest/topics/item-pipeline.html
from scrapy.exporters import JsonItemExporter


class PachongPipeline(object):
    def __init__(self):
        self.file = open('jz.json', 'wb')
        # 初始化 exporter 实例，执行输出的文件和编码
        self.exporter = JsonItemExporter(self.file, encoding='utf-8', ensure_ascii=False)
        # 开启倒数
        self.exporter.start_exporting()

    def close_spier(self, spider):
        # 可选实现，当spider被关闭时，这个方法被调用
        self.exporter.finish_exporting()
        self.file.close()

    def process_item(self, item, spider):
        print("!!!!!!!!!!!!!!!@@@@")
        self.exporter.export_item(item)
        return item
