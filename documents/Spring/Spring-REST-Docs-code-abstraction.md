# Spring REST Docs 중복코드 추상화
* 같은 프로젝트 내에 있는 API라면, REST Docs를 적용할 때 전체적으로 중복되는 코드가 있다.
```
.andDo(document("add-product",
    preprocessRequest(
        prettyPrint(),
        modifyUris()
            .scheme("http")
            .host("localhost")
            .port(8080)
    ),
    preprocessResponse(prettyPrint()),
```
* 바로 이 문서 이름과 요청, 응답 출력의 기본적인 설정 부분이다.
* 코드 중복을 줄이기 위해 추상화 작업을 하겠다. (템플릿 메서드 패턴)
## 1. COnfigration 클래스 생성
* snippet 설정 코드(중복되는 코드)를 작성할 클래스를 생성한다.
```java
@TestConfiguration
public class RestDocsConfigration {

  @Value("${server.scheme}") // 1
  private String scheme;

  @Value("${server.host}")
  private String host;

  @Value("${server.port}")
  private int port;

  @Bean
  public RestDocumentationResultHandler write() { // 2
    return MockMvcRestDocumentation.document(
        "{method-name}", // 3
        preprocessRequest(
            prettyPrint(),
            modifyUris()
                .scheme(scheme)
                .host(host)
                .port(port)
        ),
        preprocessResponse(prettyPrint()
        )
    );
  }
}
```
1. 현재 테스트는 로컬에서만 이루어 지고 있으며, 추후 배포 시 서버 정보가 바뀌는 것을 대비해 `src/main/resources/`경로에 있는 `application.yml` 을 활용하여 서버 정보를 주입받는다.
2. 리턴값은 RestDocumentationResultHandler 인스턴스
3. 각 테스트마다 생성되는 snippet의 최종 경로를 테스트 메소드 명으로 설정한다. 클래스명도 넣고 싶다면, `{class-name}` 으로 하면 된다. 여기서 '-'을 사용함으로써 디렉토리 명은 케밥 케이스로 명시된다.
## 2. setup 코드가 작성된 추상 클래스 생성
* 중복되는 setup 코드를 추상화하여 RestDocs를 적용하는 모든 코드가 상속받아 사용할 수 있도록 한다.
```java
@Import(RestDocsConfigration.class) // 1
@ExtendWith(RestDocumentationExtension.class)
public abstract class AbstractRestDocsTests {

  @Autowired
  protected RestDocumentationResultHandler restDocs;
  @Autowired
  protected MockMvc mockMvc;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) throws IOException {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
        .apply(documentationConfiguration(restDocumentation))
        .alwaysDo(MockMvcResultHandlers.print()) // 2
        .alwaysDo(restDocs) // 3
        .build();
  }
}
```
1. 내가 만든 RestDocsConfigration클래스 import 명시
2. 
3. 이 클래스를 상속받은 테스트는 항상 restDocs 실행
## 3. 일반 테스트 코드에 추상 클래스를 상속 받고 중복 코드 제거
```java
@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegrationTest extends AbstractRestDocsTests { // 1

// 2
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private ProductDocumentRepository productDocumentRepository;
  @Autowired
  private JwtUtil jwtUtil;

  private Category category;

  @BeforeEach
  void setUp() {
    // 3
    productRepository.deleteAll();

    category = Category.builder()
        .id(1L)
        .name("Test Category")
        .build();
    categoryRepository.save(category);
  }

  @DisplayName("상품 등록에 성공한다.")
  @Test
  void addProduct() throws Exception {
    // given
    AddProductRequest request = new AddProductRequest(
        1L,
        "Test Product",
        BigDecimal.valueOf(10000.00),
        "thumb.jpg",
        "detail.jpg",
        "Test Brand",
        10,
        2500,
        0
    );
    String accessToken = jwtUtil.createAccessToken("test@example.com", 1L, Role.ADMIN.name());

    // when
    mockMvc.perform(post("/products")
            .header("Access", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.price").value(request.getPrice()))
        .andExpect(jsonPath("$.thumbImg").value(request.getThumbImg()))
        .andExpect(jsonPath("$.detailImg").value(request.getDetailImg()))
        .andExpect(jsonPath("$.brand").value(request.getBrand()))
        .andExpect(jsonPath("$.stock").value(request.getStock()))
        .andExpect(jsonPath("$.deliveryFee").value(request.getDeliveryFee()))
        .andExpect(jsonPath("$.fastDelivery").value(request.getFastDelivery()))
        // REST Docs
        .andDo(restDocs.document( //4
            requestFields(               // 요청 필드 설명
                fieldWithPath("categoryId").description("상품의 카테고리 ID"),
                fieldWithPath("name").description("상품의 이름"),
                fieldWithPath("price").description("상품의 가격"),
                fieldWithPath("thumbImg").description("상품 썸네일 이미지"),
                fieldWithPath("detailImg").description("상품 상세 이미지"),
                fieldWithPath("brand").description("상품의 브랜드"),
                fieldWithPath("stock").description("상품의 재고 수량"),
                fieldWithPath("deliveryFee").description("배송비"),
                fieldWithPath("fastDelivery").description("신속 배송 여부 (0: 불가, 1: 가능)")
                    .type(JsonFieldType.NUMBER)
                    .attributes(key("constraints").value("0 또는 1만 입력 가능합니다."))
            ),
            responseFields(
                fieldWithPath("id").description("상품 ID"),
                fieldWithPath("name").description("상품의 이름"),
                fieldWithPath("price").description("상품의 가격"),
                fieldWithPath("thumbImg").description("상품 썸네일 이미지"),
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
        .andDo(print()); // 로그 출력

    ...
  }

  ...
}
```
1. `AbstractRestDocsTests` 상속 받고, `@ExtendWith(RestDocumentationExtension.class)` 코드 제거
2. `MockMvc mockMvc` 필드 제거
3. MockMvc 생성 코드 제거
4. REST Docs 생성시 중복 설정 코드 제거하고 부모 클래스 필드인 restDocs를 사용

## 4. 테스트 실행 (결과 확인)
* 추상화 전과 다르지 않는 .adoc파일이 생성되면 성공!


## Reference
https://velog.io/@chaerim1001/Spring-Rest-Docs-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0-AsciiDoc-%EB%AC%B8%EB%B2%95#buildgradle
