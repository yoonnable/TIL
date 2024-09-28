# RESTful API
* REST 아키텍처 스타일을 전부 다 완벽하게 따라서 만든 API
    * 사실 위 설명이 그냥 REST API라고 불리어야 하는데(REST 창조자 roy의 주장)
* 오늘날엔 REST 아키텍처 스타일을 전부 지키지 않았음에도 REST API라고 부르는 경우가 많아서,
* 많은 블로그 글, 유튜브 영상을 보면 REST를 "기반"으로 만든 API는 REST API, REST 스타일을 완벽히 지켜 만든 API는 RESTful API 라고 나뉘어 불리게 되는 것 같다.

그렇다면, REST가 뭔지 알아보았다.
## REST
* 분산 하이퍼 미디어 시스템(ex. Web)을 위한 아키텍처 스타일이자 아키텍처 스타일의 집합
    * 아키텍처 스타일 = 제약조건의 집합

### RESR를 구성하는 아키텍처 스타일들
1. Client-Server
2. Stateless
3. Cache
4. Uniform interface
5. layered system
6. code-on-demend (optional)

* 즉 RESTful API란, 위 5가지(6번은 옵션) 스타일을 다 지키는 API여야 한다는 것!
* 근데 REST API를 개발할 때 나머지는 다 잘 지키는 편인데, **4. Uniform interface**가 잘 안지켜짐

## Uniform Interface
* 일관된 인터페이스
* server - client의 확실한 분리를 통해 독립적 진화를 이루기 위해!
    * 독립적 진화란, **상호 운용성**을 위한 것
    * 어떤 것이 업데이트 되더라도 그로인해 문제가 발생하지 않도록 보장하는 것이다.
    > REST를 만들게 된 본질적인 계기 또한 "How do I improve HTTP without breaking the Web." 에 대한 답!

### Uniform Interface의 제약조건 4가지
* identification of resource  : 리소스가 uri로 식별되면 된다. ⭕
* manipulation of resource through representations : ⭕
* self-descriptive messages ❌
* hypermedia as the engine of application state (HATEOAS) ❌

### Self-descrip





## Reference
https://www.youtube.com/watch?v=RP_f5dMoHFc
https://www.youtube.com/watch?v=xWA1eTPSzDE
https://velog.io/@kms8571/REST%EB%A5%BC-%EC%9C%84%ED%95%9C-uniform-interface