# Elastic Load Balancer
> Elastic Load Balancing은 들어오는 애플리케이션 트래픽을 Amazon EC2 인스턴스, 컨테이너, IP 주소, Lambda 함수와 같은 여러 대상에 자동으로 분산시킨다. 단일 가용 영역 또는 여러 가용 영역에서 다양한 애플리케이션 부하를 처리할 수 있다.

* 다수의 서비스에 트래픽을 분산 시켜주는 서비스
* Health Check: 직접 트래픽을 발생시켜 Instance가 살아있는지 체크 (Auto Scaling과 비슷)
* Autoscaling과 연동 가능
* 여러 가용 영역에 분산 가능
* 지속적으로 IP 주소가 바뀌며 IP 고정 불가능 : 항상 도메인 기반으로 사용 (예외 있음(심화 과정))

* 종류
    * **Application Load Balancer**
        * 가장 자주 쓰임
        * 똑똑함
        * 트래픽을 모니터링하여 라우팅 기능
            * ex) image.sample.com -> 이미지 서버로, web.sample.com -> 웹 서버로 트래픽 분산
    * **Network Load Balancer**
        * 빠름
        * TCP 기반 빠른 트래픽 분산
        * Elastic IP(IP를 고정시켜주는 서비스) 할당 가능
    * **Classic Load Balancer**
        * 예전에 사용되던 타입으로 현재는 잘 사용하지 않음
    * **Gateway Load Balancer**
        * 먼저 트래픽을 체크함
            ![alt text](/documents/img/aws/basic/aws-basic-13/image.png)
            * EC2에 도달하기 전, 먼저 트래픽을 검사하거나, 분석하거나, 인증하거나, 로깅하는 작업을 먼저 수행하도록 도와준다.(일반적인 로드밸런서들과 약간 다른 작업)
        * 가상 어플라이언스 배포/확장 관리를 위한 서비스

## Application Load Balancer
### 대상 그룹(Target Group)
* **ALB가 라우팅 할 대상의 집합**
* 구성: 3 + 1가지 종류
    * Instance : EC2 Instance
    * IP : 내부에서만 쓸 수 있는 Private IP
    * Lanbda
    * 다른 ALB
* 프로토콜 (HTTP, HTTPS, gRPC 등 가능)
* 기타 설정: 트래픽 분산 알고리즘, 고정 세션 등
![alt text](/documents/img/aws/basic/aws-basic-13/image-1.png)
### 아키텍처
![alt text](/documents/img/aws/basic/aws-basic-13/image-2.png)
* Auto scaling과 ELB를 적용한 이 EC2들은 고가용성과 장애 내구성까지 확보

#### Auto scaling 입장에선 정상이지만, ALB 입장에서는 비정상인 경우
* EC2는 존재하지만, 웹 서버가 꺼져있는 경우는? 
    * 답: 502 Bad Gateway : ALB가 트래픽 분산하려고 존재하는 EC2들에게 트래픽을 전송하는 중에 웹 서버가 다운되어 있는 EC2에 보냈더니 아무런 트래픽을 주지 않아 어떻게 해야할지 모르는 상황!
    -> 이 경우에도 Auto Scaling이 자동으로 웹 서버를 복구시키도록 해야한다.(이게 바로Health check -> 상태 확인 유형에 ELB도 선택)