INSERT INTO KODEVERK (KODE, KODEVERK_EIER, NAVN, BESKRIVELSE, OPPRETTET_AV, SAMMENSATT)
VALUES ('EVENTMOTTAK_STATUS', 'VL', 'EVENTMOTTAK_STATUS', 'Internt kodeverk for status på eventmottak.', 'VL',
        'N');

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES (nextval('seq_kodeliste'), 'EVENTMOTTAK_STATUS', 'FEILET', to_date('2000-01-01', 'YYYY-MM-DD'));

INSERT INTO KODELISTE (id, kodeverk, kode, gyldig_fom)
VALUES (nextval('seq_kodeliste'), 'EVENTMOTTAK_STATUS', 'FERDIG', to_date('2000-01-01', 'YYYY-MM-DD'));
