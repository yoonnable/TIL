# 1. 요청 본문 값이 단순 문자열 하나 일 때, requestFields 설정 시 에러 발생
##### 관련 지식
* [Spring REST Docs](../Spring/SpringRESTDocs/Spring-REST-Docs.md)

## 문제
* 문제의 코드
```java
@DisplayName("상품 썸네일 변경에 성공한다.")
@Test
void updateThumbImg() throws Exception {
    // given
    String oldThumbImg = "old_thumb_img_url";
    String newThumbImg = "new_thumb_img_url";
    Product product = Product.builder()
        .category(category)
        .name("Test Product")
        .price(BigDecimal.valueOf(100))
        .thumbImg(oldThumbImg)
        .detailImg("detail_img_url")
        .brand("TestBrand")
        .stock(10)
        .deliveryFee(0)
        .fastDelivery(0)
        .build();
    Product savedProduct = productRepository.save(product);
    String accessToken = jwtUtil.createAccessToken("test@example.com", 1L, Role.ADMIN.name());

    // when
    mockMvc.perform(patch("/products/{id}", savedProduct.getId())
            .header("Access", accessToken)
            .contentType(MediaType.TEXT_PLAIN_VALUE)
            .content(newThumbImg))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.thumbImg").value(newThumbImg))

        // Rest Docs
        .andDo(restDocs.document(
            pathParameters(
                parameterWithName("id").description("상품 ID")
            ),
            requestFields(
                fieldWithPath("thumbImg").description("변경 될 상품 썸네일 이미지")
            ),
            responseFields(
                fieldWithPath("id").description("상품 ID"),
                fieldWithPath("name").description("상품의 이름"),
                fieldWithPath("price").description("상품의 가격"),
                fieldWithPath("thumbImg").description("변경된 상품 썸네일 이미지"),
                fieldWithPath("detailImg").description("상품 상세 이미지"),
                fieldWithPath("brand").description("상품의 브랜드"),
                fieldWithPath("stock").description("상품의 재고 수량"),
                fieldWithPath("deliveryFee").description("배송비"),
                fieldWithPath("fastDelivery").description("신속 배송 여부 (0: 불가, 1: 가능)")
                    .type(JsonFieldType.NUMBER)
                    .attributes(key("constraints").value("0 또는 1만 입력 가능합니다.")),
                fieldWithPath("createdAt").description("등록 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                    .type(JsonFieldType.STRING)
                    .attributes(key("format").value("ISO 8601 형식")),
                fieldWithPath("updatedAt").description("최신 업데이트 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                    .type(JsonFieldType.STRING)
                    .attributes(key("format").value("ISO 8601 형식")),
                fieldWithPath("score").description("인기순 점수").type(JsonFieldType.NUMBER).optional()
            )
        ))
        .andDo(print());
}
```
* 에러 메시지
```console
[Fatal Error] :1:1: Content is not allowed in prolog.

org.springframework.restdocs.payload.PayloadHandlingException: Cannot handle text/plain content as it could not be parsed as JSON or XML
	at org.springframework.restdocs.payload.ContentHandler.forContentWithDescriptors(ContentHandler.java:69)
	at org.springframework.restdocs.payload.AbstractFieldsSnippet.createModel(AbstractFieldsSnippet.java:157)
	at org.springframework.restdocs.snippet.TemplatedSnippet.document(TemplatedSnippet.java:78)
	at org.springframework.restdocs.generate.RestDocumentationGenerator.handle(RestDocumentationGenerator.java:191)
	at org.springframework.restdocs.mockmvc.RestDocumentationResultHandler$1.handle(RestDocumentationResultHandler.java:77)
	at org.springframework.test.web.servlet.MockMvc$1.andDo(MockMvc.java:219)
	at com.ecommerce.product.ProductIntegrationTest.updateThumbImg(ProductIntegrationTest.java:173)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
```
* `Cannot handle text/plain content as it could not be parsed as JSON or XML`: JSON 또는 XML로 구문 분석 할 수 없으므로 텍스트/일반 콘텐츠를 처리 할 수 ​​없습니다.
* 요청 본문에 단순 문자열 값 하나만 받아오면 `requestFields()`를 사용할 수 없다고 한다.
> 응답 또한 마찬가지로 String을 반환하는데 `responseFields()`를 사용했다? 그럼 위와 동일한 에러가 발생한다.

## 해결
### 1. 요청 본문에 단순 문자열만 받아오게 할 경우 `requestFields()`를 쓰지 않는다.
### 2. resquest DTO를 생성해서 반환해 준다.
* requestDTO 생성
* 관련 내용 : [요청 본문 역직렬화 문제 `HttpMessageNotReadableException`](/documents/Java/trobleshooting/HttpMessageNotReadableException.md)
    ```java
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    @Getter
    @NoArgsConstructor
    public class UpdateProductRequest {

    private String thumbImg;

    public UpdateProductRequest(String thumbImg) {
        this.thumbImg = thumbImg;
    }
    }
    ```
* DTO 적용한 Controller
    ```java
    @PatchMapping("/{id}")
        public ResponseEntity<Product> updateThumbImg(@PathVariable Long id,
            @RequestBody UpdateProductRequest request) {
            String thumbImg = request.getThumbImg();
            Product product = productService.updateThumbImg(id, thumbImg);
            return ResponseEntity.ok().body(product);
        }
    ```
* `requestFields()` 적용
    ```java
    @DisplayName("상품 썸네일 변경에 성공한다.")
    @Test
    void updateThumbImg() throws Exception {
        // given
        String oldThumbImg = "old_thumb_img_url";
        String newThumbImg = "new_thumb_img_url";
        Product product = Product.builder()
            .category(category)
            .name("Test Product")
            .price(BigDecimal.valueOf(100))
            .thumbImg(oldThumbImg)
            .detailImg("detail_img_url")
            .brand("TestBrand")
            .stock(10)
            .deliveryFee(0)
            .fastDelivery(0)
            .build();
        UpdateProductRequest request = new UpdateProductRequest(newThumbImg);
        Product savedProduct = productRepository.save(product);
        String accessToken = jwtUtil.createAccessToken("test@example.com", 1L, Role.ADMIN.name());

        // when
        mockMvc.perform(patch("/products/{id}", savedProduct.getId())
                .header("Access", accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            // then
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.thumbImg").value(newThumbImg))
            .andDo(restDocs.document(
                pathParameters(
                    parameterWithName("id").description("상품 ID")
                ),
                /***************************
                 * 여기!
                 ****************************/
                requestFields(
                    fieldWithPath("thumbImg").description("변경 될 상품 썸네일 이미지")
                ),
                responseFields(
                    fieldWithPath("id").description("상품 ID"),
                    fieldWithPath("name").description("상품의 이름"),
                    fieldWithPath("price").description("상품의 가격"),
                    fieldWithPath("thumbImg").description("변경된 상품 썸네일 이미지"),
                    fieldWithPath("detailImg").description("상품 상세 이미지"),
                    fieldWithPath("brand").description("상품의 브랜드"),
                    fieldWithPath("stock").description("상품의 재고 수량"),
                    fieldWithPath("deliveryFee").description("배송비"),
                    fieldWithPath("fastDelivery").description("신속 배송 여부 (0: 불가, 1: 가능)")
                        .type(JsonFieldType.NUMBER)
                        .attributes(key("constraints").value("0 또는 1만 입력 가능합니다.")),
                    fieldWithPath("createdAt").description("등록 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                        .type(JsonFieldType.STRING)
                        .attributes(key("format").value("ISO 8601 형식")),
                    fieldWithPath("updatedAt").description("최신 업데이트 일자 (형식: yyyy-MM-dd'T'HH:mm:ss.SSS)")
                        .type(JsonFieldType.STRING)
                        .attributes(key("format").value("ISO 8601 형식")),
                    fieldWithPath("score").description("인기순 점수").type(JsonFieldType.NUMBER).optional()
                )
            ))
            .andDo(print());
    }
    ```
* request-fields.snippet
![alt text](/documents/img/spring/trobleshooting/requestFields-responseFields/image-2.png)



# 2. 응답 본문 값이 단순 숫자 하나 일 때, responseFields 설정 시 에러 발생

## 문제

* 문제의 코드
```java
@DisplayName("회원가입에 성공한다.")
@Test
void signup() throws Exception {
    // given
    final String url = "/members";
    final SignupRequest request = SignupRequest.builder()
        .email("member1@test.com")
        .password("f_lab16881577")
        .name("테스트1")
        .phone("01011111111")
        .build();

    // when
    final ResultActions action = mockMvc.perform(post(url)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    action.andExpect(status().isOk())
        .andExpect(content().string("1"))
        .andDo(restDocs.document(
            requestFields(
                fieldWithPath("email").description("회원 이메일"),
                fieldWithPath("password").description("회원 비밀번호"),
                fieldWithPath("name").description("회원 이름"),
                fieldWithPath("phone").description("회원 연락처"),
                fieldWithPath("role").description("회원 권한 - [BASIC, ADMIN]").optional()
            ),
            responseFields(
                fieldWithPath("$").description("회원 고유 ID")
            )
        ))
        .andDo(print());
}
```
* 에러 메시지
```console
java.lang.ClassCastException: class java.lang.Integer cannot be cast to class java.util.List (java.lang.Integer and java.util.List are in module java.base of loader 'bootstrap')
	at org.springframework.restdocs.payload.JsonContentHandler.isEmpty(JsonContentHandler.java:145)
	at org.springframework.restdocs.payload.JsonContentHandler.getUndocumentedContent(JsonContentHandler.java:117)
	at org.springframework.restdocs.payload.AbstractFieldsSnippet.validateFieldDocumentation(AbstractFieldsSnippet.java:200)
	at org.springframework.restdocs.payload.AbstractFieldsSnippet.createModel(AbstractFieldsSnippet.java:160)
	at org.springframework.restdocs.snippet.TemplatedSnippet.document(TemplatedSnippet.java:78)
	at org.springframework.restdocs.generate.RestDocumentationGenerator.handle(RestDocumentationGenerator.java:191)
	at org.springframework.restdocs.mockmvc.RestDocumentationResultHandler$1.handle(RestDocumentationResultHandler.java:77)
	at org.springframework.test.web.servlet.MockMvc$1.andDo(MockMvc.java:219)
	at com.ecommerce.member.MemberIntegrationTest.signup(MemberIntegrationTest.java:62)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
```
* `class java.lang.Integer cannot be cast to class java.util.List` : 클래스 java.lang.integer는 클래스 java.util.list로 형변환 할 수 없다
* 객체를 반환하지 않는 API에서는 `resonseFields()`를 사용 할 수 없다고 한다.

## 해결
### 1. 객체가 아닌 값을 반환할 경우 `responseFields()`를 쓰지 않는다.
### 2. response DTO를 생성해서 반환해 준다.
* 기존에 만들어뒀던 공통으로 사용할 수 있는 응답 DTO가 있어서 일단 그걸로 신속하게 적용했다.
* DTO 적용한 코드
    ```java
    @PostMapping("")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request,
        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
        // 유효성 검증 실패 시 처리
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        try {
        Long id = memberService.save(request);
        return ResponseEntity.ok(ResponseMessage.builder().data(id).build()); // DTO 적용
        } catch (Exception e) {
        // 서비스에서 예외 발생 시 처리
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    ```
* `responseFields()` 적용
    ```java
    @DisplayName("회원가입에 성공한다.")
    @Test
    void signup() throws Exception {
        // given
        final String url = "/members";
        final SignupRequest request = SignupRequest.builder()
            .email("member1@test.com")
            .password("f_lab16881577")
            .name("테스트1")
            .phone("01011111111")
            .build();

        // when
        final ResultActions action = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)));

        // then
        action.andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value(1L)) // 1
            .andDo(restDocs.document(
                requestFields(
                    fieldWithPath("email").description("회원 이메일"),
                    fieldWithPath("password").description("회원 비밀번호"),
                    fieldWithPath("name").description("회원 이름"),
                    fieldWithPath("phone").description("회원 연락처"),
                    fieldWithPath("role").description("회원 권한 - [BASIC, ADMIN]").optional()
                ),
                relaxedResponseFields( // 2
                    fieldWithPath("data").description("회원 고유 ID") // 3
                )
            ))
            .andDo(print());

        // check DB
        List<Member> members = memberRepository.findAll();

        assertThat(members.size()).isEqualTo(1);
        assertThat(members.get(0).getEmail()).isEqualTo(request.getEmail());
        assertThat(bCryptPasswordEncoder.matches(request.getPassword(),
            members.get(0).getPassword())).isTrue();
        assertThat(members.get(0).getName()).isEqualTo(request.getName());
        assertThat(members.get(0).getPhone()).isEqualTo(request.getPhone());
    }
    ```
    1. 겸증 방식 `content().string(1)` -> `jsonPath("$.data").value(1L)`로 변경
    2. 이 전에 만들어둔 공통 DTO 사용으로 인해 불필요한 필드가 있어서 그 필드들은 필드 설명 snippet에서 적용하지 않기 위해 `relaxedResponseFields()`를 사용
    3. 응답 본문 설명 드디어 적용!
* response-fields.snippet
![alt text](/documents/img/spring/trobleshooting/requestFields-responseFields/image.png)

![alt text](/documents/img/spring/trobleshooting/requestFields-responseFields/image-1.png)
쌓여가는 스니펫을 보니 기분이 좋군




### Referenve
https://velog.io/@tmdgh0221/Spring-Rest-Docs-%EC%A0%81%EC%9A%A9%ED%95%B4%EB%B3%B4%EA%B8%B0
https://mr-popo.tistory.com/214