# JAR와 WAR
* 둘 다 Java의 jar 옵션 (java -jar)을 이용해 생성된 압축(아카이브) 파일로, 애플리케이션을 쉽게 배포하고 동작시킬 수 있도록 관련 파일(리소스, 속성 파일 등)을 패키징 한 것이다.
## JAR (Java Archive)
* 독립적인 JAVA 어플리케이션이 동작할 수 있도록 자바 프로젝트를 압축한 파일
* Class (JAVA리소스, 속성 파일), 라이브러리 파일을 포함하여 배포가 용이하다
* main메서드를 포함하고 있으므로 JRE(JAVA Runtime Environment)만 있어도 실행 가능함 (java -jar 프로젝트네임.jar)
* JSP나 서블릿 컨테이너에 대한 표준 기능을 활용하기 어렵다.

## WAS (Web Application Archive)
* Servlet / Jsp 컨테이너에 배치할 수 있는 Java 웹 애플리케이션(Web Application) 압축파일 포맷
* 웹 관련 자원을 포함함 (JSP, Servlet, JAR, Class, XML, HTML, Javascript)
* 사전 정의된 구조를 사용함 (WEB-INF, META-INF)
* 별도의 웹서버(WEB) or 웹 컨테이너(WAS) 필요
* 즉, JAR파일의 일종으로 웹 애플리케이션 전체를 패키징 하기 위한 JAR 파일이다.


## 선택 시 고려사항
* 프로젝트가 독립적인 애플리케이션인 경우 JAR 파일이 적합하고, 웹 애플리케이션인 경우 WAR 파일이 적합하다.
* 독립적인 애플리케이션을 서버나 웹 컨테이너 없이 실행해야 한다면 JAR 파일을 선택하고, 웹 애플리케이션을 실행하기 위해 특정한 웹 컨테이너(예: 톰캣, 제티)에서 호스팅해야 한다면 WAR 파일을 선택한다.
* JAR 파일은 일반적인 Java 애플리케이션에 적합하며, 독립 실행 가능한 형태로 개발자가 애플리케이션을 실행하고 테스트할 수 있다. 반면 WAR 파일은 웹 애플리케이션의 웹 구성 요소(서블릿, JSP 등)을 포함하므로, 웹 애플리케이션 개발에 필요한 기능을 활용할 수 있다.
* Sprinag boot에서 가이드하는 표준은 JAR이다.
    * 내장 톰캣 보유(Spring Web)
    * JSP를 사용하지 않음

> JAR와 WAR 파일은 서로 상호 배타적 않아 동일한 프로젝트에서 JAR 파일과 WAR 파일을 함께 사용할 수 있다. 예를 들어, Java 애플리케이션의 기능을 포함한 WAR 파일을 만들거나, JAR 파일을 웹 애플리케이션의 클래스 경로에 추가하여 사용할 수 있다.


### Reference
https://cocococo.tistory.com/entry/JAR-WAR-%EB%B0%B0%ED%8F%AC-%EC%B0%A8%EC%9D%B4%EC%A0%90%EA%B3%BC-%EC%9E%A5%EB%8B%A8%EC%A0%90
https://velog.io/@mooh2jj/JAR-vs-WAR-%EB%B0%B0%ED%8F%AC%EC%9D%98-%EC%B0%A8%EC%9D%B4