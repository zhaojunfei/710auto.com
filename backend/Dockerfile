# ============ 构建阶段 ============
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ============ 运行阶段 ============
FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=Asia/Shanghai
ENV JAVA_OPTS="-Xms512m -Xmx1024m -Dfile.encoding=UTF-8"
COPY --from=build /build/target/auto710-backend.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
