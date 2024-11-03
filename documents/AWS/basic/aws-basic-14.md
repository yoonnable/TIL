# Elastic File System
> * AWS 클라우드 서비스와 온프레미스 리소스에서 사용할 수 있는 간단하고 확장 가능하며 탄력적인 완전관리형 NFS 파일 시스템을 제공한다.
> * 애플리케이션을 중단하지 않고 온디맨드 방식으로 페타바이트 규모까지 확장하도록 구축되어, 파일을 추가하고 제거할 때 자동으로 확장하고 축소하여 확장 규모에 맞게 용량을 프로비저닝 및 관리할 필요가 없다.

## Amazon EFS
* NFS 기반 공유 스토리지 서비스
    * 따로 용량을 지정할 필요 없이 사용한 만큼 용량이 증가 <-> EBS는 미리 크기를 지정해야 한다.
* 페타바이트 단위까지 확장 가능
* 몇 천개의 동시 접속 유지 가능(다양한 인스턴스틀이 하나의 EFS에 접속하는 게 기본이므로 동시 접속은 필수!)
* 데이터는 여러 AZ(가용영역)에 나누어 분산 저장
* **쓰기 후 읽기**의 일관성
* Private Service: AWS 외부에서 접속 불가능
    * AWS 외부에서 접속하기 위해서는 VPN혹은 AWS Direct Connect 등으로 별도로 VPC와 연결 필요
* 각 가용영역에 Mount Target를 두고 각각의 가용영역에 해당 Mount Target로 접근
* Linux Only

## 퍼포먼스 모드
### Amazon EFS 퍼포먼스 모드
* General Purpose: 가장 보편적인 모드. 거의 대부분의 경우 사용 권장
* Max IO: 매우 높은 IOPS가 필요한 특수한 경우
    * ex) 빅데이터, 미디어 처리 등

## Throughput 모드
### Amazon EFS Throughput 모드
* Bursting Throughput : 닞은 Throughput일 때 크레딧을 모아서 높은 Throughput일 때 사용
    * EC2 T타입과 비슷한 개념
* Provisioned Throughput: 미리 지정한 만큼의 Throughput을 미리 확보해두고 사용
## 스토리지 클래스
### Amazon EFS 스토리지 클래스
* EFS Standard: 3개 이상의 가용영역에 보관
* EFS Standard-IA: 3개 이상의 가용영역에 보관, 조금 저렴한 비용 대신 데이터를 가져올 때 비용 발생
* EFS One Zone: 하나의 가용영역에 보관 -> 저장된 가용영역의 상황에 영향을 받을 수 있음(가용영역이 날라가면 모두 사라짐..)
* EFS One Zone-IA: 저장된 가용영역의 상황에 영향을 받을 수 있음. 데이터를 가져올 때 비용 발생(가장 저렴)

## 실습
### EFS를 활용한 스토리지 공유 웹서버 만들기
* Amazon EFS를 위한 보안 그룹 생성
* Amazon EFS 생성
* EC2 인스턴스 개 프로비전
    * 유저 데이터
        * 생성한 EFS를 마운트
        * 웹 서버 생성/실행
* 각 웹서버에서 스토리지를 공유하는 것을 확인!
* 리소스 정리!!!
