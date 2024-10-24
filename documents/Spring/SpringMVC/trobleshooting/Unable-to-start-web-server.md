# Unable to start web server: 'url-pattern' 지키지 않은 문제
## 문제
* 단순 Servlet 만들어서 학습하는 실습 중 애플리케이션 실행이 안됨
* 문제의 코드
    ```java
    @WebServlet(name = "responseJsonServlet", urlPatterns = "response-json")
    ```
* 에러 메시지
    ```
    Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
2024-10-24T20:17:27.557+09:00 ERROR 1292 --- [servlet] [           main] o.s.boot.SpringApplication               : Application run failed

org.springframework.context.ApplicationContextException: Unable to start web server
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:165) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:619) ~[spring-context-6.1.13.jar:6.1.13]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:456) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:335) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1363) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1352) ~[spring-boot-3.3.4.jar:3.3.4]
	at hello.servlet.ServletApplication.main(ServletApplication.java:12) ~[main/:na]
Caused by: org.springframework.boot.web.server.WebServerException: Unable to start embedded Tomcat
	at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.initialize(TomcatWebServer.java:147) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.<init>(TomcatWebServer.java:107) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.getTomcatWebServer(TomcatServletWebServerFactory.java:516) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.getWebServer(TomcatServletWebServerFactory.java:222) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.createWebServer(ServletWebServerApplicationContext.java:188) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:162) ~[spring-boot-3.3.4.jar:3.3.4]
	... 8 common frames omitted
Caused by: java.lang.IllegalArgumentException: Invalid <url-pattern> [response-json] in servlet mapping
	at org.apache.catalina.core.StandardContext.addServletMappingDecoded(StandardContext.java:2838) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.Context.addServletMappingDecoded(Context.java:882) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.ApplicationServletRegistration.addMapping(ApplicationServletRegistration.java:188) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.springframework.boot.web.servlet.ServletRegistrationBean.configure(ServletRegistrationBean.java:194) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.servlet.ServletRegistrationBean.configure(ServletRegistrationBean.java:51) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.servlet.DynamicRegistrationBean.register(DynamicRegistrationBean.java:124) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.servlet.RegistrationBean.onStartup(RegistrationBean.java:52) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.selfInitialize(ServletWebServerApplicationContext.java:241) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.springframework.boot.web.embedded.tomcat.TomcatStarter.onStartup(TomcatStarter.java:52) ~[spring-boot-3.3.4.jar:3.3.4]
	at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:4412) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:164) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1203) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1193) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264) ~[na:na]
	at org.apache.tomcat.util.threads.InlineExecutorService.execute(InlineExecutorService.java:75) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at java.base/java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:145) ~[na:na]
	at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:749) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.StandardHost.startInternal(StandardHost.java:772) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:164) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1203) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1193) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264) ~[na:na]
	at org.apache.tomcat.util.threads.InlineExecutorService.execute(InlineExecutorService.java:75) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at java.base/java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:145) ~[na:na]
	at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:749) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.StandardEngine.startInternal(StandardEngine.java:203) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:164) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.StandardService.startInternal(StandardService.java:415) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:164) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.core.StandardServer.startInternal(StandardServer.java:870) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:164) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.apache.catalina.startup.Tomcat.start(Tomcat.java:437) ~[tomcat-embed-core-10.1.30.jar:10.1.30]
	at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.initialize(TomcatWebServer.java:128) ~[spring-boot-3.3.4.jar:3.3.4]
	... 13 common frames omitted
    ```
* 원인 파악
에러 메시지에서 
`Caused by: java.lang.IllegalArgumentException: Invalid <url-pattern> [response-json] in servlet mapping`
이 부분을 보면
'url-pattern'에 'response-json'라는 형태는 사용할 수 없다는 것

## 해결
* 오타 수정
    ```java
    @WebServlet(name = "responseJsonServlet", urlPatterns = "/response-json")
    ```

