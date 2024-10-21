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


