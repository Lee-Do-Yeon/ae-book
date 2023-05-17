
# 목차



## 1. 서비스 소개
중고 서적 최저가 검색 뿐만 아니라 <br>
아이와 보호자가 함께 사용하는 도서 컨텐츠 서비스

### 타깃
- 어린 자녀를 둔 보호자<br>
- 아동<br>


### 주요기능
1. Spring Batch를 활용한 최저가 데이터 조회<br>
2. ChatGPT를 통한 서평 작성 도우미<br>
3. 카카오 API를 활용한 최저가 가격 알림 <br>
4. AI를 활용한 선화 추출 <br>
5. 아이를 위한 색칠 공부 <br>
6. 사진으로 도서 검색 <br>


---

## 2. 개발환경


| 소프트웨어 | version | 상세 |
| --- | --- | --- |
| Java SE | JDK8 |  |
| Gradle | - |  |
| Spring boot | 2.7.10 |  |
| Spring Security |  3.0.4  |   |
| Spring Batch | 4.3.3 | |
| JPA | - |  |
| database [mariadb] | 10.11.2 |  |
| Redis | 7.0.10 | 용도 : session 클러스터링 |
| Python | 3.8.6 |  |
| FastAPI | 0.95.1 |  |
| node | 14.21.3 | |



## 3. 배포 환경

### CICD
- AWS EC2
- AWS S3
- Minikube
- Docker, Jenkins, Nginx

### gitignore

application-key.yml<br>
application-db.yml


## 4. 협업

### git 컨벤션

| 커밋유형 | 의미 |
| --- | --- |
| feat | 새로운 기능 추가 |
| fix | 버그 수정 |
| docs | 문서 수정 |
| style | 코드 formatting, 세미콜론 누락, 코드 자체의 변경이 없는 경우 |
| refactor | 코드 리팩토링 |
| test | 테스트 코드, 리팩토링 테스트 코드 추가 |
| chore | 패키지 매니저 수정, 그 외 기타 수정 ex) .gitignore |
| design | CSS 등 사용자 UI 디자인 변경 |
| comment | 필요한 주석 추가 및 변경 |
| rename | 파일 또는 폴더 명을 수정하거나 옮기는 작업만인 경우 |
| remove | 파일을 삭제하는 작업만 수행한 경우 |
| !hotfix | 급하게 치명적인 버그를 고쳐야 하는 경우 |

### git flow
- 메인(dev, release) 브랜치 push 금지 <br>
- dev는 통합개발 브랜치로 사용<br>
- release는 릴리즈 목적으로 사용하되, release-v1.1(1주차), release-v1.2(2주차), release-v1.2.1(2주차 퀵픽스) 사용<br>

- dev 브랜치는 2명, release-v1.x브랜치는 3명의 approve가 있어야 반영하는 것으로 합의<br>
- pr 당 400라인(medium, 200라인은 hard 버전) 이내로 보내기를 권장<br>
- pr에는 반드시 라인 커멘트 달아주기<br>
<br>

![gitflow](./exec/img/gitFlow.gif)


## 5. 외부 기술
1. 카카오 API<br>
- 소셜 로그인<br>
- 알림톡 <br>

2. 알라딘 Open API<br>
- 도서 DB 제공 : 알라딘 인터넷서점(www.aladin.co.kr)<br>

## 6. 서비스 설계

### ERD
![ERD](./exec/img/aebookERD.png)


### API 명세서
![API](./exec/img/apiDocs.PNG)


### 화면 설계서
![화면설계](./exec/img/figma.PNG)

## 7. 사용자 인터페이스

### 메인 페이지
![메인페이지](./exec/img/MainPage.gif)

### 색칠 공부 페이지
![선화 추출 페이지](./exec/img/makePaint.gif)

### 사진으로 책 검색 페이지
![사진으로 도서 검색 페이지](./exec/img/ISBN.gif)

### 제목으로 책 검색 페이지
![책 검색 페이지](./exec/img/SearchBook.gif)

### 최저가 알림 신청 페이지
![최저가 알림 신청 페이지](./exec/img/NotificationRegister.gif)

### 카카오톡 최저가 알림톡 
![카카오톡 최저가 알림](./exec/img/notification_01.jpg)

### ChatGPT 서평 작성 페이지
![ChatGPT 서평 자동 작성 페이지](./exec/img/chatGptReview.gif)

### 음성 인식을 통한 ChatGPT 서평 작성 페이지
![음성 인식 서평 자동 작성 페이지](./exec/img/SoundReview.gif)

### 마이페이지
![마이 페이지](./exec/img/mypage_01.PNG)
