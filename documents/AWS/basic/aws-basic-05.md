# IAM 기초
## IAM
- Identity and Access Management : AWS 서비스와 리소스에 대한 액세스 관리, 허용 & 거부(Access control)
- AWS 계정 관리 및 리소스, 사용자, 서비스의 권한 제어
- 사용자 생성 및 관리 및 계정 보안
- 다른 계정과 리소스 공유
- 계정에 별칭 부여 -> 로그인 url주소 생성 가능
- 글로벌 서비스임 (Region 서비스가 아님)

## IAM 구성
- 사용자 : 실제 AWS를 사용하는 사람 or 애플리케이션
- 그룹
    - 사용자의 집합
    - 그룹에 속한 사용자는 그룹에 부여된 권한 행사 가능
- 정책(Policy)
    - 사용자와 그룹, 역할이 언제, 어디서, 무엇을, 어떻게 할 수 있는지에 관한 문서
    - JSON 형식으로 정의
- 역할(Role)
    - AWS 리소스에 부여하는 AWS 리소스가 무엇을 할 수 있는지를 정의
    혹은 다른 사용자가 역할을 부여 받아 사용
    다른 자격에 대해서 신뢰관계를 구축 가능
    역할을 바꾸어 가며 서비스를 사용 가능

## IAM 빌링 조회 활성화 시키기
1. 루트 유저로 로그인
2. 우측 상단 별칭을 클릭하고 `계정` 클릭
3. 스크롤을 내리면 `결제 정보에 대한 IAM 사용자 및 역할 액세스 ` 섹션에 액세스 비활성화 되어 있는 것 확인
4. `편집` 버튼 클릭해서 활성화로 변경해주기
* 콘솔을 처음 방문하고 24시간이 지나야 확인 가능

## IAM 자격 증명 보고서 (Credential Report)
* 계정의 모든 사용자와 암호, 액세스 키, MFA 장치 등의 증명 상태를 나열하는 보고서를 생성하고 다운로드 가능
* 4시간에 한 번씩 생성 가능
* AWS 콘솔, CLI, API에서 생성 요청 및 다운로드 가능
* 포함되는 정보
  > * 암호 (활성화 여부, 마지막으로 사용한 시간, 마지막으로 변경된 시간, 변경되어야 하는 시간)
  > * 액세스 키 (활성화 여부, 마지막으로 사용된 시간, 마지막으로 변경된 시간, 마지막으로 사용된 서비스)
  > * 기타 (MFA 사용 여부, 사용자 생성 시간)

### 자격 증명 보고서 다운 받기
* IAM 서비스 화면에서 좌측 메뉴에 `자격 증명 보고서` 클릭하고 다운받으면 csv 파일로 다운받아 지는데 엑셀로 열면 편하게 볼 수 있다.

## IAM 모범 사용 사례
- 루트 사용자는 사용하지 않기
- 불필요한 사용자는 만들지 않기/사용하지 않는 사용자는 바로바로 삭제 하기
    - 나중가서 이 사용자가 쓰는 건지 아닌지 몰라서 삭제하기도 애매해 질 수 있음
- 가능하면 그룹과 정책을 사용하기
- 최소한의 권한만을 허용하는 습관 들이기 (Principle of least privilege)
- MFA를 활성화 하기
- AccessKey 대신 역할 활용
- IAM 자격 증명 보고서 활용하기


### Reference
-  https://www.youtube.com/playlist?list=PLfth0bK2MgIan-SzGpHIbfnCnjj583K2m