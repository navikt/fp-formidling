Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM)
values (nextval('seq_kodeliste'), 'BG_HJEMMEL', 'F_14_7_8_28_8_30', null, 'folketrygdloven §§ 14-7, 8-28 og 8-30',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));
