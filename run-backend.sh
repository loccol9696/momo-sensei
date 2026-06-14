#!/bin/bash
export JAVA_HOME="C:\\Users\\ADMIN\\.jdks\\temurin-21.0.8"
export PATH="$JAVA_HOME/bin:$PATH"
echo "[INFO] Using Java 21 from: $JAVA_HOME"
java -version
echo "[INFO] Starting Momo Sensei Backend..."
./mvnw spring-boot:run
