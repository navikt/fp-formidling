FROM navikt/java:17-appdynamics

LABEL org.opencontainers.image.source=https://github.com/navikt/fp-formidling
ENV APPD_ENABLED=true

RUN mkdir lib
RUN mkdir conf

# Config
COPY web/target/classes/logback*.xml conf/

# Application Container (Jetty)
COPY web/target/app.jar .
COPY web/target/lib/*.jar ./

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/urandom \
    -Duser.timezone=Europe/Oslo \
    -Dlogback.configurationFile=conf/logback.xml"

# Export vault properties
COPY .scripts/03-import-appd.sh /init-scripts/03-import-appd.sh
COPY .scripts/05-import-users.sh /init-scripts/05-import-users.sh
