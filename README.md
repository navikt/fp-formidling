Bestilling og produksjon av Brev og Dokumenter for foreldrepenger
================

Applikasjonen bestiller brev basert på hendelser fra en kafka-kø, eller via et REST-grensesnitt for forhåndsvisning.

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan rettes til:
* Ramesh Shiraddi (ramesh.shiraddi@nav.no)
* Aleksander Andresen (aleksander.andresen@nav.no)

### For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #fp-fomidling.



### Oppsett
#### Konfigfiler
For å kjøre lokalt trenger du 
app.properties & app-local.properties med riktige verdier i web\webapp

#### Sertifikater
For å kunne kjøre mot miljø fra lokalt, må man oppgi testsertfikater i 
test-sikkerhet/src/test/resources

#### Database
Du trenger postgres installert lokalt(helst 11+), og unit tester bruker en database som heter
fpformidling_unit, og en bruker med det samme (fpformidling_unit) som brukernavn og passord
For å kjøre trenger du bruker vl_dba med samme passord, men denne bruker default database med
eget schema
