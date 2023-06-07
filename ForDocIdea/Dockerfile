FROM tomcat:9-jre17-temurin

ARG CACHEBUST=1

RUN rm -rf /usr/local/tomcat/webapps/CTI*
COPY ./target/CTI-1.0.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]

