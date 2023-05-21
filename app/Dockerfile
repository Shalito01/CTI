FROM tomcat:9-jre17-temurin

RUN rm -rf /usr/local/tomcat/CTI*
COPY ./target/CTI-1.0.war /usr/local/tomcat/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]

