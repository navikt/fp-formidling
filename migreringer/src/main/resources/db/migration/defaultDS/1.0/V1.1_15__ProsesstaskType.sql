INSERT INTO PROSESS_TASK_FEILHAND (KODE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID)
VALUES ('DEFAULT', 'Eksponentiell back-off med tak', NULL, 'VL', to_timestamp('10.11.2017', 'DD.MM.YYYY'));

INSERT INTO PROSESS_TASK_TYPE (KODE, NAVN, FEIL_MAKS_FORSOEK, FEIL_SEK_MELLOM_FORSOEK, FEILHANDTERING_ALGORITME,
                               BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID)
VALUES ('formidling.bestillBrev', 'Bestiller brev', '3', '30', 'DEFAULT', 'Task som bestiller brev fra dokprod', 'VL',
        to_timestamp('08.03.2018', 'DD.MM.YYYY'));
INSERT INTO PROSESS_TASK_TYPE (KODE, NAVN, FEIL_MAKS_FORSOEK, FEIL_SEK_MELLOM_FORSOEK, FEILHANDTERING_ALGORITME,
                               BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID)
VALUES ('formidling.publiserHistorikk', 'Publiserer historikk', '3', '30', 'DEFAULT',
        'Task som legger historikk på kafkakø', 'VL', to_timestamp('08.03.2018', 'DD.MM.YYYY'));