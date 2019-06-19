
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AKTIVITET_STATUS', 'KUN_YTELSE', null,
        null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);


Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AKTIVITET_STATUS', 'VENTELØNN_VARTPENGER', null,
        null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
