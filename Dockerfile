# 第一阶段：构建阶段（使用 JDK 17）
FROM m.daocloud.io/docker.io/eclipse-temurin:17-jdk-jammy as builder

# 设置工作目录
WORKDIR /app

# 复制构建文件（利用 Docker 层缓存优化）
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN mkdir -p /app/data
COPY ./knowledge_base.db /app/data/knowledge_base.db
RUN chmod a+rw /app/data/knowledge_base.db
# 构建项目并打包
RUN ./mvnw clean package -DskipTests

# 第二阶段：运行阶段（使用 JRE 17，更轻量）
FROM m.daocloud.io/docker.io/eclipse-temurin:17-jre-jammy

# 设置工作目录
WORKDIR /app

# 从构建阶段复制生成的 JAR 文件
COPY --from=builder /app/target/*.jar app.jar
COPY --from=builder /app/data/knowledge_base.db /app/data/knowledge_base.db
# 设置时区（可选）
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 使用非 root 用户运行（增强安全性）
RUN useradd -m appuser && chown -R appuser:appuser /app
USER appuser

# 暴露端口（与 Spring Boot 的 server.port 一致）
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
