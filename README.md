# 스프링부트2로 웹서비스 출시하기

## 06. AWS 생성 및 셋팅
1. 인스턴스 Amazon Linux AMI 선택
2. 스토리지 30GB
3. 태그 Name  springboot-aws-impl-alone 추가
4. 보안 그룹 추가 SSH 내IP, 사용자 지정 TCP 8080 추가
5. 키페어 생성 springboot-aws-impl-alone
6. 탄력적 IP 새주소 할당
7. 작업 - 주소연결

### EC2 접속하기

pem 키 .ssh 로 이동
```bash
cp pem.key ~/.ssh
chmod 600 ~/.ssh/pem.key
```

config 작성

```bash
vi ~/.ssh/config
```

```properties
# springboot-aws-impl-alonez
Host springboot-aws-impl-alone
    HostName ec2의 탄력적 IP 주소
    User ec2-user
    IdentityFile ~/.ssh/pem키 이름
```

퍼미션 옵션 변경
```bash
chmod 700 ~/.ssh/config
```

접속
```bash
ssh 서비스명
```

jdk 8 설치
```bash
sudo yum install -y java-1.8.0-openjdk-devel.x86_64
```

java 버전 8로 변경

```bash
sudo /usr/sbin/alternatives --config java
sudo yum remove java-1.7.0-openjdk
java -version
```

### 타임존 변경
```bash
sudo rm /etc/localtime
sudo ln -s /usr/share/zoneinfo/Asia/Seoul /etc/localtime
date
```

### Hostname 변경
```bash
sudo hostnamectl set-hostname springboot-aws-impl-alone
sudo reboot
```

/etc/hosts 에 HOSTNAME 등록
```bash
sudo vi /etc/hosts
```
```properties
127.0.0.1   springboot-aws-impl-alone
```

## 07. AWS RDS

MYSQL에서 MARIADB로 마이그레이션 해야 할 10가지 이유

MariaDB는 MySQL 대비 장점
- 동일 하드웨어 사양으로 MySQL보다 향상된 성능
- 좀 더 활성화된 커뮤니티
- 다양한 기능
- 다양한 스토리지 엔진

1. MariaDB 선택
2. 프리티어 선택
3. DB 인스턴스 식별자 - springboot-aws-impl-alone
4. 사용자 이름 암호 설정
5. 스토리지 범용(SSD) - 20
6. 퍼블릭 엑세스 가능 - 예
7. 초기 데이터베이스 이름 - springboot_aws_impl_alone

### RDS 운영환경에 맞는 파라미터 설정하기
- 타임존
- Character Set
- Max Connection

utf8과 utf8mb4의 차이는 이모지 저장 가능 여부

1. 파라미터 그룹 클릭
2. 파라미터 그룹 생성 클릭
3. 그룹이름, 설명 springboot-aws-impl-alone 입력 후 생성
4. 생성된 파라미터 편집 
    - time_zone - Asia/Seoul
    - character_set_client - utf8mb4
    - character_set_connection - utf8mb4
    - character_set_database - utf8mb4
    - character_set_filesystem - utf8mb4
    - character_set_results - utf8mb4
    - collation_connection - utf8mb4_general_ci
    - collation_server - utf8mb4_general_ci
    - max_connections - 150
5. 데이터베이스 선택 후 수정 클릭
    - DB 파라미터 그룹 위에 편집한 그룹 선택
    - 즉시 적용
    - 적용이 잘안되면 재부팅
6. VPC 보안 그룹 선택
    - MYSQL/Aurora 선택 ec2 보안 그룹 ID 복사해서 RDS 인바운드에 추가
    - MYSQL/Aurora 선택 인바운드에 내IP 추가
7. RDS 엔드포인트로 DB 접속

현재의 character_set, collation 설정 확인
```sql
show variables like 'c%'
```

변경이 안된 항목 직접 변경
```sql
ALTER DATABASE 데이터베이스명
CHARACTER SET = 'utf8mb4'
COLLATE = 'utf8mb4_general_ci'
```

타임 존 확인
```sql
select @@time_zone, now();
```

### EC2에서 RDS에서 접근 확인

EC2에 MySQL 접근 테스트를 위해 MySQL CLI 설치

```bash
sudo yum install mysql
```

RDS 접속
```bash
mysql -u 계정 -p -h Host 주소
mysql -u admin -p -h springboot-aws-impl-alone.caoklolkwzss.ap-northeast-2.rds.amazonaws.com
```

데이터베이스 목록 확인

```sql
show databases;
```

## 08. EC2 서버에 프로젝트를 배포해 보자
### EC2에 프로젝트 Clone 받기
```bash
sudo yum install git
git --version
mkdir ~/app && mkdir ~/app/step1
cd ~/app/step1
git clone https://github.com/freelife1191/springboot-aws-impl-alone.git
cd springboot-aws-impl-alone
```

테스트로 검증
```bash
./gradlew test
```

### 배포 스크립트 만들기
```bash
vi ~/app/step1/deploy.sh
```

scripts/step1_deploy.sh 내용 복사해서 붙여넣기


```bash
chmod +x ./deploy.sh
./deploy.sh
cat nohup.out
```

### 외부 Security 파일 등록하기

```bash
vi /home/ec2-user/app/application-oauth.yml
```

deploy.sh 수정

```bash
# 계속 구동될 수 있도록 nohup 로 구동
# -Dspring.config.location 스프링 파일 위치를 지정
# application-oauth.yml 은 외부에 파일이 있기 때문에 절대경로를 사용
nohup java -jar \
    -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/application-oauth.yml \
    $REPOSITORY/$JAR_NAME 2>&1 &
```

### 스프링 부트 프로젝트로 RDS 접근하기
- 테이블 생성
- 프로젝트 설정: MariaDB에서 사용 가능한 드라이버 추가
- EC2 설정: 서버 내부에서 접속 정보를 관리하도록 설정

1. 테스트 코드 수행 시 로그로 생성되는 쿼리를 사용하여 테이블 생성
2. File 검색(cmd + shift + O) 스프링 세션 테이블 생성 SQL schema-mysql.sql 파일 찾기
3. 서버에서 구동될 환경 설정파일 추가 src/main/resources/application-real.yml
4. app 디렉토리에 application-real-db.properties 파일 생성
5. nohup 스크립트 추가 수정
6. `curl localhost:8080` 테스트

### EC2에서 소셜 로그인하기
- AWS 보안 그룹 8080 열려있는지 확인
- AWS EC2 도메인으로 접속
   - 인스턴스의 퍼블릭 IPv4 DNS로 접속
- 구글에 EC2 주소 등록
- 네이버에 EC2 주소 등록
   - 서비스 URL
      - 로그인을 시도하는 서비스가 네이버에 등록된 서비스인지 판단하기 위한 항목
      - 포트는 제외하고 도메인만 입력
      - EC2 주소 등록시 localhost는 안됨
      - 개발 단계에서는 등록하지 않는 것을 추천
      - localhost도 하고 싶으면 네이버 서비스를 하나 더 생성해서 키를 발급
   
## 09. Travis CI 배포 자동화 - 코드가 푸시되면 자동으로 배포해 보자
마틴 파울러의 CI 4가지 규칙
- 모든 소스 코드가 살아 있고(현재 실행되고) 누구든 현재의 소스에 접근할 수 있는 단일 지점을 유지할 것
- 빌드 프로세스를 자동화해서 누구든 소스로부터 시스템을 빌드하는 단일 명령어를 사용할 수 있게 할 것
- 테스팅을 자동화해서 단일 명령어로 언제든지 시스템에 대한 건전한 테스트 수트를 실행할 수 있게 할 것
- 누구나 현재 실행 파일을 얻으면 지금까지 가장 완전한 실행 파일을 얻었다는 확신을 하게 할 것

### Travis CI 연동하기
깃허브에서 제공하는 무료 CI 서비스

Travis CI 웹서비스 설정

1. https://travis-ci.org 접속 후 계정명 -> Setting 클릭
2. Sync account 클릭
3. springboot-aws-impl-alone 깃허브 저장소 활성화

프로젝트 설정

1. .travis.yml 생성
2. push 후 travis 웹에서 확인

### Travis CI 와 AWS S3 연동하기
Travis CI 연동시 구조

![9-1](img/freelec-springboot-chap9-1.jpeg)

1. AWS Key 발급
   - IAM - 사용자 - 사용자 추가
   - 사용자 이름 freelect-travis-deploy 기입
   - 프로그래밍 방식 엑세스 체크
   - 기존 정책 직접 연결 선택
   - AmazonS3FullAccess 체크
   - AWSCodeDeployFullAccess 체크
   - 태그 Name - freelect-travis-deploy 추가
2. Travis CI에 키 등록
   - Travis CI에서 More Options 클릭후 Settings
   - Environment Variables 에 AWS_ACCESS_KEY, AWS_SECRET_KEY 등록
3. S3 버킷 생성
   - 버킷 이름 freelect-springboot-build
   - 모든 퍼블릭 엑세스 차단 체크
4. .travis.yml 추가

### Travis CI와 AWS S3, CodeDeploy 연동하기
EC2에 IAM 역할 추가하기

- 역할
   - AWS 서비스에만 할당할 수 있는 권한
   - EC2, CodeDeploy, SQS 등
- 사용자
   - AWS 서비스 외에 사용할 수 있는 권한
   - 로컬 PC, IDC 서버 등

1. 역할 - 역할 만들기
2. AWS 서비스 - EC2 
3. AmazonEc2RoleforAWSCodeDeploy
4. 태그 본인이 원하는 이름으로 추가 ec2-codedeploy-role
5. 검토에서 마지막으로 역할의 이름을 등록하고 나머지 등록 정보를 최종적으로 확인

EC2 서비스에 등록

1. EC2 인스턴스 목록으로 이동
2. 마우스 우클릭 후 보안 -> IAM 역할 수정 - 생성한 역할 선택
3. 인스턴스 재부팅

CodeDeploy 에이전트 설치

1. EC2에 접속해서 입력해서 에이전드 다운로드

```bash
aws s3 cp s3://aws-codedeploy-ap-northeast-2/latest/install . --region ap-northeast-2
```

2. 실행권한 추가
```bash
chmod +x ./install
```

3. ruby 설치
   
```bash
sudo yum install ruby -y
```

4. install 파일로 설치 진행

```bash
sudo ./install auto
```

5. Agent가 정상적으로 실행되고 있는지 상태 검사

```bash
sudo service codedeploy-agent status
```

### CodeDeploy를 위한 권한 생성
CodeDeploy에서 EC2에 접근하려면 권한이 필요

1. AWS에서 IAM 역할 생성 역할 - 역할 만들기 - AWS 서비스 - CodeDeploy 선택
2. 태그 추가 Name - codedeploy-role 
3. 검토 역할 이름 codedeploy-role

### CodeDeploy 생성
- Code Commit
   - 깃허브와 같은 코드 저장소의 역할
   - 프라이빗 기능을 지원한다는 강점이 있지만 현재 깃허브에서 **무료로 프라이빗 지원**을 하고 있어서 거의 사용되지 않음
- Code Build
   - Travis CI와 마찬가지로 **빌드용 서비스**
   - 멀티 모듈을 배포해야 하는 경우 사용해 볼만하지만 규모가 있는 서비스에서는 대부분 젠킨스/팀시티 등을 이용하니 이것 역시 사용할 일이 거의 없음
- CodeDeploy
   - AWS의 배포 서비스
   - 앞에서 언급한 다른 서비스들은 대체재가 있지만 CodeDeploy는 **대체재가 없음**
   - 오토 스케일링 그룹 배포, 블루 그린 배포, 롤링 배포, EC2 단독 배포 등 많은 기능을 지원
   
1. CodeDeploy 서비스에서 애플리케이션 생성 버튼 클릭
2. 애플리케이션 이름 - springboot-aws-impl-alone EC2/온프레미스 선정택 후 생성
3. 배포 그룹 생성 버튼 클릭 
4. 배포 그룹 이름 입력 springboot-aws-impl-alone-group 
5. 서비스 역할 선택 codedeploy-role
6. 배포 유형 현재 위치 선택
7. 환경 구성 Amazon Ec2 인스턴스 체크
8. 키 Name , 값 springboot-aws-impl-alone 태그 추가
9. 배포 설정 - CodeDeployDefault.AllAtOnce
10. 로드 밸런싱 활성화 체크 해제
11. 배포 그룹 생성 완료

### Travis CI, S3, CodeDeploy 연동

Travis CI의 Build가 끝나면 S3에 zip 파일이 전송되고
zip 파일은 /home/ec2-user/app/step2/zip로 복사되어 압출을 풀 예정

Travis CI의 설정은 .travis.yml로 진행
AWS CodeDeploy의 설정은 appspec.yml로 진행

1. EC2 접속해서 디렉토리 생성

```bash
mkdir ~/app/step2 && mkdir ~/app/step2/zip
```

2. .travis.yml codedeploy deploy 스크립트 추가
3. .appspec.yml 파일 추가
3. 푸시 및 배포 테스트

### 배포 자동화 구성
1. deploy.sh step2 추가
2. .travis.yml 파일 before_deploy 스크립트 수정
3. appspec.yml 파일 수정
4. build.gradle version 업데이트

### 로그 확인

디렉토리 명은 CodeDeploy ID

로그 확인 디렉토리
```bash
cd /opt/codedeploy-agent/deployment-root
```