Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM)
values (nextval('seq_kodeliste'), 'FAGSAK_YTELSE', 'SVP', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'));
