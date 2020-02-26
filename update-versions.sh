#!/usr/bin/env bash

echo ABAKUS_IMAGE=docker.pkg.github.com/navikt/fp-abakus/fpabakus > .env
echo VTP_IMAGE=docker.pkg.github.com/navikt/vtp/vtp >> .env
echo ORACLE_IMAGE=docker.pkg.github.com/navikt/vtp/foreldrepenger-oracle >> .env
echo POSTGRES_IMAGE=postgres:12 >> .env
echo FPSAK_IMAGE=docker.pkg.github.com/navikt/fp-sak/fp-sak >> .env

echo ".env fil opprettet - Klart for docker-compose up"
