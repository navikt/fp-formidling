FROM navikt/java:11-appdynamics

ENV APPD_ENABLED=true
ENV APP_NAME=fpformidling
ENV APPDYNAMICS_CONTROLLER_HOST_NAME=appdynamics.adeo.no
ENV APPDYNAMICS_CONTROLLER_PORT=443
ENV APPDYNAMICS_CONTROLLER_SSL_ENABLED=true

RUN mkdir /app/lib
RUN mkdir /app/webapp
RUN mkdir /app/conf

# Config
COPY web/webapp/target/classes/logback.xml /app/conf/
COPY web/webapp/target/classes/jetty/jaspi-conf.xml /app/conf/

# Application Container (Jetty)
COPY web/webapp/target/app.jar /app/
COPY web/webapp/target/lib/*.jar /app/lib/

# Export vault properties
COPY export-vault.sh /init-scripts/export-vault.sh

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Europe/Oslo "