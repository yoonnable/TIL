# Servlet
* 만약, 0에서 부터 시작해서 웹 애플리케이션 서버를 직접 구현해야 한다면
    1. 서버 TCP/IP 연결 대기, 소켓 연결
    2. HTTP 요청 메시지를 파싱해서 읽기(다 풀어 헤쳐 분할하고 읽기)
    3. 요청 방식, URL이 뭔지 확인하기
    4. HTTP 메시지 본문(body)내용 파싱
        * 내용에 있는 데이터를 사용할 수 있게 파싱
    5. 서버가 해야 할 프로세스 실행
    6. 비즈니스 로직 실행
        * 데이터베이스에 저장 요청
    7. HTTP 응답 메시지 생성 시작
        * HTTP 시작 라인 생성
        * Header 생성
        * 메시지 body에 HTML 입력
    8. TCP/IP에 응답 전달, 소켓 종료
* 위 작업을 모두 구현해야 한다.
* 위 작업 중 **의미있는 비즈니스 로직**은 6번 작업 뿐이다.
* 개발자가 6번만 해도 모든 동작이 구현될 수있게 지원하는 것이 바로 **서블릿**이다.
    * 즉, 서블릿을 지원하는 WAS 서버만 있다면, 개발자는 6번만 작업하면 웹 애플리케이션 서버 하나 뚝딱!

## 서블릿 특징
```java
@WebServlet(name = "helloServlet", urlPatterns = "/hello") // 1
public class HelloServlet extends HttpServlet {
    @Overried
    protected void service(HttpServletRequest request, // 2
     HttpServletResponse respose) { // 3
        // application code...
    }
}
```
1. `urlPattern("/hello")`의 URL이 호출되면 해당 서블릿의 `service()` 메서드가 실행
    * `HttpServlet` 상속 받으면 됨!
2. HTTP 요청 정보를 편리하게 사용할 수 있도록 해주는 `HttpServletRequest`
3. HTTP 응답 정보를 편리하게 제공할 수 있도록 해주는 `HttpServletResponse`
* => 개발자는 [HTTP 스펙](../../CS/Network/HTTP.md)을 매우 편리하게 사용할 수 있다.

## 웹 애플리케이션 동작 과정 (with Servlet)
1. 웹 브라우저에서 `localhost:8080/hello` 요청한다.
2. WAS에서 웹 브라우저에서 보낸 HTTP 요청 메시지를 기반으로 request와 response객체를 생성한다.
3. 서블릿 컨테이너에서 방금 만든 request,resposne객체를 파라미터로 넘겨 `helloServlet.service()` 실행
4. 실행이 마무리 되면, 다시 WAS가 Response객체 정보로 HTTP응답을 생성해서 response객체에 담아서 다시 웹 브라우저에 전송한다.

### 서블릿을 지원하는 WAS = 서블릿 컨테이너

## Servlet Container
* 톰켓처럼 서블릿을 지원하는 WAS를 서블릿 컨테이너라고 한다.
* 서블릿 컨테이너는 서블릿 객체를 생성, 초기화, 호출, 종료하는 생명주기를 관리한다.
* 서블릿 객체는 [**싱글톤**](#1-싱글톤-객체를-하나만-생성해서-공유하여-사옹하는-것-new-안씀)으로 관리
    * 고객의 요청이 올 때마다 계속 객체를 생성하는 것은 비효율
    * 최초 로딩 시점에 서블릿 객체를 미리 만들어두고 재활용
    * 모든 고객 요청은 동일한 서블릿 객체 인스턴스에 접근
    * 공유 변수(멤버 변수) 사용 주의
    * 서블릿 컨테이너 종료시 함께 종료
* JSP도 서블릿으로 변환되어서 사용한다.
* [동시 요청을 위한 멀티 쓰레드](./Multi-threading.md) 처리를 지원한다.(천 명, 만 명이 동시에 접속해도 잘 돌아가는 이유)


## HttpServletRequest
* 서블릿이 개발자 대신 HTTP 요청 메시지를 파싱하는데, 그 결과를 담는 곳이 `HttpServletRequest` 객체이다.
* `HttpServletRequest`를 사용하면 HTTP 요청 메시지를 편하게 조회할 수 있다.
* HTTP 요청 메시지
    ```
    POST /save HTTP/1.1
    Host: localhost:8080
    Content-Type: application/x-www-form-urlencoded

    username=Servlet
    ```
    * Start Line
        * HTTP 메소드, URL, 쿼리 스트링, 스키마, 프로토콜
    * 헤더
        * 헤더조회
    * 바디
        * form 파라미터 형식 조회, message body 데이터 직접 조회

* 부가 기능
    * 임시 저장소 기능
        * 해당 HTTP 요청이 시작부터 끝날 때까지 유지
        * 저장: `request.setAttribute(name, value)`
        * 조회: `request.getAttribute(name)`
    * 세션 관리 기능
        * `request.getSession(create: true)`

* **중요**⭐
    * `HttpServletRequest`, `HttpServletResponse` 이 객체들은 HTTP 요청 메시지, HTTP 응답 메시지를 편리하게 사용하도록 도와주는 객체이므로 깊이있는 이해를 위해서 **HTTP 스펙이 제공하는 요청, 응답 메시지 자체**를 해하고 있어야 한다.


---
##### [1] 싱글톤: 객체를 하나만 생성해서 공유하여 사옹하는 것 (new 안씀)


### Reference
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard