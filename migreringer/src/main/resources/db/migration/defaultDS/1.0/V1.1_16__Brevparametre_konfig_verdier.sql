insert into KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
values ('vedtak.klagefrist.uker', 'Vedtak klagefrist', 'INGEN', 'INTEGER',
        ' Klagefrist i uker (positivt heltall), sendes i vedtaksbrev til brukeren');
insert into KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
values (nextval('SEQ_KONFIG_VERDI'), 'vedtak.klagefrist.uker', 'INGEN', '6', to_date('01.01.2016', 'dd.mm.yyyy'));

insert into KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
values ('brev.svarfrist.dager', 'Brukers svarfrist', 'INGEN', 'PERIOD', 'Brukers svartfrist (periode)');
insert into KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
values (nextval('SEQ_KONFIG_VERDI'), 'brev.svarfrist.dager', 'INGEN', 'P3W', to_date('01.01.2016', 'dd.mm.yyyy'));

INSERT INTO KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
VALUES ('innsyn.klagefrist.uker', 'Innsyn klagefrist', 'INGEN', 'INTEGER',
        ' Klagefrist i uker (positivt heltall), sendes i svar på innsynskrav til brukeren');
INSERT INTO KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
VALUES (nextval('SEQ_KONFIG_VERDI'), 'innsyn.klagefrist.uker', 'INGEN', '3', to_date('01.01.2016', 'dd.mm.yyyy'));

insert into KONFIG_VERDI_KODE (kode, navn, konfig_gruppe, konfig_type, beskrivelse)
values ('søk.antall.uker', 'Søk antall uker', 'INGEN', 'PERIOD', 'Antall uker før uttak en bruker må søke.');
insert into KONFIG_VERDI (id, konfig_kode, konfig_gruppe, konfig_verdi, gyldig_fom)
values (nextval('SEQ_KONFIG_VERDI'), 'søk.antall.uker', 'INGEN', 'P4W', to_date('01.01.2016', 'dd.mm.yyyy'));
