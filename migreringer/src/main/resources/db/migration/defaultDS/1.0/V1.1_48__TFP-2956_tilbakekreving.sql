insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, EKSTRA_DATA, GYLDIG_FOM, GYLDIG_TOM)
values (nextval('seq_kodeliste'), 'BEHANDLING_TYPE', 'BT-007', null, null, '{ "behandlingstidFristUker" : 0, "behandlingstidVarselbrev" : "N" }', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));
insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, EKSTRA_DATA, GYLDIG_FOM, GYLDIG_TOM)
values (nextval('seq_kodeliste'), 'BEHANDLING_TYPE', 'BT-009', null, null, '{ "behandlingstidFristUker" : 0, "behandlingstidVarselbrev" : "N" }', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'));
