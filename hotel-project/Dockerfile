FROM tomcat
RUN rm -rf /usr/local/tomcat/webapps/*
COPY hotel/target/hotel-app.war /usr/local/tomcat/webapps/hotel-app.war
EXPOSE 8080
