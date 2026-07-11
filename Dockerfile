# Build stage: compile Java source code with Maven
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /src

RUN apt-get update && apt-get install -y --no-install-recommends maven curl \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /root/.m2 && \
    echo '<settings><mirrors><mirror><id>aliyun</id><mirrorOf>*</mirrorOf><url>https://maven.aliyun.com/repository/public</url></mirror></mirrors></settings>' > /root/.m2/settings.xml

COPY pom.xml /src/pom.xml
COPY src/ /src/src/
COPY application.properties /src/application.properties

RUN mvn -DskipTests clean package -q

# Runtime stage: minimal JRE image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /src/target/kefu-snapdeploy-1.0.0.jar /app/app.jar
COPY --from=builder /src/application.properties /app/application.properties

EXPOSE 8080

ENV JAVA_OPTS="-Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseG1GC -XX:+UseStringDeduplication -XX:ActiveProcessorCount=1 -XX:MaxGCPauseMillis=200"

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar --spring.config.additional-location=file:/app/application.properties"]
