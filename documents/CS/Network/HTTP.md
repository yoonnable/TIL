# HTTP

### 서버 클라이언트 구조
* 개인 PC에서 브라우저를 켜고 naver URL을 입력하면 네이버 화면이 뜬다. 
여기서 
    * '개인 PC', '브라우저' = 클라이언트
    * URL을 입력하고 엔터를 눌렀을 때 발생하는 일들
        1. naver 서버에서 인터넷 망을 통해 요청을 받음
        2. 데이터를 만들어서 다시 인터넷 망을 통해 응답
        3. 응답을 통해 개인 PC에 화면이 뜸
    * 위 과정이 서버와 클라이언트가 데이터를 요청하고 받는 구조임
* 서버 컴퓨터 : 간단하게 하루 24시간 내내 켜져있는 컴퓨터라고 생각하면 됨.

=> 서버-클라이언트 모델 프로토콜 = 웹 브라우저가 서버와 통신하는 규칙

## HTTP 요청
```
GET /index.html HTTP/1.1
Host: example.com
User-Agent: Mozilla/5.0
Accept-Language: ko-KR
```
## HTTP 응답
```
HTTP/1.1 200 OK
Date: Sat, 09 Oct 2024 18:00:23 GMT
Server: Apache
Content-Type: text/html

<html>
...
<html>
```

## 백엔드 개발자가 고민해야 할 3가지
1. 정적 리소스를 어떻게 제공할지
2. 동적 HTML 페이지을 어떻게 제공할지
3. HTTP API를 어떻게 제공할지

## HTTP API
* HTML이 아니라 데이터를 전달한다.
* 주로 JSON 형식을 사용해서 데이터를 전달한다.
* 다양한 시스템에서 호출한다.
    * 필요한 데이터만을 보냄
    * UI 화면이 필요하면, 클라이언트가 별도 처리
    * 앱, 웹 클라이언트, 서버 to **서버(API 서버)**



### Reference
* https://docs.tosspayments.com/resources/glossary/http-protocol