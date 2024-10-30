# 使用官方的Tomcat镜像作为基础镜像
FROM tomcat:9-jdk8

# 设置工作目录
WORKDIR /usr/local/tomcat

# 删除webapps目录下的内容，包括原有的ROOT应用
RUN rm -rf webapps/*

# 从本地target目录复制并重命名war包到镜像的webapps目录中
ARG WAR_FILE
COPY target/${WAR_FILE} webapps/ROOT.war

# 安装 xmlstarlet 并设置AccessLogValve maxDays
# RUN apt-get update && apt-get install -y xmlstarlet && \
#     xmlstarlet ed --inplace \
#     -s "Server/Service/Engine/Host/Valve[@className='org.apache.catalina.valves.AccessLogValve']" -t attr -n maxDays -v "30" \
#     conf/server.xml && \
#     sed -i 's/<Connector port="8080" protocol="HTTP\/1.1"/<Connector port="8080" protocol="HTTP\/1.1" maxPostSize="104857600"/g' conf/server.xml

# 暴露端口
EXPOSE 8080

# Tomcat容器启动后会自动运行，无需添加ENTRYPOINT