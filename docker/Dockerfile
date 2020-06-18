FROM tomcat:9-jdk11-adoptopenjdk-openj9

ENV DB_HOST localhost
ENV DB_PORT 3306
ENV DB_DATABASE oagi
ENV DB_USERNAME oagi
ENV DB_PASSWORD oagi
ENV REDIS_HOST localhost
ENV REDIS_PORT 6379

COPY wait-for-it.sh /wait-for-it.sh
COPY mysql-connector-java-8.0.20.jar /usr/local/tomcat/lib/mysql-connector-java-8.0.20.jar

RUN ["rm", "-rf", "/usr/local/tomcat/webapps/ROOT"]
COPY score-http-2.0.0.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]
