@echo off
set JAVA_OPTS=-Dspring.profiles.active=default -Dspring.output.ansi.enabled=ALWAYS
java %JAVA_OPTS% -jar target/kafka-avro-demo-1.0-SNAPSHOT.jar
pause 