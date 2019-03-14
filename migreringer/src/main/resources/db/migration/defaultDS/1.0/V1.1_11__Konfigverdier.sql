-- EXPORT FRA FPSAK
INSERT INTO KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
VALUES ('norg2.kontakt.telefonnummer', 'Norg2 kontakttelefonnummer', 'INGEN', 'STRING', 'Norg2 kontakttelefonnummer');
INSERT INTO KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
VALUES (nextval('SEQ_KONFIG_VERDI'), 'norg2.kontakt.telefonnummer', 'INGEN', '55553333',
        to_date('05.12.2017', 'dd.mm.yyyy'));

INSERT INTO KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
VALUES ('norg2.kontakt.klageinstans.telefonnummer', 'Norg2 Nav Klageinstans kontakttelefon', 'INGEN', 'STRING',
        'Norg2 Nav Klageinstans kontakttelefon');
INSERT INTO KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
VALUES (nextval('SEQ_KONFIG_VERDI'), 'norg2.kontakt.klageinstans.telefonnummer', 'INGEN', '21071730',
        to_date('05.12.2017', 'dd.mm.yyyy'));

insert into KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
values ('brev.avsender.enhet.navn', 'NAV avsender enhetsnavn', 'INGEN', 'STRING',
        'NAV avsender enhetsnavn');
insert into KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
values (nextval('SEQ_KONFIG_VERDI'), 'brev.avsender.enhet.navn', 'INGEN', 'NAV Familie- og pensjonsytelser',
        to_date('01.01.2016', 'dd.mm.yyyy'));

insert into KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
values ('brev.returadresse.adresselinje1', 'Adresselinje1 i returadresse', 'INGEN', 'STRING',
        'Adresselinje1 i returadresse');
insert into KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
values (nextval('SEQ_KONFIG_VERDI'), 'brev.returadresse.adresselinje1', 'INGEN', 'Postboks 6600 Etterstad',
        to_date('01.01.2016', 'dd.mm.yyyy'));

insert into KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
values ('brev.returadresse.postnummer', 'Postnummer i returadresse', 'INGEN', 'STRING', 'Postnummer i returadresse');
insert into KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
values (nextval('SEQ_KONFIG_VERDI'), 'brev.returadresse.postnummer', 'INGEN', '0607',
        to_date('01.01.2016', 'dd.mm.yyyy'));

insert into KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
values ('brev.returadresse.poststed', 'Poststed i returadresse', 'INGEN', 'STRING', 'Poststed i returadresse');
insert into KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
values (nextval('SEQ_KONFIG_VERDI'), 'brev.returadresse.poststed', 'INGEN', 'OSLO',
        to_date('01.01.2016', 'dd.mm.yyyy'));

insert into KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
values ('brev.avsender.klage.enhet.navn', 'NAV Klageinstans avsender enhetsnavn', 'INGEN', 'STRING',
        'NAV Klageinstans avsender enhetsnavn');
insert into KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
values (nextval('SEQ_KONFIG_VERDI'), 'brev.avsender.klage.enhet.navn', 'INGEN', 'NAV Klageinstans',
        to_date('01.01.2016', 'dd.mm.yyyy'));
