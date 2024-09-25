# Spring Boot 프로젝트 AWS EC2 배포
* Spring Boot + Java 웹 프로젝트
* MySQL, Elasticsearch, kafka와 함께 배포해야 하는 프로젝트

## 1. AWS EC2 인스턴스 설정
### 1) AWS EC2 서버(인스턴스) 생성
* AMI : Ubuntu LTS 버전 또는 Amazon Linux 2 추천. (프리 티어 사용 가능 한 것으로)
* 인스턴스 유형 : t2.micro (프리 티어 사용 가능한 것으로)
    * MySQL, Elasticsearch, Kafka를 함께 사용하려면 메모리와 CPU가 충분해야 함..
* 키 페어 필수
* 보안 그룹 : SSH(22), HTTP(80), HTTPS(443) 포트와 MySQL(3306), Kafka(9092) 포트를 열어줘야 함.
* 스토리지 : 전체 용량 30GB 이내, 범용 or 마그네틱 (프리 티어)

### 2) EC2 접속 전 탄력적 IP 할당 후 연결하기
> ### IP
> #### 프라이빗 IP
> * 내부(사내)에서 접근 가능한 IP(진짜 주소)
> * 내부에서는 데이터를 자유롭게 주고 받을 수 있다.
> #### 퍼블릭 IP
> * 외부에서 접근 가능한 IP
> * 동적 IP로 인스턴스를 접속 할 때마다 IP가 변경되어 있는다.
> * 동적 IP의 불편함을 해소하기 위해 **탄력적 IP**를 생성해 사용할 수 있다.
* 탄력적 IP > 탄력적 IP 주소 할당 > 할당
* 할당한 탄력적 IP 선택 후 작업 > 탄력적 IP 주소 연결
* 인스턴스 선택 > 프라이빗 IP 주소 선택 > 재연결 허용 > 연결

### 3) 접속 쉘 파일 만들기
우선 내 컴퓨터에 특정 폴더(ex deploy) 만들어서, .sh파일(ex live_server_sh.sh) 하나 만들고, 편집 툴 이용해서 파일 열기
```bash
#!/bin/bash

# ssh: Secure Shell 명령어로, 원격 서버에 안전하게 접속하기 위한 프로토콜입니다.
# -i: ssh 명령어에 옵션으로 사용되는 플래그로, 특정한 private key 파일을 지정합니다.
# D:/Develop/keyFiles/MyWebServerKeypair.pem: 접속에 사용할 private key 파일의 경로입니다.
# ubuntu: 원격 서버에서 사용할 사용자 이름입니다.
# 15.165.24.225: 접속하려는 원격 서버의 탄력적 IP 주소입니다.
ssh -i D:/Develop/keyFiles/MyWebServerKeypair.pem ubuntu@15.165.24.225
```
* 위와 같이 작성 후 저장
* 위 폴더에서 git bash를 실행하고, 위 .sh 파일을 끌어오면 자동으로 위 파일에 작성한 명령어가 실행이 된다.

### 4) 최신 상태로 업그레이드 해주기
```bash
# sudo = super do : 슈퍼 계정으로 하겠다는 의미
sudo su #관리자 권한으로 접속

# apt : 패키지 관리자 ex) spring -> maven, gradle
apt update # 해당 패키지의 최신 버전을 가져온다.

apt upgrade
```

## 2. 필요한 소프트웨어 설치
### 1) Java 설치
* Spring Boot 프로젝트를 실행하려면 Java가 필요
```bash
apt install openjdk-17-jdk -y # java17 설치
java -version # 자바 버전 확인
```

### 2) MySQL 설치
```bash
apt install mysql-server -y # MySQL 서버를 설치. '-y' 옵션은 설치 시 모든 질문에 대해 'yes'라고 자동으로 응답

systemctl start mysql # MySQL 서비스 시작

# MySQL 서비스를 부팅 시 자동으로 시작하도록 설정
# 이 명령어를 통해 서버가 재부팅될 때 MySQL 서비스가 자동으로 실행됨
systemctl enable mysql 
```

### 3) MySQL 설정
* MySQL에 접속하여 유저 생성 및 데이터베이스 설정
```bash
# MySQL 루트 사용자로 MySQL 셸에 접속합니다. '-u' 옵션은 사용자 이름을 지정합니다.
sudo mysql -u root

# 'your_db_name'이라는 이름의 데이터베이스를 생성. 
# 데이터베이스 이름은 원하는 대로 변경할 수 있다.
CREATE DATABASE `your_db_name`;

# 'your_user'라는 이름의 새로운 사용자를 생성. 
# '%'는 모든 호스트에서 접속할 수 있도록 허용.
# 비밀번호는 'your_password'로 설정.
CREATE USER 'your_user'@'%' IDENTIFIED BY 'your_password';

# 생성한 사용자에게 'your_db_name' 데이터베이스에 대한 모든 권한을 부여.
# '%'는 모든 호스트에서 접근 가능하다는 의미
GRANT ALL PRIVILEGES ON your_db_name.* TO 'your_user'@'%';

# 변경된 권한을 MySQL 서버에 적용.
# 이 명령어는 권한 테이블을 재로드합니다.
FLUSH PRIVILEGES;

exit; # 나가기
```

### 4) Elasticsearch 설치
1. APT HTTPS 지원 패키지 설치
    * APT 패키지 관리자가 HTTPS를 통해 패키지를 다운로드할 수 있도록 지원하는 패키지를 설치합니다.
    ```bash
    apt install apt-transport-https
    ```
2. Elasticsearch GPG 키 추가
    * Error 발생
    ```bash
    wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -
    ```
3. Elasticsearch 저장소 추가
    * 8.x 버전의 저장소를 [/etc/apt/sources.list.d에 추가](#1-왜-etcaptsourceslistd-여기에-추가하는-건가)
    ``` bash
    sh -c 'echo "deb https://artifacts.elastic.co/packages/8.x/apt stable main" > /etc/apt/sources.list.d/elastic-8.x.list'
    ```
4. 패키지 목록 업데이트
    ```bash
    apt update
    ```
5. Elasticsearch 설치
    ```bash
    apt install elasticsearch
    ```

6. Elasticsearch 서비스 시작
    * Elasticsearch를 설치한 후, 서비스를 시작하고 부팅 시 자동으로 시작되도록 설정
    ```bash
    systemctl start elasticsearch
    systemctl enable elasticsearch
    ```

7. Elasticsearch 상태 확인
    * Elasticsearch가 제대로 실행되고 있는지 확인하려면 아래 명령어를 사용
    ```bash
    systemctl status elasticsearch
    ```

8. 테스트
    * Elasticsearch가 정상적으로 작동하는지 확인하려면 브라우저나 curl을 사용하여 아래 URL에 접속
    ```bash
    curl -X GET "localhost:9200/"
    ```

### 5) Elasticsearch 설정

### 6) Kafka 다운로드
### 7) Zookeeper와 Kafka 시작







## 3. 프로젝트 준비
### 1) 빌드
* intallJ > Gradle > Tasks > build > bootJar
### 2) jar 파일 확인
* 프로젝트 경로\build\libs 에서 .jar 파일 확인












##### [1] 왜 /etc/apt/sources.list.d 여기에 추가하는 건가?
* sources.list.d 디렉토리는 APT 패키지 소스를 별도의 파일로 관리할 수 있게 해줍니다. 이를 통해 여러 저장소를 파일별로 구분하여 관리할 수 있어, 패키지 소스를 더 조직적으로 유지할 수 있습니다.
* 각 저장소를 개별 파일로 관리하면, 특정 저장소를 추가하거나 삭제할 때 편리합니다. 예를 들어, Elasticsearch의 저장소는 elastic-8.x.list라는 파일로 관리하면 쉽게 추가하거나 삭제할 수 있습니다.

## 문제 해결
#### Elasticsearch GPG 키 추가 명령어 실행 시 에러 발생















### Reference
* https://www.youtube.com/watch?v=fBzR6DqP5jk&t=396s
* https://www.youtube.com/watch?v=c2lC3qc7UwY
