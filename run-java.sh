#!/usr/bin/env sh
set -eu

export JAVA_OPTS="${JAVA_OPTS:-} -Djava.security.egd=file:/dev/./urandom"

export STARTUP_CLASS=${STARTUP_CLASS:-"no.nav.foreldrepenger.fpformidling.web.server.jetty.JettyServer"}
export CLASSPATH=app.jar:lib/*
export LOGBACK_CONFIG=${LOGBACK_CONFIG:-"./conf/logback.xml"}

exec java -cp ${CLASSPATH:-"app.jar:lib/*"} ${DEFAULT_JAVA_OPTS:-} ${JAVA_OPTS} -Dlogback.configurationFile=${LOGBACK_CONFIG?} -Dconf=${CONF:-"./conf"}  -Dwebapp=${WEBAPP:-"./webapp"} -Dapplication.name=${APP_NAME} ${STARTUP_CLASS?} $@
