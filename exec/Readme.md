# 포팅매뉴얼

## 🪐 기술 스택 및 배포 환경


### **기타 인프라**
|개요|기술|버전|
|------|---|---|
|개발환경|redis|7.0.11|
|개발환경|mariaDB|10.11.2-MariaDB|

### **Frontend**
|개요|기술|버전|
|------|---|---|
|개발환경|node.js|14.21.3|
|개발환경|npm|9.6.4|
|개발환경|vue/cli|2.9.6|
|개발환경|VScode|1.74.2|

<br>

### **Backend-Spring**
|개요|기술|버전|
|------|---|---|
|빌드도구|gradle|7.6.1|
|개발환경|Java|8|
|개발환경|SpringBoot|2.7.7|
|개발환경|IntelliJ|2022.3.1|
<br>

### **Backend-Batch**
|개요|기술|버전|
|------|---|---|
|개발환경|SpringBatch|2.7.7|
|개발환경|Java|8|
<br>

### **Backend-Fast(ai)**
기타 버전은 /ai/requirments.txt 파일 참고
|개요|기술|버전|
|------|---|---|
|개발환경|python|3.8|
|개발환경|FastAPI|0.95.1|
|개발환경|IntelliJ|2022.3.1|
<br>

### **배포 관련 환경**
|개요|기술|버전|
|------|---|---|
|배포환경|Ubuntu|20.04.5 LTS|
|배포환경|Docker|23.0.1|
|배포환경|Jenkins|2.387.1 LTS|
<br>

## 환경 변수
- 프로젝트 배포에 필요한 환경 변수 파일은 다음을 참조하세요
- [아이북 환경 변수 정보.pdf](아이북_환경_변수_정보.pdf)

## 🔧인프라 사전 설정
서버 실행 전 mariadb 및 redis 설치가 필요합니다.   
도커를 사용한 인프라 설정 시 다음의 명령어를 사용할 수 있습니다.   
도커를 사용하지 않고 로컬로 설치해도 무관합니다.   
서버 실행 시 선택한 방법에 따라 application-db.yml 등 환경 변수를 잘 확인하여 사용하시길 바랍니다.   
docker compose 사용시 env-config_ae-book_network 네트워크가 생성됩니다. 해당 네트워크를 사용하지 않을경우 각 실행코드에서 `--network env-config_ae-book_network` 옵션을 빼고 실행하면 됩니다.
```
docker-compose -f env-config/docker-compose-env.yml up -d
```

## 🐬1. 일반 배포(실행) 방법
<br/>

### Spring 빌드 방법 
docker가 설치되지 않은 환경에서 다음으로 프로젝트를 빌드하고 실행할 수 있습니다. (batch 동일)
``` bash
# 빌드
./gradlew build -x test

# 실행
# java -jar -Dspring.profiles.active=dev build/libs/ae-book-0.0.1-SNAPSHOT.jar
```

### Vue 빌드 방법
``` bash
# 빌드
npm i
npm run build

# 실행
# npm run start
```

### FastAPI 빌드 방법
``` bash
pip install -r requirements.txt
pip install git+https://git@github.com/SKTBrain/KoBERT.git@master
pip install 'git+https://github.com/SKTBrain/KoBERT.git#egg=kobert_tokenizer&subdirectory=kobert_hf'
pip install torch==1.13.1
apt-get update
apt-get -y install libgl1-mesa-glx
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

<br/>

## 🐳2. 배포 with Docker, Jenkins
docker image 이름은 {area}-{framework}-img로 통일   
docker container 이름은 {area}-{framework}로 통일   
이름은 자유롭게 해도 됩니다. docker build 명령어 실행시 docker build name:tag 식으로 태그를 붙일 수 있으며 해당 방식으로 버전관리를 할 수 있습니다.

### Backend-Spring
``` docker
# 빌드
chmod +x backend/gradlew
backend/gradlew -p backend cleanQuerydslSourceDir		
backend/gradlew -p backend build -x test
docker build -t back-spring-img backend/. --no-cache

# 실행
if (docker ps | grep "back-spring"); then docker stop back-spring;
fi
docker run -it -d --rm -p 8082:8082 --name back-spring --network env-config_ae-book_network back-spring-img
```
### Frontend
``` docker
# 빌드
docker build -t front-vue-img frontend/. --no-cache

# 실행
if (docker ps | grep "front-vue"); then docker stop front-vue;
fi
docker run -it -d --rm -p 3000:3000 --name front-vue --network env-config_ae-book_network front-vue-img
```

### Backend-Batch
``` docker
# 빌드
chmod +x batch/gradlew
batch/gradlew -p batch build -x test
docker build -t back-batch-img batch/. --no-cache

# 실행
if (docker ps | grep "back-batch"); then docker stop back-batch;
fi
docker run -it -d --rm -p 8084:8082 --name back-batch --network env-config_ae-book_network back-batch-img 			
```

### Backend-Fast
``` docker
# 빌드
docker build -t back-fast-img ai/. --no-cache

# 실행
if (docker ps | grep "back-fast"); then docker stop back-fast;
fi
docker run -it -d --rm -p 8000:8000 --name back-fast --network env-config_ae-book_network back-fast-img			
```
### Jenkinsfile
위의 명령어를 조건을 나누어 자동으로 실행해준다고 생각하면 됩니다.   
2번 방법으로 배포하려면 주석처리를 전부 제거해주시면 됩니다.

## Nginx Proxy Server SSL 셋팅
HTTPS 접속 설정을 위한 proxy server 셋팅 방법입니다.
nginx 설정에 대한 다음 파일을 필요로 하며, 특정 포트, 특정 경로의 접속만을 허용합니다.

### nginx.conf
```
server {

    if ($host = ae-book.com) {
        return 301 https://$host$request_uri;
    } # managed by Certbot


    listen 80;
    listen [::]:80;
    server_name ae-book.com;
    return 404; # managed by Certbot
}

server {
    
    location / {
        proxy_pass http://localhost:3000;
    }

    listen 443 ssl; # managed by Certbot
    ssl_certificate /etc/letsencrypt/live/j8c205.p.ssafy.io/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/j8c205.p.ssafy.io/privkey.pem; # managed by Certbot
    # include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    # ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot

    error_page 500 502 503 504 /50.x.html;

    location = /50x.html {
        root /usr/share/nginx/html;
    }
}

```

위의 설정을 적용하기 위한 nginx 실행 명령은 다음과 같습니다.

```
# nginx 로컬 환경에 설치
sudo apt-get install nginx # nginx/1.18.0 (Ubuntu)
sudo systemctl stop nginx

sudo apt-get install letsencrypt
sudo letsencrypt certonly --standalone -d [도메인]
# 이메일 입력
# 서비스 이용 동의
# 정보 수집 동의 (선택)

# 발급받은 키 확인
cd /etc/letsencrypt/live/[도메인]
ls

# Nginx 설정 파일 작성
cd /etc/nginx/sites-availabe
sudo vim [파일명].conf
sudo ln -s /etc/nginx/sites-available/nginx.conf /etc/nginx/sites-enabled/nginx.conf

# 설정 파일 에러 없는지 확인
sudo nginx -t
# 설정 파일 반영하여 재시작
sudo systemctl restart nginx

```

## 🦖3. 배포 with Docker, Jenkins, Kubernetes
쿠버네티스를 사용하기 위해서는 scheduler, controller, api-server, etcd, kubelet, kube-proxy를 설치해야하며 필요에 따라 dns, ingress controller, storage class등을 설치해야 합니다. 
우리 프로젝트에서는 빠른 사용을 위한 도구 minikube를 사용합니다.
### minikube 설치
```
# install minikube
curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 \
  && chmod +x minikube
sudo mkdir -p /usr/local/bin/
sudo install minikube /usr/local/bin/

# 가상머신 시작
minikube start --driver=docker --kubernetes-version=v1.23.1 --memory 8192 --cpus 2

# 접속 ip 확인
minikube ip
123.456.78.9

# install kubectl
curl -LO https://storage.googleapis.com/kubernetes-release/release/v1.23.1/bin/linux/amd64/kubectl
chmod +x ./kubectl
sudo mv ./kubectl /usr/local/bin/kubectl
```
### 실행
각 서버를 실행할 파드에 대한 정보는 env-config/kubernetes 폴더 안에 있습니다.   
`kubectl apply` 혹은 `kubectl delete` 실행 명령어는 각 yml이 파일이 위치한 곳에서 실행해야합니다.   
**주의 spring, fast 프로젝트가 재 빌드, 재실행되면 front 서버또한 재시작 되어야한다.(재빌드는 필요없다)**
```
# front nodeport를 접근하기 위한 로드 밸런서 실행. 없으면 접근을 못함.
# 초기 실행 한번만 실행하면 됩니다.
kubectl apply -f loadbalancer-k8s.yml

# minikube에서 돌릴 docker image를 로컬 환경에서 빌드하기 위함
eval $(minikube -p minikube docker-env)

docker build -t front-vue-img frontend/. --no-cache
docker build -t back-spring-img backend/. --no-cache
docker build -t back-batch-img batch/. --no-cache
docker build -t back-fast-img ai/. --no-cache


# 파드 실행
kubectl apply -f back-spring-k8s.yml
kubectl apply -f back-fast-k8s.yml
kubectl apply -f front-vue-k8s.yml
kubectl apply -f back-batch-k8s.yml

# 파드 삭제 (만약 재실행하려는 경우 삭제 후 실행해주어야함)
kubectl delete -f back-spring-k8s.yml
kubectl delete -f back-fast-k8s.yml
kubectl delete -f front-vue-k8s.yml
kubectl delete -f back-batch-k8s.yml
```

### Jenkinsfile
역시 위의 명령어를 조건에 따라 실행시키도록 로직을 작성해주면 됩니다.


## Nginx Proxy Server SSL 셋팅 for kubernetes
하단에 설정된 아이피 `123.456.78.9`는 다음 명령어 `minikube ip`를 입력했을 때 볼 수 있습니다.
뒤에 따라오는 30000포트는 3000으로 띄워진 front 파드에 연결된 로드밸런서의 포트번호 입니다.   
자세한건 어렵고.. 여러 서버가 띄워진 minikube에 접근하기 위해서라고 생각하시면 됩니다. (이거 안해주면 접근 못함)   
nginx 설정은 2번 방법에 작성된 것과 같습니다.
### nginx.conf
```
server {

    if ($host = ae-book.com) {
        return 301 https://$host$request_uri;
    } # managed by Certbot


    listen 80;
    listen [::]:80;
    server_name ae-book.com;
    return 404; # managed by Certbot
}

server {
    client_max_body_size 5M;

    location / {
        proxy_pass http://123.456.78.9:30000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_updrage;
        proxy_set_header Connection "Upgrade";
        proxy_set_header Host $host;
    }

    location /sockjs-node {
        proxy_pass http://123.456.78.9:30000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    listen 443 ssl; # managed by Certbot
    ssl_certificate /etc/letsencrypt/live/ae-book.com/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/ae-book.com/privkey.pem; # managed by Certbot
    # include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    # ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; # managed by Certbot

    error_page 500 502 503 504 /50.x.html;

    location = /50x.html {
        root /usr/share/nginx/html;
    }
}
```