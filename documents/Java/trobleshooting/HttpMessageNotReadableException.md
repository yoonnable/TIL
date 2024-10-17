# 요청 본문 역직렬화 문제 `HttpMessageNotReadableException`

## 문제
* Spring REST Docs를 적용하는 작업 중 기존에 요청 본문을 단순 문자열 값 하나만 받아오는 형식으로 했다가 `requestField` 설정할 때 형변환 되지 않는다는 에러가 발생해 요청 본문을 dto로 감싸는 작업을 하였다.
* 변경된 코드
1. `UpdateProductRequest` DTO 추가
    ```java
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    @Getter
    public class UpdateProductRequest {

    private String thumbImg;

    public UpdateProductRequest(String thumbImg) {
        this.thumbImg = thumbImg;
    }
    }
    ```
2. Controller 수정
    ```java
    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateThumbImg(@PathVariable Long id,
        @RequestBody UpdateProductRequest request) {
        String thumbImg = request.getThumbImg();
        Product product = productService.updateThumbImg(id, thumbImg);
        return ResponseEntity.ok().body(product);
    }
    ```
3. 관련 Test 코드 수정
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
                 * 여기! 적용하고 싶어서 수정함
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

* 테스트 실행 후 발생한 에러 로그...
    ```console
    ...

    Resolved Exception:
                Type = org.springframework.http.converter.HttpMessageNotReadableException
    ...

    Status
    Expected :200
    Actual   :400
    <Click to see difference>

    java.lang.AssertionError: Status expected:<200> but was:<400>
    ```
    * Bad Request인 400 코드가 뜨고, `HttpMessageNotReadableException`라는 에러 메시지를 확인할 수 있었다.
    * 요청에 문제가 있다는 건 확실! 근데 뭐가 문제인지 모르겠어서 찾아보았다.
* `HttpMessageNotReadableException` : 주로 요청 본문(JSON 데이터)을 서버에서 읽을 수 없을 때 발생하며, 일반적으로 JSON 직렬화/역직렬화 과정에서 문제가 있을 때 나타난다고 한다.
* 직렬화는 객체 -> JSON(통신하기 쉬운 형태), 역직렬화는 JSON -> 객체로 바꿔주는 거로만 알고 있고, 이를 사용하기 위해선 어떻게 구현해야 하는지 잘 몰라서 생긴 문제인 것 같다.
* (+) 또한 내 테스트코드가 내부적으로 어떻게 동작하는지 과정도 디테일하게 모른 것이 문제..

## 해결
### 역직렬화시 기본 생성자 필요
* Jackson과 같은 JSON 직렬화 라이브러리는 역직렬화할 때 **기본 생성자**를 필요로 한다.
    * 객체를 역직렬화할 때 기본 생성자를 사용하여 인스턴스를 생성한 후, setter 메서드나 필드에 직접 값을 할당하기 때문이다.

#### Jackson 역직렬화 동작 방식
1. 기본 생성자로 객체를 먼저 생성
2. JSON에서 전달된 값을 해당 객체의 필드에 매핑

### 역직렬화가 제대로 이루어지지 않아서 발생한 문제!!
* 테스트 코드에서 요청을 받아오는 과정 (with 역직렬화)
    1. `mockMvc.perform(patch("/products/{id}", savedProduct.getId())...)`에서 `patch()` 메서드는 HTTP PATCH 요청을 보낸다. 
        * 이 요청에 포함된 JSON 문자열은 `.content(new ObjectMapper().writeValueAsString(request))`에 의해 생성된 것으로 `UpdateProductRequest` 객체를 JSON으로 **직렬화**하고 HTTP 요청의 body에 담는다.

    2. 서버는 이 요청을 받으면, Spring의 컨트롤러 메서드 `updateThumbImg`의 `@RequestBody UpdateProductRequest request` 부분이 실행된다.
    ```java
    public ResponseEntity<Product> updateThumbImg(@PathVariable Long id,
        @RequestBody UpdateProductRequest request) {
    ```
    * 여기서 **역직렬화**가 이루어진다.
* Spring은 HTTP 요청 body에 있는 JSON 데이터를 `UpdateProductRequest` 타입의 객체로 **변환(역직렬화)** 하는데, 이때, `UpdateProductRequest` 클래스에 **기본 생성자**가 필요합니다.
* 기본 생성자가 없으면 Spring이 객체를 생성할 수 없어서 `HttpMessageNotReadableException`이 발생한 것.


### DTO에 기본 생성자 추가
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



## 추가로 궁금한 점
### 1. 해당 프로젝트에 동일한 방식으로 구현한 '상품 등록' 요청 dto에는 기본 생성자가 없는데도 왜 잘 동작하나?
* AddProductRequest.java
    ```java
    import java.math.BigDecimal;
    import lombok.Getter;

    @Getter
    public class AddProductRequest {

        private Long categoryId;
        private String name;
        private BigDecimal price;
        private String thumbImg;
        private String detailImg;
        private String brand;
        private int stock;
        private int deliveryFee;
        private Integer fastDelivery;

        public AddProductRequest(Long categoryId, String name, BigDecimal price, String thumbImg,
            String detailImg, String brand, int stock, int deliveryFee, Integer fastDelivery) {
            this.categoryId = categoryId;
            this.name = name;
            this.price = price;
            this.thumbImg = thumbImg;
            this.detailImg = detailImg;
            this.brand = brand;
            this.stock = stock;
            this.deliveryFee = deliveryFee;
            this.fastDelivery = fastDelivery;
        }
    }
    ```
* 상품 등록 테스트 코드
    ```java
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
            
            ...

    }
    ```
* 보다시피 '썸네일 변경' 코드와 크게 다르지 않다. 그런데 '상품 등록'은 기본 생성자 없이 성공했고, '썸네일 변경'은 실패한게 이해가 안가서 찾아보았다.

우선, 두 DTO의 눈에 띄는 차이점부터 살펴보자.

#### 차이점
* AddProductRequest
    * 상품을 등록하기 위해 필요한 모든 정보를 포함하고 있다.
    * categoryId, name, price, thumbImg, detailImg, brand, stock, deliveryFee, fastDelivery
* UpdateProductRequest
    * thumbImg 하나만 포함되어 있다. 
    * 따라서, 요청이 더 간단하고, **역직렬화 시 필요한 정보가 제한적**이다.

> ### 역직렬화 시 필요한 정보가 제한적
* #### 역직렬화 개념
    * 역직렬화는 JSON과 같은 문자열 데이터를 자바 객체로 변환하는 과정이다. 
    * 이 과정에서 Jackson 라이브러리 같은 JSON 파서가 사용된다.

* #### 역직렬화 방법 (Jackson의 동작)
    1. Jackson은 요청 본문을 읽고 JSON 필드를 자바 객체의 필드에 매핑하기 위해 적절한 생성자를 찾는다.
    2. 기본 생성자가 있는 경우, Jackson은 이 생성자를 사용하여 객체를 만들고, setter 메서드를 통해 필드 값을 설정한다.
    3. 기본 생성자가 없고 매개변수로 받는 생성자만 있을 경우, 요청 본문에서 제공된 모든 값이 해당 생성자의 매개변수에 맞아떨어져야 한다.


#### 즉, 기본 생성자가 없어도 `AddProductRequest`의 역직렬화가 성공하는 이유
* 모든 필드에 대해 생성자를 통해 값을 설정할 수 있기 때문
* 요청 본문에서 모든 필드가 JSON 형태로 전달되면, Jackson은 해당 생성자를 사용하여 객체를 생성하고 값을 설정한다.
* 요청 본문을 살펴보면,
    ```json
    {
    "categoryId": 1,
    "name": "Test Product",
    "price": 10000,
    "thumbImg": "thumb.jpg",
    "detailImg": "detail.jpg",
    "brand": "Test Brand",
    "stock": 10,
    "deliveryFee": 2500,
    "fastDelivery": 0
    }
    ```
    * 모든 필드에 대해 값이 제공되므로 기본 생성자는 필요하지 않다는 것!!!!!


#### 반면에 `UpdateProductRequest`의 경우
* 이 DTO는 thumbImg 필드만 포함하고 있으며, 기본 생성자가 없으면 Jackson은 역직렬화할 때 사용할 생성자를 찾을 수 없다는 것 ㅠㅠㅠㅠ
* 요청 본문을 살펴보면,
    ```json
    {
    "thumbImg": "new_thumb.jpg"
    }
    ```
    * Jackson은 이 JSON을 `UpdateProductRequest` 객체로 변환하기 위해 기본 생성자 또는 매개변수로 값을 받는 생성자를 찾아야 한다.
    * 기본 생성자가 없다면, Jackson은 해당 클래스를 인스턴스화할 방법을 알 수 없으므로 에러가 발생하는 것이다.

#### 테스트 케이스의 동작
* `AddProductRequest`는 모든 필드가 요청 본문에 제공되면 기본 생성자가 없어도 문제가 없기 때문에 정상적으로 역직렬화 가능
* 반면에, `UpdateProductRequest`는 매개변수로 받는 생성자가 필요하며, 기본 생성자가 없으면 요청 본문에서 제공된 값이 없거나 부족한 경우 에러가 발생한다.

#### 결론
* `UpdateProductRequest`는 너무 작은 필드(1개)로 역직렬화를 어떻게 처리해야하나 난감한 상황이 펼쳐진 것.
* 어떤 DTO에서 기본 생성자가 필요하고 어떤 경우에 불필요한지는 그 DTO가 요구하는 입력의 형태와 Jackson이 객체를 생성하는 방식에 따라 다르다.

### 2. 그렇담, 필드가 2개 이상이고, 모든 필드에 값을 넣은 요청 본문을 보낸다면, 기본 생성자가 없어도 역직렬화를 할 수 있을까?
* 궁금해서 시도 해봄
* dto에 detailImg 필드 추가
    ```java
    import lombok.Builder;
    import lombok.Getter;

    @Getter
    public class UpdateProductRequest {

    private String thumbImg;
    private String detailImg;

    public UpdateProductRequest(String thumbImg) {
        this.thumbImg = thumbImg;
    }

    public UpdateProductRequest(String thumbImg, String detailImg) {
        this.thumbImg = thumbImg;
        this.detailImg = detailImg;
    }
    }
    ```
* 테스트 코드에 모든 필드 값 넣어 줌
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
        UpdateProductRequest request = new UpdateProductRequest(newThumbImg, product.getDetailImg());
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
                requestFields(
                    fieldWithPath("thumbImg").description("변경 될 상품 썸네일 이미지"),
                    fieldWithPath("detailImg").description("변경 될 상품 상세 이미지")
                ),

                ...
    ```
    * 성공

* 그럼, 둘 중에 하나의 필드만 값을 넣으면?
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
                requestFields(
                    fieldWithPath("thumbImg").description("변경 될 상품 썸네일 이미지"),
                    fieldWithPath("detailImg").description("변경 될 상품 상세 이미지")
                ),
                
                ...
    ```
    * 이것도 성공!
* 문제는 객체에 필드가 하나뿐이라는 것 그거 뿐이었군,,