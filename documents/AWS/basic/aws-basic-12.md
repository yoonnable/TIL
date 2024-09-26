# EC2 Autoscaling
## 스케일링
* 인스턴스 혹은 컴퓨팅 파워를 늘리는 것
### Vertical Scale(Scale up) = 수직적 스케일링
* 인스턴스 크기 자체를 늘리는 것
    * 4GB의 CPU가 있을 때 1GB의 CPU가 필요하다면, 4GB -> 16GB의 CPU로 늘리는 것
* 비용이 너무 많이 듦(크기가 커질수록 비용이 기하급수적으로 증가함)
### Horizontal Scale(scale out)
* 인스턴스 규모를 늘리는 것
    * 4GB의 CPU가 있을 때 16GB의 CPU가 필요하다면, 4GB를 4개로 놀리는 것
* 비용이 합리적이다.

## AWS Auto Scaling
* 애플리케이션을 모니터링하고 용량을 자동으로 조정하여, 최대한 저렴한 비용으로 안정적이고 예측 가능한 성능을 유지하는 것
*  즉, 자동으로 스케일링 서비스를 해주는 것

### 종류
* <span style="background:#FFE6E6">EC2 Auto Scaling (여기서 다룰 것)</span>
* DDB Auto Scaling
* Sot Fleet Autoo Scaling
* Aurora Auto Scaling
* ECS Auto Scaling

### EC2 Auto Scaling의 목표
* 정확한 수의 EC2 인스턴스를 보유하도록 보장하기 위해
    * 그룹의 최소 인스턴스 숫자 및 최대 인스턴스 숫자를 설정한다.
        * 최소 숫자 이하로 내려가지 않도록 인스턴스 갯수를 유지(이하의 경우 자동으로 인스턴스 추가)
        * 최대 숫자 이상 늘어나지 않도록 인스턴스 갯수를 유지(이상의 경우 자동으로 인스턴스 삭제)
    * 다양한 스케일링 정책 적용 가능
        * ex) CPU의 부하에 따라 인스턴스 크기 조정
    * 가용 영역에 인스턴스를 골고루 분산시켜서 서비스 장애를 예방한다.

### EC2 Auto Scaling의 구성
#### 시작 구성(launch configurations) / 시작 템플릿(launch template)
**무엇을 실행시킬 것인가? (EC2를 자동으로 추가해야할 경우 어떤 구성으로 되어있는 EC2를 추가하면 되는 지 설정하는 것**
* EC2의 타입, 사이즈
* AMI
* 보안 그룹, Key, IAM
* 유저 데이터
#### 모니터링
**언제 실행시킬 것인가? + 상태 확인**
* ex) CPU 점유율이 일정 %을 넘어섰을 때 EC2를 추가로 실행, EC2 2개 이상이 필요한 스택에서 하나가 죽었을 때 추가로 실행 등
* CloudWatch (and/or) ELB(Elastic Load Balance) 와 연계


#### 설정
**얼마나 어떻게 실행시킬 것인가?**
* 최대/최소/원하는 인스턴스 갯수
* ELB와 연동 등

## 실습하기