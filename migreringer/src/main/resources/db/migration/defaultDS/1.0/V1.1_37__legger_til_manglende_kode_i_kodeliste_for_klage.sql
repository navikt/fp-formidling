Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM)
values (nextval('seq_kodeliste'), 'KLAGEVURDERING', 'HJEMSENDE_UTEN_Å_OPPHEVE', null, 'Hjemsende uten å oppheve',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));

Insert into KODELISTE (ID, KODEVERK, KODE, NAVN, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM)
values (nextval('seq_kodeliste'), 'KLAGEVURDERING', '-', 'Ikke definert', null, 'Ikke definert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));
