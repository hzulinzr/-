#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
@author: nl8590687
用于训练语音识别系统语音模型的程序

"""
import platform as plat
import os

import tensorflow as tf
from keras.backend.tensorflow_backend import set_session


from SpeechModel251 import ModelSpeech

os.environ["CUDA_VISIBLE_DEVICES"] = "0"
#进行配置，使用95%的GPU
config = tf.ConfigProto()
config.gpu_options.allow_growth = True
#config.gpu_options.allow_growth=True   #不全部占满显存, 按需分配
set_session(tf.Session(config=config))


datapath = ''
modelpath = 'model_speech'


if(not os.path.exists(modelpath)): # 判断保存模型的目录是否存在
	os.makedirs(modelpath) # 如果不存在，就新建一个，避免之后保存模型的时候炸掉

system_type = plat.system() # 由于不同的系统的文件路径表示不一样，需要进行判断
if(system_type == 'Windows'):
	datapath = 'I:\\python_speech_file'
	modelpath = modelpath + '\\'
elif(system_type == 'Linux'):
	datapath = 'dataset'
	modelpath = modelpath + '/'
else:
	print('*[Message] Unknown System\n')
	datapath = 'dataset'
	modelpath = modelpath + '/'

ms = ModelSpeech(datapath)
ms.LoadModel(modelpath + 'm251\\speech_model251_e_0_step_71500.model')
#ms.TrainModel(datapath, epoch = 50, batch_size = 3, save_step = 500)
ms.TestModel(datapath, str_dataset='dev', data_count = 1024 ,out_report=True)
#ms.TestModel(datapath, str_dataset='dev', data_count = 128)


