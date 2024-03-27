FROM openjdk:8-jdk-alpine

# Environment variables
ENV TOMCAT_MAJOR=8 \
    TOMCAT_VERSION=8.5.37 \
    CATALINA_HOME=/opt/tomcat
#
#RUN mkdir /var/lib/docker/overlay2/ye9onjasjdspb7s41rkoe4lpm/merged/java

COPY . /var/www/html
WORKDIR /var/www/html
EXPOSE 80
#FROM tomcat:10.1.15
#
#COPY ./target/AstonREST-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/

