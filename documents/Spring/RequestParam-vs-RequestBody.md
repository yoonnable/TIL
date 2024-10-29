# `@RequestParam` vs `@RequestBody`

<details>
<summary>서론</summary>

### 문제의 발단
* Spring REST Docs를 적용하기 위해 테스트 코드를 수정 후 실행하니 에러 발생
```console
2024-10-14T16:28:10.819+09:00 DEBUG 10576 --- [ntainer#0-0-C-1] org.elasticsearch.client.RestClient      : request [PUT http://localhost:9200/products/_doc/1?refresh=false] returned [HTTP/1.1 200 OK]
[Fatal Error] :1:1: Content is not allowed in prolog.

org.springframework.restdocs.payload.PayloadHandlingException: Cannot handle application/json content as it could not be parsed as JSON or XML
	at org.springframework.restdocs.payload.ContentHandler.forContentWithDescriptors(ContentHandler.java:69)
	at org.springframework.restdocs.payload.AbstractFieldsSnippet.createModel(AbstractFieldsSnippet.java:157)
	at org.springframework.restdocs.snippet.TemplatedSnippet.document(TemplatedSnippet.java:78)
	at org.springframework.restdocs.generate.RestDocumentationGenerator.handle(RestDocumentationGenerator.java:191)
	at org.springframework.restdocs.mockmvc.RestDocumentationResultHandler$1.handle(RestDocumentationResultHandler.java:77)
	at org.springframework.test.web.servlet.MockMvc$1.andDo(MockMvc.java:219)
	at com.ecommerce.product.ProductIntegrationTest.updateThumbImg(ProductIntegrationTest.java:177)
	at java.base/java.lang.reflect.Method.invoke(Method.java:568)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1511)
```
* 이 에러를 해결하기 위해 여러 방법을 찾아보던 중
기존 코드에 적용했던 `@RequestBody`를 `@RquestParam`으로 바꾸는 내용을 발견
* 왜냐하면, 기존 서비스 코드가 요청 받는 데이터가 String 형 값 하나 뿐이었기 때문이다.
* 결론 적으로, 테스트 실패를 해결하는데는 관련 없는 내용이지만, 문득 `@RequestBody`와 `@RequestParam`의 차이를 정확히 알지 못하는 것 같아서 학습했다.
</details>

---
`@RequestParam`과 `@RequestBody`는 **Spring Framework**에서 제공하는 기능으로, 주로 **Spring MVC**에서 **RESTful 웹** 서비스를 구현할 때 사용된다.


## @RequestBody
* `@RequestBody`는 요청 본문(body)에서 JSON이나 XML 등의 데이터 형식을 읽어 Java 객체로 변환하는 데 사용된다.
* 요청 본문에 JSON 데이터가 포함되어 있을 때, Spring은 이 데이터를 Java 객체(또는 맵)로 변환한다.
### 코드 리뷰
```java
@PatchMapping("/{id}")
  public ResponseEntity<Product> updateThumbImg(@PathVariable Long id,
      @RequestBody String thumbImg) {
    Product product = productService.updateThumbImg(id, thumbImg);
    return ResponseEntity.ok().body(product);
  }
```
* API 설계에서, 일반적으로 클라이언트는 여러 필드를 포함하는 JSON 객체를 서버에 전송한다. 
    * 이미지를 업로드하는 경우 이미지 URL을 포함한 JSON 객체를 보낼 수 있다.
* 만약 클라이언트가 보낸 데이터가 JSON 객체라면, 특정 키에 대한 값을 요청으로 받을 수 있다.
<br/>*(`@RequestBody String thumbImg`와 같은 코드에서는, 단순 문자열을 받기 때문에 JSON 형식의 데이터가 필요하지 않음💡그럼 이 경우엔 어떻게 하는 게 좋을까?)*
    * 만약 메서드에서 `@RequestBody`로 `String thumbImg`를 사용하면, 클라이언트에서 단순한 문자열을 보내는 경우에 적합하다.
    <br/>그러나 만약 JSON 객체로 보내면, 이 메서드는 올바르게 처리하지 못할 수 있다.
        * 예를 들어, newThumbImg를 `"{\"thumbImg\": \"new_thumb_img_url\"}"`와 같이 설정하면, JSON 키가 `thumbImg`이고 그 값이 `new_thumb_img_url`로 설정되는데,
        이 경우 서버는 `thumbImg`라는 키를 가진 JSON 객체를 인식하고 이를 적절하게 처리할 수 있다.

* String newThumbImg = "new_thumb_img_url";와 같은 경우, 단순한 문자열을 서버에 전송하는 것은 API 설계의 일반적인 패턴과 맞지 않다.
* 요청 본문에 JSON 구조를 포함하는 것이 일반적인 관행
* 따라서 String newThumbImg = "{\"thumbImg\": \"new_thumb_img_url\"}";와 같이 JSON 형식으로 문자열을 설정하는 것이 더 적절하며, 서버가 이 요청을 제대로 처리할 수 있다.
* 위 내용을 제대로 파악하기 위해 SpringMVC 학습 중...
* 만약 String newThumbImg = "new_thumb_img_url";와 같은 단순 문자열을 사용하고 싶다면, @RequestBody 대신에 @RequestParam을 사용할 수 있다.(PATCH인데...?)


### `@RequestParam`으로 수정한 코드
```java
@PatchMapping("/{id}")
public ResponseEntity<Product> updateThumbImg(@PathVariable Long id,
    @RequestParam String thumbImg) {
    Product product = productService.updateThumbImg(id, thumbImg);
    return ResponseEntity.ok().body(product);
}
```
* 위와 같은 방식은 /{id}?thumbImg=" + newThumbImg 이런식으로 요청을 보내야 한다.
* 상품 정보를 수정하는 프라이빗한 기능엔 너무 맞지 않는 방식이다.
* 결국, 위와 같은 상황에선 그냥 request DTO를 만들어서 요청 받는게 Best





## @RequestParam
### 장점
* 간단한 구조: 문자열 값 하나를 전송할 때, 쿼리 파라미터로 쉽게 전달할 수 있습니다.
* 명시적: URL에 직접 파라미터가 포함되므로, 요청의 내용을 쉽게 이해할 수 있습니다.
* 불필요한 JSON 처리 없음: JSON 파싱이 필요 없으므로 코드가 간단해집니다.

### 용도
* 단순한 문자열 또는 몇 개의 필드를 요청할 때 적합
* URL의 쿼리 파라미터나 폼 데이터에서 값을 가져옵니다.
* 전송 방식: 일반적으로 URL에 포함되어 쿼리 문자열로 전달됩니다. 
    * ex) GET /products?id=1&thumbImg=new_thumb_img_url와 같이 요청
* 전송 예: 주로 GET 요청이나 폼 데이터 전송

## @RequestBody
### 장점
* 복잡한 데이터 전송: 여러 필드가 포함된 객체를 전송할 때 유용합니다. JSON 구조로 복잡한 데이터를 전달할 수 있습니다.
* RESTful API의 일반적인 패턴: 많은 RESTful API가 JSON 객체를 요청 본문에 담아 전송하는 방식을 따릅니다.
### 용도
* 복잡한 객체나 여러 필드를 포함해야 할 경우: @RequestBody를 사용하여 JSON 형식으로 데이터를 전달하는 것이 좋습니다.
* 용도: HTTP 요청의 본문에서 JSON 또는 XML 데이터를 Java 객체로 변환합니다.
* 전송 방식: 요청 본문(body)로 전달됩니다. 
    * POST 요청에서 JSON 객체를 포함하여 전송
* 전송 예: 주로 POST, PUT, PATCH 요청