# Spring REST Docs
## Spring REST Docs vs Swagger
* ### Swagger
    #### 장점
    * Annotation 기반으로 문서 생성
    * 쉽다
    * GUI로 API 테스트 가능
    #### 단점
    * Production 코드에 문서화를 위한 코드가 추가됨
    * 검증되지 않은 문서 생성
* ### Spring REST Docs
    * 테스트 코드 기반으로 RESTful 문서생성을 돕는 도구
    * [Asciidoctor](#1-asciidoctor)를 사용하여 HTML를 생성
    * 테스트로 생성된 [snippet](#2-snippet)을 사용해서 정확성을 보장함
    #### 장점
    * 테스트가 성공해야 문서가 생성(실패 시 안만들어짐) -> 검증된 문서
    * Production 코드에 영향 없음
    * api 명세 최신화가 강제
    #### 단점
    * 어렵다
    * 테스트 코드 양이 많아짐

## Spring REST Docs 적용하기 (with Gradle and Junit5)
### 1. 의존성
* Spring Web, Spring REST Docs
```java
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

ext {
	set('snippetsDir', file("build/generated-snippets"))
}

tasks.named('test') {
	outputs.dir snippetsDir
	useJUnitPlatform()
}

tasks.named('asciidoctor') {
	inputs.dir snippetsDir
	dependsOn test
}
```
### 2. 테스트 코드 작성

```Java
package com.example.rest_docs.books;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(RestDocumentationExtension.class) // 1
@WebMvcTest(BookController.class) //2
class BookControllerTest {

  private MockMvc mockMvc;

  // 3
  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(documentationConfiguration(restDocumentation)) // 4
        .build();
  }

  @Test
  void getBook() throws Exception {
    // 5
    mockMvc.perform(get("/books/{id}", 1L).accept(MediaType.APPLICATION_JSON)) // 6
        .andExpect(status().isOk()) // 7
        .andDo(document("get-a-book")); // 8
  }
}
```
1. 문서 snippet을 생성하는 첫 번째 단계는 `RestDocumentationExtension`을 테스트 클래스에 적용한다.
    * `RESTDocumentationExtension`은 프로젝트의 빌드 도구를 기반으로 출력 디렉토리로 자동 구성됨
        * `build/build/generated-snippets` (Maven은 경로 다름 주의)
        ```java
        // build.gradle
        
        ...
        
        ext {
            set('snippetsDir', file("build/generated-snippets"))
        }

        ...
        
        ```
2. `WebApplicationContext`를 테스트 컨텍스트에서 주입하기 위해 테스트 클래스에 Spring 컨텍스트를 로드해야 하는데, 이를 위해 `@SpringBootTest` 또는 `@WebMvcTest` 어노테이션을 사용해야 한다.
여기서는 `MockMvc` 테스트를 진행 중이므로 `@WebMvcTest`를 사용한다. 이 어노테이션은 웹 계층(Controller)만 로드하고, 전체 애플리케이션 컨텍스트는 로드하지 않기 때문에 더 가볍다.
3. 다음으로 `MockMVC`를 구성하는 `@Beforeeach` 메소드를 만든다.
4. `Mockmvc` 인스턴스는 `MockmvcrestDocumentationConfigurer`를 사용하여 구성되는데, 이는 `org.springframework.restdocs.mockmvc.mockmvcrestdocumentation`의 정적 `documentationConfiguration ()` 메소드에서 생성한다.
5. 테스트 프레임 워크를 구성 했으므로 이를 사용하여 나머지 서비스를 호출하고 요청 및 응답을 문서화 할 수 있다.
6. 서비스의 `/books/1`를 호출하고, `application/JSON` 응답이 필요하다는 것을 나타낸다.
7. 서비스가 예상 응답을 생성했다고 주장한다.
8.  **snippet을 `get-a-book` (구성된 출력 디렉토리 아래에 위치)라는 디렉토리로 작성하여 서비스에 대한 호출을 문서화해준다.**
    * snippet은 `RestDocumentationResulthandler`가 작성하는데, `org.springframework.restdocs.mockmvc.mockmvcrestdocumentation`의 정적 `document ()` 메소드에서 생성한다.
    * 기본적으로 6개의 snippet이 작성된다.
        * `<output-directory>/get-a-book/curl-request.adoc`
        * `<output-directory>/get-a-book/http-request.adoc`
        * `<output-directory>/get-a-book/http-response.adoc`
        * `<output-directory>/get-a-book/httpie-request.adoc`
        * `<output-directory>/get-a-book/request-body.adoc`
        * `<output-directory>/get-a-book/response-body.adoc`
### 3. 테스트 실행
* 테스트가 성공하면, `build/generated-snippets/get-a-book/`이 경로에 snippet 자동 생성! 
* 여기까지만 해도 Spring REST Docs로 API 문서 자동화 끝!!!!
* But, 이대로 두기엔 가독성이 너무 떨어지므로 좀 더 보기 좋게 예쁘게 개선해보자.
### 4. API 문서를 예쁘게 만들기
#### 1. `.adoc`파일 만들기
* `.adoc`파일을 만들어야 한다. 경로는 아래와 같다.
    * `src/docs/asciidoc/*.adoc`(파일명은 원하는대로, 일반적으로 index)
    * Maven은 경로 다름 주의
* 생성한 파일에 스니펫 포함하기
```
// 공식 문서에 나온 예시 코드
include::{snippets}/get-a-book/curl-request.adoc[]
```
#### 2. `.html`파일 만들기
* 방법1: IntelliJ우측 Gradle > documentation > asciidoctor
* 방법2: 터미널에 `./gradlew asciidoctor` 입력
* `build/asciidoc/*.html` 이 경로에 `.html`파일이 만들어 진다.
    * [안될 시 해결 방법](#-asciidoctor를-통해-indexadoc-파일을-읽어서-indexhtml-파일을-생성하는-작업에서-indexadoc-파일-내-including한-경로를-찾지-못해-발생하는-문제)
    * Maven은 경로 다름 주의






---
##### [1] Asciidoctor
* AsciiDoc을 HTML, DocBook 등으로 변환하기 위한 빠른 텍스트 프로세서(.adoc)
##### [2] snippet
* 문서화에 필요한 문서 조각

## 문제 해결
### ⚠ asciidoctor를 통해 index.adoc 파일을 읽어서 index.html 파일을 생성하는 작업에서 index.adoc 파일 내 including한 경로를 찾지 못해 발생하는 문제
#### 발단
* index.adoc 파일을 만들고 index.html로 문서를 확인하고 싶어서 asciidoctor을 실행시켰다.
#### 문제
* snippet 내용이 나올 줄 알았는데 index.html에 아래와 같은 내용이 나왔다.
```
<p>Unresolved directive in index.adoc - include::{snippets}/get-a-book/curl-request.adoc[]
Unresolved directive in index.adoc - include::{snippets}/get-a-book/http-request.adoc[]
Unresolved directive in index.adoc - include::{snippets}/get-a-book/http-response.adoc[]
Unresolved directive in index.adoc - include::{snippets}/get-a-book/httpie-request.adoc[]
Unresolved directive in index.adoc - include::{snippets}/get-a-book/request-body.adoc[]
Unresolved directive in index.adoc - include::{snippets}/get-a-book/response-body.adoc[]</p>
```
경로를 찾을 수 없다는 것!
#### 해결
* 경로에 있는 {snippets} 값을 지정해주지 않아서 발생한 문제였다.
* `:snippets: build/generated-snippets` 이렇게 snippets에 경로 저장
* adoc 문법 모르는 주니어 개발자의 역경...
* 덕분에 해결방법 찾으면서 추가로 좋은 설정 몇 개 알아냄



## Reference
https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/#getting-started
https://spring.io/projects/spring-restdocs
https://www.youtube.com/watch?v=A3WDAVQP32k
https://www.youtube.com/watch?v=BoVpTSsTuVQ
https://colabear754.tistory.com/218