#!/usr/bin/env bash

# Enables remote debugging on port 5005 if the application is running in a development cluster

if [ "$NAIS_CLUSTER_NAME" = "dev-fss" ] || [ "$NAIS_CLUSTER_NAME" = "dev-gcp" ]; then
  export JAVA_OPTS="${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"
fi
