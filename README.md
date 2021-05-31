Bestilling og produksjon av Brev og Dokumenter for foreldrepenger
================

Applikasjonen bestiller brev basert på hendelser fra en kafka-kø, eller via et REST-grensesnitt for forhåndsvisning.

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes til:
* Anja Gøtesen Aalerud (anja.gotesen.aalerud@nav.no)
* Jan Erik Johnsen (jan.erik.johnsen@nav.no)

### Henvendelser for NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #fp-brev.


## Oppsett
### Run Config
For å kjøre lokalt må du å ha VTP kjørende på forhånd.
Konfigurasjonen i app-vtp.properties skal du ikke trenge å endre.
Sett $MODULE_DIR$ som working directory, og bruk classpath til webapp.
Kjører på Java 16. Kan korte ned classpathen med jar manifest.

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


