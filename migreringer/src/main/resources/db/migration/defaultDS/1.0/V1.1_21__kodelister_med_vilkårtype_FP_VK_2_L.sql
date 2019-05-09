-- Kodeliste
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM)
values (nextval('seq_kodeliste'), 'VILKAR_TYPE', 'FP_VK_2_L', null, 'Løpende medlemskapsvilkår',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));


-- Kodeliste_relasjon
Insert into KODELISTE_RELASJON (ID, KODEVERK1, KODE1, KODEVERK2, KODE2, GYLDIG_FOM, GYLDIG_TOM, OPPRETTET_AV,
                                OPPRETTET_TID, ENDRET_AV, ENDRET_TID)
values ('1008450', 'VILKAR_TYPE', 'FP_VK_2_L', 'AVSLAGSARSAK', '1020', to_date('09.11.2018', 'DD.MM.YYYY'),
        to_date('31.12.9999', 'DD.MM.YYYY'), 'VL',
        to_timestamp('09.11.2018 08.38.06,138000000', 'DD.MM.YYYY HH24.MI.SS'), null, null);


Insert into KODELISTE_RELASJON (ID, KODEVERK1, KODE1, KODEVERK2, KODE2, GYLDIG_FOM, GYLDIG_TOM, OPPRETTET_AV,
                                OPPRETTET_TID, ENDRET_AV, ENDRET_TID)
values ('1008500', 'VILKAR_TYPE', 'FP_VK_2_L', 'AVSLAGSARSAK', '1021', to_date('09.11.2018', 'DD.MM.YYYY'),
        to_date('31.12.9999', 'DD.MM.YYYY'), 'VL',
        to_timestamp('09.11.2018 08.38.06,138000000', 'DD.MM.YYYY HH24.MI.SS'), null, null);


Insert into KODELISTE_RELASJON (ID, KODEVERK1, KODE1, KODEVERK2, KODE2, GYLDIG_FOM, GYLDIG_TOM, OPPRETTET_AV,
                                OPPRETTET_TID, ENDRET_AV, ENDRET_TID)
values ('1008550', 'VILKAR_TYPE', 'FP_VK_2_L', 'AVSLAGSARSAK', '1023', to_date('09.11.2018', 'DD.MM.YYYY'),
        to_date('31.12.9999', 'DD.MM.YYYY'), 'VL',
        to_timestamp('09.11.2018 08.38.06,138000000', 'DD.MM.YYYY HH24.MI.SS'), null, null);


Insert into KODELISTE_RELASJON (ID, KODEVERK1, KODE1, KODEVERK2, KODE2, GYLDIG_FOM, GYLDIG_TOM, OPPRETTET_AV,
                                OPPRETTET_TID, ENDRET_AV, ENDRET_TID)
values ('1008600', 'VILKAR_TYPE', 'FP_VK_2_L', 'AVSLAGSARSAK', '1024', to_date('09.11.2018', 'DD.MM.YYYY'),
        to_date('31.12.9999', 'DD.MM.YYYY'), 'VL',
        to_timestamp('09.11.2018 08.38.06,138000000', 'DD.MM.YYYY HH24.MI.SS'), null, null);


Insert into KODELISTE_RELASJON (ID, KODEVERK1, KODE1, KODEVERK2, KODE2, GYLDIG_FOM, GYLDIG_TOM, OPPRETTET_AV,
                                OPPRETTET_TID, ENDRET_AV, ENDRET_TID)
values ('1008650', 'VILKAR_TYPE', 'FP_VK_2_L', 'AVSLAGSARSAK', '1025', to_date('09.11.2018', 'DD.MM.YYYY'),
        to_date('31.12.9999', 'DD.MM.YYYY'), 'VL',
        to_timestamp('09.11.2018 08.38.06,138000000', 'DD.MM.YYYY HH24.MI.SS'), null, null);
