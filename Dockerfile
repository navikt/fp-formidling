FROM navikt/java:17-appdynamics

LABEL org.opencontainers.image.source=https://github.com/navikt/fp-formidling
ENV TZ=Europe/Oslo
ENV APPD_ENABLED=true

RUN mkdir lib
RUN mkdir conf

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/urandom \
    -Duser.timezone=Europe/Oslo \
    -Dlogback.configurationFile=conf/logback.xml"

# Export vault properties
COPY --chown=apprunner:root .scripts/03-import-appd.sh /init-scripts/03-import-appd.sh
COPY --chown=apprunner:root .scripts/05-import-users.sh /init-scripts/05-import-users.sh
COPY --chown=apprunner:root .scripts/08-remote-debug.sh /init-scripts/08-remote-debug.sh


# Config
COPY web/target/classes/logback*.xml conf/

# Application Container (Jetty)
COPY web/target/lib/*.jar ./
COPY web/target/app.jar .
