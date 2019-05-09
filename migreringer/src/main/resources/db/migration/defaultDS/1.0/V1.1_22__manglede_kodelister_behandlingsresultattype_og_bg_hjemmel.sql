Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'BEHANDLING_RESULTAT_TYPE', 'UGUNST_MEDHOLD_I_KLAGE', null,
        'Vedtaket er omgjort til ugunst', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'BEHANDLING_RESULTAT_TYPE', 'MANGLER_BEREGNINGSREGLER', null, null,
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'BEHANDLING_RESULTAT_TYPE', 'DELVIS_MEDHOLD_I_KLAGE', null,
        'Vedtaket er delvis omgjort', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'BEHANDLING_RESULTAT_TYPE', 'HJEMSENDE_UTEN_OPPHEVE', null,
        'Behandlingen er hjemsendt', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        null);


UPDATE KODELISTE
SET NAVN = 'Ikke definert'
where kode = '-';
UPDATE KODELISTE
SET NAVN ='folketrygdloven § 14-7'
where kode = 'F_14_7';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-30'
where kode = 'F_14_7_8_30';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-35'
where kode = 'F_14_7_8_35';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-38'
where kode = 'F_14_7_8_38';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-40'
where kode = 'F_14_7_8_40';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-41'
where kode = 'F_14_7_8_41';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-42'
where kode = 'F_14_7_8_42';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-43'
where kode = 'F_14_7_8_43';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-47'
where kode = 'F_14_7_8_47';
UPDATE KODELISTE
SET NAVN = 'folketrygdloven §§ 14-7 og 8-49'
where kode = 'F_14_7_8_49';

