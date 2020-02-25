#!/usr/bin/env bash

echo "Kjører  $0"

if test -f /var/run/secrets/nais.io/srvfpformidling/username;
then
   export  SYSTEMBRUKER_USERNAME=$(cat /var/run/secrets/nais.io/srvfpformidling/username)
   echo "Setter SYSTEMBRUKER_USERNAME til $SYSTEMBRUKER_USERNAME"
fi

if test -f /var/run/secrets/nais.io/srvfpformidling/password;
then
   export  SYSTEMBRUKER_PASSWORD=$(cat /var/run/secrets/nais.io/srvfpformidling/password)
   echo "Setter SYSTEMBRUKER_PASSWORD"
fi
# Eksporterer vault verdiene
