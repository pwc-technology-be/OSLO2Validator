FROM maven:3.5-jdk-8-alpine AS build

WORKDIR /app
COPY pom.xml /app/pom.xml
COPY src/ /app/src
RUN mvn clean package

FROM tomcat:9-jre8-alpine AS config

# This configures tomcat to write the access logs to stdout and sets a format which includes the x-request-id
RUN apk --update add xmlstarlet
RUN  cat /usr/local/tomcat/conf/server.xml | xml ed -d "/Server/Service/Engine/Host/Valve" \
      -s "/Server/Service/Engine/Host" -t elem -n "Valve" \
      -a "/Server/Service/Engine/Host/Valve" -t attr -n "className" -v "org.apache.catalina.valves.AccessLogValve" \
      -a "/Server/Service/Engine/Host/Valve" -t attr -n "directory" -v "/proc/self/fd" \
      -a "/Server/Service/Engine/Host/Valve" -t attr -n "prefix" -v "1" \
      -a "/Server/Service/Engine/Host/Valve" -t attr -n "suffix" -v "" \
      -a "/Server/Service/Engine/Host/Valve" -t attr -n "rotatable" -v "false" \
      -a "/Server/Service/Engine/Host/Valve" -t attr -n "pattern" -v "%{x-request-id}i - %h %l %u %t \"%r\" %s %b" \
    > /usr/local/tomcat/conf/server-configured.xml

FROM tomcat:9-jre8-alpine

WORKDIR /usr/local/tomcat/webapps
RUN rm -rf ./ROOT/
COPY --from=config /usr/local/tomcat/conf/server-configured.xml /usr/local/tomcat/conf/server.xml
COPY --from=build /app/target/*.war ./ROOT.war

