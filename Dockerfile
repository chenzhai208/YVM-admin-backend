FROM tomcat:10.1-jdk17

# 可选：清空默认示例应用
RUN rm -rf /usr/local/tomcat/webapps/*

# 将构建好的 WAR 放为 ROOT（使应用运行在 /）
COPY target/contact.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
