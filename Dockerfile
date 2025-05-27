FROM ghcr.io/navikt/fp-baseimages/java:21
LABEL org.opencontainers.image.source=https://github.com/navikt/fp-formidling

# Config
COPY target/classes/logback*.xml ./conf/

# Application Container (Jetty)
COPY target/lib/*.jar ./lib/
COPY target/app.jar .

