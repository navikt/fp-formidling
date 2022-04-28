#!/usr/bin/env bash

echo "Kjører  $0"

if test -f /var/run/secrets/nais.io/systembruker/username;
then
   export  SYSTEMBRUKER_USERNAME=$(cat /var/run/secrets/nais.io/systembruker/username)
   echo "Setter SYSTEMBRUKER_USERNAME til $SYSTEMBRUKER_USERNAME"
fi

if test -f /var/run/secrets/nais.io/systembruker/password;
then
   export  SYSTEMBRUKER_PASSWORD=$(cat /var/run/secrets/nais.io/systembruker/password)
   echo "Setter SYSTEMBRUKER_PASSWORD"
fi
