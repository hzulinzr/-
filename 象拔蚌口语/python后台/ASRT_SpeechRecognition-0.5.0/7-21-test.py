from pypinyin import lazy_pinyin,Style
import xpinyin

class test:

    def get_wav_list(filename: str):
        '''
        读取一个wav文件列表，返回一个存储该列表的字典类型值
        ps:在数据中专门有几个文件用于存放用于训练、验证和测试的wav文件列表
        '''
        txt_obj=open(filename,'r',encoding='utf-8') # 打开文件并读入
        txt_text=txt_obj.read()
        txt_lines=txt_text.split('\n') # 文本分割
        dic_filelist={} # 初始化字典
        list_wavmark=[] # 初始化wav列表
        for i in txt_lines:
            print(i)
            if(i!=''):
                txt_l=i.split('	')
                dic_filelist[txt_l[0]] = txt_l[1]
                list_wavmark.append(txt_l[0])
        txt_obj.close()
        return dic_filelist,list_wavmark


    #style = Style.TONE3


    s = xpinyin.Pinyin()
    data_path = "D:\\AISHELL-2-sample\\iOS\\data\\trans.txt"
    dict_wav,list_wav = get_wav_list(data_path)

    dict_new_wav = dict()
    content = ""
    for i in list_wav:
        pinyin_content = s.get_pinyin(dict_wav[i]," ",tone_marks="numbers")
        content = f"{content}\n{i} {pinyin_content}"
        #dict_new_wav[i] = " ".join(lazy_pinyin(dict_wav[i], style=style))

    print(content)
    f = open("D:\\AISHELL-2-sample\\iOS\\data\\syllable.txt",'w')
    f.write(content)



s = xpinyin.Pinyin()
pinyin = s.get_pinyin("撒旦的"," ",tone_marks="numbers")
print(pinyin)
