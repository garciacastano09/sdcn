FROM bitnami/tomcat:9.0

#COPY ./target/sdcn2017 /bitnami/tomcat/data/sdcn2017
COPY ./target/sdcn2017.war /bitnami/tomcat/data/sdcn2017.war
EXPOSE 8080