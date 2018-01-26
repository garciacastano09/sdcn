FROM bitnami/tomcat:9.0

COPY ./target/sdcn2017 /bitnami/tomcat/data/sdcn2017
EXPOSE 8080