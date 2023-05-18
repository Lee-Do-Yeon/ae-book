from fastapi import FastAPI,File,UploadFile,Form
from fastapi.responses import JSONResponse, StreamingResponse, FileResponse
from review_star_prediction import *
from curse_detection import *
from isbn_ocr import *
from dotenv import load_dotenv
import os
import openai
# import io
import base64
import cv2
import sys
import urllib.request
import requests
import numpy as np
from PIL import Image
from typing import Dict,Any
from starlette.middleware.cors import CORSMiddleware
from io import BytesIO
import datetime
import random
from fastapi.staticfiles import StaticFiles
import json

##setting cors origin
origins = [
    "http://0.0.0.0:3000",
    "http://0.0.0.0",
    "http://localhost:3000"
]

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

#constant
STT_URL = "https://naveropenapi.apigw.ntruss.com/recog/v1/stt?lang=Kor"
TTS_URL = "https://naveropenapi.apigw.ntruss.com/tts-premium/v1/tts"
ADJECTIVE = ["아름다운","이해하기 쉬운","재미있는","슬픈","교훈을 주는","상상력을 자극하는",
             "감동을 주는","어린이가 좋아할 만한","사실적인"]
VOICE = ["vara","vdain","nkyungtae","nminsang"]
REVIEWLENGTH = [10,20,30,40,50,60,70,80,90,100]
STORYLENGTH = [100,110,120,130,140,150]

#api key
load_dotenv()
openai.api_key = os.getenv("SECRET_KEY")
client_id = os.getenv("CLIENT_ID")
client_secret = os.getenv("CLIENT_SECRET")

#setting static files

app = FastAPI()

# static serving "directory sound" 
app.mount("/static", StaticFiles(directory="static"), name="static")

@app.get("/fast")
async def root():
    return {"message":"Hello World"}

#test
#e.g.:http://127.0.0.1:8000/hello/daehyuck
@app.get("/fast/hello/{name}")
async def say_hello(name: str):
    return {"message":f"Hello {name}"}

"""
input: title, words, writer
output: review, star point
"""
@app.post("/fast/reviews/gpt")
async def create_review(data:Dict[Any,Any]):
    
    # {작가}의 {제목} 책을 읽고 {키워드}를 키워드로 해서 서평을 {char} 자 이내로 써줘.
    
    #unpack data
    title = data["title"]
    words = data["keyword"]
    writer = data["writer"]
    length = np.random.choice(REVIEWLENGTH)
    
    curse_recognition = detect_curse(words)
    
    if curse_recognition == 1:
        
        return {"review":'', "star":0, "respond":2}
    
    # message 구성    
    m = f"너는 {writer}의 {title}이라는 책을 읽은 사람이야. 너에 대한 자기소개는 하지 말고 {words}를 키워드로 해서 서평, 키워드라는 말을 쓰지 말고 서평을 {length}자 이내로 써줘"
    
    #chatgpt request
    completion = openai.ChatCompletion.create(
    model="gpt-3.5-turbo",
    messages=[
        {"role": "user", "content": m}
    ]
    )
    
    #chatgpt response
    response = completion.choices[0].message['content']
    
    #predicted star point
    star = predict_star_point(response)
    
    return {"review":response, "star":star, "respond":1}


"""
input:mp3 file(keyword), title
output:review & point prediction
"""
@app.post("/fast/reviews/sound")
async def sound_to_review(audio: UploadFile = File(...), title: str = Form(...), writer: str = Form(default=None)):
    
    #read mp3 file to byte string
    data = await audio.read()
    
    headers = {
        "X-NCP-APIGW-API-KEY-ID": client_id,
        "X-NCP-APIGW-API-KEY": client_secret,
        "Content-Type": "application/octet-stream"
    }
    
    response = requests.post(STT_URL,  data=data, headers=headers)
    rescode = response.status_code
    
    if(rescode == 200):
        
        words = json.loads(response.text)['text'] #stt result
        
        #fail sound recognition
        if words == '':
            
            return {"words": '', "review":'', "star":0, "respond":0}
        
        elif detect_curse(words) == 1:
            
            return {"words": '', "review":'', "star":0, "respond":2}
        
        length = np.random.choice(REVIEWLENGTH)
        
        review,star = create_gpt_review(title,words,writer,length)
        
        return {"words": words, "review":review,"star":star,"respond":1}
    
    else:
        
        return {"words": '', "review":'', "star":0, "respond":3}
    
    
#convert image to sketch
@app.post("/fast/paintings/sketch")
async def image_to_sketch(image: UploadFile = File(...)):
    
    #read image data
    img = await image.read()
    
    #convert image to byte string
    img = np.fromstring(img,dtype=np.uint8)
    
    #read image
    img = cv2.imdecode(img,cv2.IMREAD_COLOR)
    
    #convert the color image to grayscale
    gray_image = cv2.cvtColor(img,cv2.COLOR_BGR2GRAY)
    
    #apply inverted filter
    inverted_image = 255 - gray_image
    
    #apply gaussian blur
    blur = cv2.GaussianBlur(inverted_image,(21,21),0)
    
    #convert blur image to inverted blur
    inv_blur = 255 - blur
    
    #convert it to sketch image
    #sketch can be obtained by performing bit-wise division
    #between grayscale image and inverted-blur image
    sketch = cv2.divide(gray_image, inv_blur, scale = 256.0)
    
    #convert sketch image to binary string
    binary_sketch = cv2.imencode('.jpg',sketch)[1].tobytes()
    
    # encoding byte string to base64
    encoded = base64.b64encode(binary_sketch)
    
    # decoding base64 to utf-8
    decoded = encoded.decode('utf-8')
    
    #return json response
    return JSONResponse(decoded)


"""
input: isbn image
output: isbn string
"""
@app.post("/fast/books/isbn")
async def isbn_detection(image: UploadFile = File(...)):
    
    #read image data
    img = await image.read()
    
    #convert image to byte string
    img = np.fromstring(img,dtype=np.uint8)
    
    #read image
    img = cv2.imdecode(img,cv2.IMREAD_COLOR)
    
    #detect
    result = ocr.ocr(img,cls = True)
    
    #find isbn string
    for idx in range(len(result)):
        res = result[idx]
        for line in res:
            
            data = line[1][0]
            
            #success
            if 'ISBN' in data:
                
                #simple cleansing
                
                #case1 : ISBN 979-11-6050-443-9 (with white space)
                if data[4] == ' ':
                    
                    data = data[5:].replace('-','').strip()
                
                #case2 : ISBN979-11-6050-443-9 (no white space)
                elif data[4] >= '0' and data[4] <= '9':
                    
                    data = data[4:].replace('-','').strip()
                                
                return {"status":1, "data":data} 
    
    #fail
    return {"status":0, "data":""}

"""
input: story keyword
output: chatgpt story, sound
"""
@app.post("/fast/stories/gpt")
async def create_story(text:Dict[Any,Any]):
    
    if detect_curse(text['text']) == 1:
        
        return {'story': '', 'respond':2}
    
    length = np.random.choice(STORYLENGTH)
    
    #chatgpt query
    query = f"너는 동화작가야. 너에 대한 자기소개는 하지 말고 어린이를 위해서 {text['text']}를 소재로 {np.random.choice(ADJECTIVE)} 동화를 {length}자 이내로 만들어줘."
        
    #chatgpt request
    completion = openai.ChatCompletion.create(
    model="gpt-3.5-turbo",
    messages=[
        {"role": "user", "content": query}
    ]
    )
    
    #chatgpt response
    story = completion.choices[0].message['content']
    
    return {'story': story, 'respond':1}

"""
input:text
output:respond code(code = 200(success) & etc.(fail))
"""
@app.post("/fast/stories/sound")
async def text_to_sound(text: Dict[Any,Any]):
    
    text = text['data']
    
    #create data(default)
    encText = urllib.parse.quote(text)
    data = f"speaker={np.random.choice(VOICE)}&volume=-5&speed=0&pitch=0&format=wav&text=" + encText
    
    #header
    request = urllib.request.Request(TTS_URL)
    request.add_header("X-NCP-APIGW-API-KEY-ID",client_id)
    request.add_header("X-NCP-APIGW-API-KEY",client_secret)
    
    #response
    response = urllib.request.urlopen(request, data=data.encode('utf-8'))
    rescode = response.getcode()
    
    if(rescode==200):
        
        response_body = response.read()
        now = datetime.datetime.now()
        
        #random filename
        filename = f"{round(random.random()*1000)}_{now.year}_{now.month}_{now.day}_{now.hour}_{now.minute}_{now.second}_{now.microsecond}.wav"
        
        #save sound file
        with open(f"./static/sound/{filename}",'wb') as f:
            f.write(response_body)
        f.close()
        
        rescode = 1
    
    else:
        
        rescode = 0
        filename = ''
        
    return {'rescode':rescode, 'sound':filename}

# File download
@app.get("/fast/file/download/{filename}")
def download_file(filename: str):
    return FileResponse(path=f"static/sound/{filename}", filename=f"{filename}", media_type="multipart/form-data")