@echo off
set JAVA_HOME=C:\Users\ADMIN\.jdks\temurin-21.0.8
set PATH=%JAVA_HOME%\bin;%PATH%
echo [INFO] Using Java 21 from: %JAVA_HOME%
java -version
echo [INFO] Starting Momo Sensei Backend...
call mvnw.cmd spring-boot:run
pause
