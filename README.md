FP-FORMIDLING
================
[![Bygg og deploy](https://github.com/navikt/fp-formidling/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/navikt/fp-formidling/actions/workflows/build.yml)
[![Promote](https://github.com/navikt/fp-formidling/actions/workflows/promote.yml/badge.svg?branch=master)](https://github.com/navikt/fp-formidling/actions/workflows/promote.yml)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-formidling&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=navikt_fp-formidling)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-formidling&metric=coverage)](https://sonarcloud.io/summary/new_code?id=navikt_fp-formidling)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-formidling&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_fp-formidling)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-formidling&metric=bugs)](https://sonarcloud.io/dashboard?id=navikt_fp-formidling)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-formidling&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=navikt_fp-formidling)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-formidling&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=navikt_fp-formidling)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-formidling&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=navikt_fp-formidling)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=navikt_fp-formidling&metric=sqale_index)](https://sonarcloud.io/dashboard?id=navikt_fp-formidling)

Bestilling og produksjon av Brev og Dokumenter for foreldrepenger.
Applikasjonen bestiller brev basert på hendelser via et REST-grensesnitt.

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes til:

* Anja Gøtesen Aalerud (anja.gotesen.aalerud@nav.no)
* Michal J. Sladek (michal.sladek@nav.no)

### Henvendelser for NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #fp-brev.

## Oppsett

### Run Config

For å kjøre lokalt må du å ha VTP kjørende på forhånd.
Konfigurasjonen i app-vtp.properties skal du ikke trenge å endre.
Sett $MODULE_DIR$ som working directory, og bruk classpath til webapp.
Kjører på Java 17. Kan korte ned classpathen med jar manifest.

### Database

Bruk Docker Compose i fpsak-autotest for lokal utvikling - da får du PostgreSQL-container automatisk (se lengre ned).

For tilgang til vedlikehold og feilsøking ute i miljø, kreves det midlertidig brukernavn og passord fra Vault.
Se https://github.com/navikt/utvikling/blob/main/docs/teknisk/Vault.md for å komme i gang.
De ulike miljøvariablene som trengs er inneholdt i prosjektmappen til "app.yaml".

Eksempel (Windows): Koble til fpformidling-databasen i prod med readonly-tilgang

1. I IntelliJ, åpne Database-fanen og trykk på pluss-ikonet for å legge til en ny PostgreSQL-"data source"
2. Fyll inn Name (valgfritt), Host (a01dbfl039.adeo.no), Port (5432) og Database (fpformidling)
3. Åpne Powershell og kjør "vault login -method=oidc" (forutsetter at CLI-klienten og miljøvariabelen VAULT_ADDR er satt opp)
4. Kjør deretter "vault read postgresql/prod-fss/creds/fpformidling-readonly"
5. Tilbake i IntelliJ, fyll inn User og Password med verdiene generert av Vault og trykk OK.

Alternativt kan man koble seg til via pgAdmin som følger med Postgres, eller DBeaver som er et GUI-verktøy ala Oracle SQL Developer.

#### Lokale databaser

Unit tester bruker en database som heter fpformidling_unit, og en bruker med det samme (fpformidling_unit) som brukernavn og passord.
For å kjøre trenger du bruker vl_dba med vl_dba som passord, men denne bruker default database med eget schema.

### Dokumentasjon for interne

https://confluence.adeo.no/display/TVF/FP-Formidling

### Kjør opp avhengigheter for lokal utvikling:

Dette gjøres nå i _fpsak-autotest_-prosjektet. Her finnes det en felles docker-compose som skal brukes for lokalt utvikling.
Vennligst se dokumentasjonen her: [Link til lokal utvikling i fpsak-autotest](https://github.com/navikt/fpsak-autotest/tree/master/docs).

### Remote debugging med Java base image

I Java base imagene så er remote debugging skrudd på for port 5005 i development clustrene "dev-fss" og "dev-gcp".
For å kunne remote debugge en kjørende applikasjon så må man først port-forwarde til podden hvor applikasjonen kjører.
Dette innebærer at du har naisdevice kjørende på maskinen.

```shell script
kubectl -nteamforeldrepenger port-forward service/fpformidling 5005:5005
```

Hvis ikke allerede gjort, opprett en ny configuration i IntelliJ av type "Remote".
Start debuggingen slik som man vanligvis ville gjort lokalt.

### Sikkerhet

Det er mulig å kalle tjenesten med bruk av følgende tokens

- Azure CC
- Azure OBO med følgende rettigheter:
    - fpsak-saksbehandler
    - fpsak-veileder
    - fpsak-drift
- STS (fases ut)
