# 서블릿 실습
* 서블릿 실습의 전체적인 내용을 적은 것이 아니라, 실습 하면서 뭔가 까먹을 것 같다..! 나중에 똑같은 작업을 하려면 또 다시 찾아봐야 할 것 같다..! 싶은 내용만 적음
## 1. 프로젝트 생성
1. war 선택
2. ~~intelliJ에서 빌드 방식 설정 (속도가 빠름)~~
    * ~~File > Settings... > Build, Execution, Deployment > Build Tools > Gradle~~
    * ~~Build and run using : IntelliJ IDEA~~
    * ~~Run test using : IntellJ IDEA~~
* 주의! 스프링 부트 3.2 부터 Gradle 옵션을 선택하자.
스프링 부트 3.2 부터 앞서 Build and run using에 앞서 설명한 IntelliJ IDEA를 선택하면 몇가지 오류가 발생한다.
따라서 스프링 부트 3.2를 사용한다면 다음과 같이 IntelliJ IDEA가 아니라 Gradle을 선택해야 한다
3. lombok 플러그인 설치 후 어노테이션 설정
    * File > Setting.. > plugin > "lombok"검색 후 설치
    * File > Setting.. > Build > Complier > Annotation Processors
    * Enable annotation processing 체크


## 2. Hello 서블릿
```java
package hello.servlet.basic;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "helloServlet", urlPatterns = "/hello")
public class HelloServlet extends HttpServlet {

  // 이 서블릿이 호출된다는 것은 이 service 메소드가 실행된 다는 것!!!
  // 접근제한자 protected인 것으로 오버라이드 할 것
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    System.out.println("HelloServlet.service");
    System.out.println("request = " + request);
    System.out.println("response = " + response);

    // 요청 url : /hello?username=kim
    String username = request.getParameter("username"); // 쿼리 파라미터 값 쉽게 가져오기
    System.out.println("username = " + username);


    response.setContentType("text/plain"); // 단순 문자열 타입의 응답 값을 보냄 (헤더에 담김)
    response.setCharacterEncoding("utf-8"); // 문자 인코딩 (헤더에 담김)
    response.getWriter().write("hello " + username); // HTTP 응답 메시지 Body에 들어가는 내용

  }
}

  ```
  * 요청 url : `/hello?username=kim`
  * 로그
    ```console
    HelloServlet.service
    request = org.apache.catalina.connector.RequestFacade@42cb136b
    response = org.apache.catalina.connector.ResponseFacade@157186f4
    username = kim
    ```
    * `org.apache.catalina.connector.RequestFacade@74bce32d` : 매개변수로 받은 `HttpServletRequest` 인터페이스의 구현체
    * `org.apache.catalina.connector.ResponseFacade@30867dc6` : 매개변수로 받은 `HttpServletResponse` 인터페이스의 구현체
    * 실행결과 : 화면에 hello kim이라고 출력됨
* tip : 프로젝트의 모든 요청, 응답 정보 및 내용을 로그로 확인하고 싶을 때
    * application.properties
        ```properties
        logging.level.org.apache.coyote.http11=trace
        ```
    * application.yml
        ```yml
        logging:
            level:
                org.apache.coyote.http11: trace
        ```

## 3. HttpServletRequest
### 1. HTTP 요청 데이터 - GET 쿼리 파라미터
* 이름이 같은 복수 파라미터도 전송이 가능
  ```java
  // http://localhost:8080/request-param?username=Hello&age=20&username=Kim
  System.out.println("이름이 같은 복수 파라미터 조회 - start");
      String[] names = request.getParameterValues("username");
      for (String name : names) {
        System.out.println("name = " + name);
      }
  ```
  * 결과
  ```
  이름이 같은 복수 파라미터 조회 - start
  name = Hello
  name = Kim
  ```
* 중복일 때, `getParameter()`를 사용할 경우 맨 첫번째 값을 반환

### 2. HTTP 요청 데이터 - POST HTML Form
* 테스트 시 HTML form 만들기 귀찮을 땐 Postman 사용

### 3. HTTP 요청 데이터 - API 메시지 바디
#### 1. 단순 텍스트
```java
 @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    ServletInputStream inputStream = request.getInputStream(); // 메시지 바디의 내용을 바이트코드로 얻어온다.
    String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8); // 스프링이 제공하는 유틸 사용. 인코딩 정보 필수
    System.out.println("messageBody = " + messageBody);

    response.getWriter().write("ok");
  }
```
#### 2. JSOM
```java
@WebServlet(name = "requestBodyJsonServlet", urlPatterns = "/request-body-json")
public class RequestBodyJsonServlet extends HttpServlet {

  // 스프링부트가 기본으로 제공하는 JSON변환 라이브러리
  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    ServletInputStream inputStream = request.getInputStream();
    String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

    System.out.println("messageBody = " + messageBody);

    HelloData helloData = objectMapper.readValue(messageBody, HelloData.class); // 자동 역직렬화

    System.out.println("helloData.getUsername() = " + helloData.getUsername());
    System.out.println("helloData.getAge() = " + helloData.getAge());

    response.getWriter().write("ok");
  }
}
```
* 결과
  ```
  messageBody = {"username":"hello", "age":20}
  helloData.getUsername() = hello
  helloData.getAge() = 20
  ```

## 4. HttpServletResponse
* 응답 헤더 기본 셋팅
  ```java
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // [status-line]
    response.setStatus(HttpServletResponse.SC_OK);

    // [response-headers]
    response.setHeader("Content-Type", "text/plain;charset=utf-8"); // 한글이 깨지지 않게 하기 위한 인코딩까지!
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // 캐시 완전 무효화
    response.setHeader("Pragma", "no-cache"); // 과거의 캐시 삭제
    response.setHeader("my-header", "hello"); // 사용자화

    // [message body]
    PrintWriter writer = response.getWriter();
    writer.println("ok. 안녕?");
  }
  ```
  * 결과 화면
  <br/>
  ![alt text](/documents/img/spring/springmvc/practice-servlet/image.png)
  <br/>
  ![alt text](/documents/img/spring/springmvc/practice-servlet/image-1.png)

* 응답 코드 `response.setStatus(HttpServletResponse.SC_BAD_REQUEST);`로 설정
  * 결과
  <br/>
    ![alt text](/documents/img/spring/springmvc/practice-servlet/image-2.png)

* 편의 메서드 사용
  ```java
  // content 편의 메서드
  private void content(HttpServletResponse response) {
    //Content-Type: text/plain;charset=utf-8
    //Content-Length: 2
    //response.setHeader("Content-Type", "text/plain;charset=utf-8");
    response.setContentType("text/plain");
    response.setCharacterEncoding("utf-8");
    //response.setContentLength(2); //(생략시 자동 생성)
  }

  // 쿠키 편의 메서드
  private void cookie(HttpServletResponse response) {
    //Set-Cookie: myCookie=good; Max-Age=600;
    //response.setHeader("Set-Cookie", "myCookie=good; Max-Age=600");
    Cookie cookie = new Cookie("myCookie", "good");
    cookie.setMaxAge(600); //600초
    response.addCookie(cookie);
  }

  // redirect 편의 메서드
  private void redirect(HttpServletResponse response) throws IOException {
    //Status Code 302
    //Location: /basic/hello-form.html
    //response.setStatus(HttpServletResponse.SC_FOUND); //302
    //response.setHeader("Location", "/basic/hello-form.html");
    response.sendRedirect("/basic/hello-form.html");
  }
  ```

* 참고로 중복 셋팅 하면 나중에 셋팅된 값이 적용된다.


### 1. HTTP 응답 데이터 - 단순 텍스트
```java
// [message body]
PrintWriter writer = response.getWriter();
writer.println("ok. 안녕?");
```
### 2. HTTP 응답 데이터 - HTML
* HTTP 응답으로 HTML을 반환할 때는 content-type을 text/html 로 지정해야 함
```java
response.setContentType("text/html");
response.setCharacterEncoding("utf-8");
```

### HTTP 응답 데이터 - API JSON
```java
@WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
public class ResponseJsonServlet extends HttpServlet {

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");

    HelloData helloData = new HelloData();
    helloData.setUsername("Hello Servlet");
    helloData.setAge(20);

    // {"username":"hello", "age":20} 로 직렬화 작업 필요
    String result = objectMapper.writeValueAsString(helloData);
    response.getWriter().write(result);

  }
}
```







### Reference
https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-mvc-1/dashboard

