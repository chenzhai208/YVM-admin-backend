# syntax=docker/dockerfile:1

# —— 构建阶段：使用 Maven 编译 WAR ——
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
# 先把依赖下载好，加速后续构建
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package


# —— 运行阶段：使用 Tomcat 运行 WAR ——
FROM tomcat:10.1-jdk17

ENV PORT=8080
EXPOSE 8080

# 删除默认 webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# 将编译出来的 WAR 作为 ROOT.war 部署
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
