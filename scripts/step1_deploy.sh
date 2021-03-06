#!/bin/bash

REPOSITORY=/home/ec2-user/app/step1
PROJECT_NAME=springboot-aws-impl-alone

cd $REPOSITORY/$PROJECT_NAME/

echo "> Git Pull"

git pull

echo "> 프로젝트 Build 시작"

./gradlew build

echo "> step1 디렉토리로 이동"

cd $REPOSITORY

echo "> Build 파일 복사"

cp $REPOSITORY/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"

# pgrep는 process id 추출 -f 옵션으로 프로세스 이름 찾기
CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

echo "현재 구동중인 어플리케이션 pid: $CURRENT_PID"

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
    echo "> kill -15 $CURRENT_PID"
    kill -15 $CURRENT_PID
    sleep 5
fi

echo "> 새 어플리케이션 배포"

# 가장 마지막 jar 파일명 찾기
JAR_NAME=$(ls -tr $REPOSITORY/ | grep '.*.jar' | tail -n 1)

echo "> JAR Name: $JAR_NAME"

# 계속 구동될 수 있도록 nohup 로 구동
# -Dspring.config.location 스프링 파일 위치를 지정
# application-oauth.yml 은 외부에 파일이 있기 때문에 절대경로를 사용
# -Dspring.profiles.active=real real profile 를 활성화 시킴
nohup java -jar \
    -Dspring.config.location=classpath:/application.yml,/home/ec2-user/app/application-oauth.yml,/home/ec2-user/app/application-real-db.yml \
    -Dspring.profiles.active=real \
    $REPOSITORY/$JAR_NAME 2>&1 &