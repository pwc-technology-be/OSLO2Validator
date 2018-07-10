FROM maven:3.5-jdk-8-alpine AS build

WORKDIR /app
COPY pom.xml /app/pom.xml
COPY src/ /app/src
RUN mvn clean package

FROM tomcat:9-jre8-alpine

WORKDIR /usr/local/tomcat/webapps
RUN rm -rf ./ROOT/
COPY --from=build /app/target/*.war ./ROOT.war

