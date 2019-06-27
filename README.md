Bestilling og produksjon av Brev og Dokumenter for foreldrepenger
================

Applikasjonen bestiller brev basert på hendelser fra en kafka-kø, eller via et REST-grensesnitt for forhåndsvisning.

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes til:
* Ramesh Shiraddi (ramesh.shiraddi@nav.no)
* Aleksander Andresen (aleksander.andresen@nav.no)
* Tor Nærland (tor.nerland@nav.no)

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen #fp-fomidling.

### Oppsett
#### Konfigfiler
For å kjøre lokalt trenger du app-local.properties med riktige verdier i web\webapp

#### Sertifikater
For å kunne kjøre mot miljø fra lokalt, må man oppgi testsertfikater i HOME mappen.
 I windows blir det slik "c:\Users\<bruker navn>\.modig"

#### Database
Du trenger postgres installert lokalt(helst 11+), og unit tester bruker en database som heter
fpformidling_unit, og en bruker med det samme (fpformidling_unit) som brukernavn og passord
For å kjøre trenger du bruker vl_dba med vl_dba som passord, men denne bruker default database med
eget schema

 #### Run Config
 Set $MODULE_DIR$ som working directory, og bruk classpath til webapp. 
 Kjører på java 11. Kan korte ned classpathen med jar manifest.
 
 #### Dokumentasjon for interne
 https://confluence.adeo.no/display/TVF/FP-Formidling
