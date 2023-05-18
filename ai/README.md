# 파이썬 버전

python 3.8.6

# 파이썬 버전 확인

cmd - "python --version"

# local에서 fastapi 실행하는 순서

0. 작업환경 이동

cd ai

1. 가상환경 설치

python -m venv venv

2. 가상환경 실행

source venv/Scripts/activate

3. 패키지 설치(버전 충돌 에러 무시할것)

pip install -r requirements.txt

pip install git+https://git@github.com/SKTBrain/KoBERT.git@master

pip install 'git+https://github.com/SKTBrain/KoBERT.git#egg=kobert_tokenizer&subdirectory=kobert_hf'

pip install transformers==4.10.1

4. 서버 실행
uvicorn main:app --reload

# docs

fastapi에서는 swagger 형식의 docs를 제공

http://127.0.0.1:8000/docs