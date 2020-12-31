# 스프링부트2로 웹서비스 출시하기

## AWS 생성 및 셋팅
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