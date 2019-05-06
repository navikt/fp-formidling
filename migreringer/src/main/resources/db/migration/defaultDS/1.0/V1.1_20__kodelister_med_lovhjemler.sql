--Klage avvist
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'KLAGE_AVVIST_AARSAK', 'KLAGET_FOR_SENT', null,
        'Bruker har klaget etter at klagefristen er utløpt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["31", "33"]},"KA": {"lovreferanser": ["31", "34"]}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'KLAGE_AVVIST_AARSAK', 'KLAGE_UGYLDIG', null, 'Klage er ugyldig',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'KLAGE_AVVIST_AARSAK', '-', null, 'Ikke definert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'KLAGE_AVVIST_AARSAK', 'IKKE_PAKLAGD_VEDTAK', null,
        'Formkrav: ikke paklagd vedtak', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["28", "33"]},"KA": {"lovreferanser": ["28", "34"]}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'KLAGE_AVVIST_AARSAK', 'KLAGER_IKKE_PART', null, 'Formkrav: klager ikke part',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["28", "33"]},"KA": {"lovreferanser": ["28", "34"]}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'KLAGE_AVVIST_AARSAK', 'IKKE_KONKRET', null, 'Formkrav: klage ikke konkret',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["32", "33"]},"KA": {"lovreferanser": ["32", "34"]}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'KLAGE_AVVIST_AARSAK', 'IKKE_SIGNERT', null, 'Formkrav: ikke signert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"klageAvvistAarsak":{"NFP": {"lovreferanser": ["31", "33"]},"KA": {"lovreferanser": ["31", "34"]}}}');

--Avslagsårsaker
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1001', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_1", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1002', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1003', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1004', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_16_1", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1005', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1006', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1007', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1008', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1009', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1010', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1011', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"ES": [{"kategori": "FP_VK1", "lovreferanse": "§ 14-17 1. ledd"}, {"kategori": "FP_VK4", "lovreferanse": "§ 14-17 1. ledd"}, {"kategori": "FP_VK5", "lovreferanse": "§ 14-17 3. ledd"}]}, {"FP": [{"kategori": "FP_VK1", "lovreferanse": "§ 14-5 1. ledd"}, {"kategori": "FP_VK11", "lovreferanse": "§ 14-5 1. ledd"}, {"kategori": "FP_VK16", "lovreferanse": "§ 14-5 2. ledd"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1012', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1013', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_8", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1014', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_8", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1015', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_8", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1016', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1017', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1018', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1019', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_34", "lovreferanse": "21-3,21-7"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1020', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_2", "lovreferanse": "14-2"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1021', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_2", "lovreferanse": "14-2"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1023', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_2", "lovreferanse": "14-2"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1024', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_2", "lovreferanse": "14-2"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1025', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_2", "lovreferanse": "14-2"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1026', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_1", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1032', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"ES": [{"kategori": "FP_VK1", "lovreferanse": "§ 14-17 1. ledd"}, {"kategori": "FP_VK4", "lovreferanse": "§ 14-17 1. ledd"}, {"kategori": "FP_VK5", "lovreferanse": "§ 14-17 3. ledd"}]}, {"FP": [{"kategori": "FP_VK_8", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1033', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"ES": [{"kategori": "FP_VK4", "lovreferanse": "14-17"}, {"kategori": "FP_VK5", "lovreferanse": "14-17"}, {"kategori": "FP_VK33", "lovreferanse": "14-17"}]}, {"FP": {"lovreferanse": "14-5"}}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1034', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"ES": [{"kategori": "FP_VK4", "lovreferanse": "14-17"}, {"kategori": "FP_VK5", "lovreferanse": "14-17"}, {"kategori": "FP_VK33", "lovreferanse": "14-17"}]}, {"FP": [{"kategori": "FP_VK_8", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '-', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1031', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-5"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1041', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_41", "lovreferanse": "14-7"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1035', null, 'Ikke tilstrekkelig opptjening',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_23", "lovreferanse": "14-6"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1027', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_11", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1028', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_11", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1029', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_11", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1051', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": [{"FP": [{"kategori": "FP_VK_16", "lovreferanse": "14-5"}]}]}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'AVSLAGSARSAK', '1099', null, 'Ingen beregningsregler tilgjengelig i løsningen',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);

--Gradering Avslag
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'GRADERING_AVSLAG_AARSAK', '-', null, 'Ikke definert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'GRADERING_AVSLAG_AARSAK', '4523', null,
        'Avslag gradering - arbeid 100% eller mer', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('01.01.2000', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'GRADERING_AVSLAG_AARSAK', '4504', null,
        '§14-16 andre ledd: Avslag gradering - gradering før uke 7', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'GRADERING_AVSLAG_AARSAK', '4501', null,
        '§14-16: Ikke gradering pga. for sen søknad', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'GRADERING_AVSLAG_AARSAK', '4502', null,
        '§14-16 femte ledd, jf §21-3: Avslag graderingsavtale mangler - ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-16,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'GRADERING_AVSLAG_AARSAK', '4503', null,
        '§14-16 fjerde ledd: Avslag gradering – ikke rett til gradert uttak pga. redusert oppfylt aktivitetskrav på mor',
        to_date('01.01.2021', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-16"}}}');


--Ikke Oppfylt årsaker
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '-', null, 'Ikke definert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4005', null,
        '§14-10 sjuende ledd: Ikke sammenhengende perioder', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4002', null,
        '§14-9: Ikke stønadsdager igjen på stønadskonto', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-9"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4020', null, '§22-13 tredje ledd: Brudd på søknadsfrist',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "22-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4022', null, '§14-10 tredje ledd: Barnet er over 3 år',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4012', null,
        '§14-10 fjerde ledd: Far/medmor har ikke omsorg', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4003', null, '§14-10 fjerde ledd: Mor har ikke omsorg',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4013', null,
        '§14-10 første ledd: Mor søker fellesperiode før 12 uker før termin/fødsel',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4060', null,
        '§14-10 sjette ledd: Samtidig uttak - ikke gyldig kombinasjon', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4007', null,
        '§14-12 tredje ledd: Den andre part syk/skadet ikke oppfylt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4008', null,
        '§14-12 tredje ledd: Den andre part innleggelse ikke oppfylt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4033', null,
        '§14-11 første ledd bokstav a: Ikke lovbestemt ferie', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4032', null,
        '§14-11 første ledd bokstav a: Ferie - selvstendig næringsdrivende/frilanser',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4037', null,
        '§14-11 første ledd bokstav b: Ikke heltidsarbeid', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4038', null,
        '§14-11 første ledd bokstav c: Søkers sykdom/skade ikke oppfylt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4039', null,
        '§14-11 første ledd bokstav c: Søkers innleggelse ikke oppfylt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4040', null,
        '§14-11 første ledd bokstav d: Barnets innleggelse ikke oppfylt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4031', null,
        '§14-9: Ferie/arbeid innenfor de første 6 ukene', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-9"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4034', null,
        '§14-11, jf §14-9: Avslag utsettelse - ingen stønadsdager igjen', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,14-9"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4035', null,
        '§14-11 første ledd bokstav b, jf. §14-14: Bare far har rett, mor fyller ikke aktivitetskravet',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,14-14,14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4030', null, '§14-9: Avslag utsettelse før termin/fødsel',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-9"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4041', null,
        '§14-11 første ledd bokstav a: Avslag utsettelse ferie på bevegelig helligdag',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4050', null,
        '§14-13 første ledd bokstav a: Aktivitetskravet arbeid ikke oppfylt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4051', null,
        '§14-13 første ledd bokstav b: Aktivitetskravet offentlig godkjent utdanning ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4052', null,
        '§14-13 første ledd bokstav c: Aktivitetskravet offentlig godkjent utdanning i kombinasjon med arbeid ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4053', null,
        '§14-13 første ledd bokstav d: Aktivitetskravet mors sykdom/skade ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4054', null,
        '§14-13 første ledd bokstav e: Aktivitetskravet mors innleggelse ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4055', null,
        '§14-13 første ledd bokstav f: Aktivitetskravet mors deltakelse på introduksjonssprogram ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4056', null,
        '§14-13 første ledd bokstav g: Aktivitetskravet mors deltakelse på kvalifiseringsprogram ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4057', null,
        '§14-14 tredje ledd: Unntak for aktivitetskravet, mors mottak av uføretrygd ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-14"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4058', null,
        '§14-5 tredje ledd: Unntak for Aktivitetskravet, stebarnsadopsjon - ikke nok dager',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-5"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4059', null,
        '§14-13 sjette ledd, jf. §14-9 fjerde ledd: Unntak for Aktivitetskravet, flerbarnsfødsel - ikke nok dager',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13, 14-9"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4006', null,
        '§14-10 sjuende ledd: Ikke sammenhengende perioder', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('01.01.2001', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4023', null,
        '§14-10 femte ledd: Arbeider i uttaksperioden mer enn 0%', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4025', null,
        '§14-16 første ledd: Avslag gradering - arbeid 100% eller mer', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4018', null,
        '§14-10 andre ledd: Søkt uttak/utsettelse før omsorgsovertakelse', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('01.01.2001', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4080', null, '§14-16: Ikke gradering pga. for sen søknad',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('01.01.2000', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4084', null,
        '§14-10 sjette ledd: Annen part har overlappende uttak, det er ikke søkt/innvilget samtidig uttak',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4085', null,
        '§14-10 sjette ledd: Det er ikke samtykke mellom partene', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4086', null,
        '§14-10 sjette ledd og §14-11: Annen part har overlappende uttaksperioder som er innvilget utsettelse',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10,14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4073', null,
        '§14-12 første ledd: Ikke rett til kvote fordi mor ikke har rett til foreldrepenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4074', null,
        '§14-12 tredje ledd, jf §21-3: Avslag overføring kvote pga. sykdom/skade/innleggelse ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4066', null,
        '§14-13 første ledd bokstav a, jf §21-3: Aktivitetskrav - arbeid ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4067', null,
        '§14-13 første ledd bokstav b, jf §21-3: Aktivitetskrav – utdanning ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4068', null,
        '§14-13 første ledd bokstav c, jf §21-3: Aktivitetskrav – arbeid i komb utdanning ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4069', null,
        '§14-13 første ledd bokstav d og femte ledd, jf §21-3: Aktivitetskrav – sykdom/skade ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4070', null,
        '§14-13 første ledd bokstav e og femte ledd, jf §21-3: Aktivitetskrav – innleggelse ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4071', null, '§14-10: Bruker er død',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4072', null, '§14-9 sjuende ledd: Barnet er dødt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-9"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4081', null,
        '§14-11 første ledd bokstav a: Avslag utsettelse pga ferie tilbake i tid', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4082', null,
        '§14-11 første ledd bokstav b: Avslag utsettelse pga arbeid tilbake i tid', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4061', null,
        '§14-11 første ledd bokstav a, jf §21-3: Utsettelse ferie ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4062', null,
        '§14-11 første ledd bokstav b, jf §21-3: Utsettelse arbeid ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4063', null,
        '§14-11 første ledd bokstav c og tredje ledd, jf §21-3: Utsettelse søkers sykdom/skade ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4064', null,
        '§14-11 første ledd bokstav c og tredje ledd, jf §21-3: Utsettelse søkers innleggelse ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4065', null,
        '§14-11 første ledd bokstav d, jf §21-3: Utsettelse barnets innleggelse - ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4092', null,
        '§14-12: Avslag overføring - har ikke aleneomsorg for barnet', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4093', null,
        '§14-16: Avslag gradering - søker er ikke i arbeid', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4091', null,
        '§14-10 sjuende ledd: Ikke sammenhengende perioder', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('01.01.2001', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4090', null,
        '§14-10 sjuende ledd: Ikke sammenhengende perioder', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('01.01.2001', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4094', null,
        '§14-16 femte ledd, jf §21-3: Avslag graderingsavtale mangler - ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('01.01.2000', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-16,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4088', null,
        '§14-13 første ledd bokstav f, jf §21-3: Aktivitetskrav – introprogram ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4089', null,
        '§14-13 første ledd bokstav g, jf §21-3: Aktivitetskrav – KVP ikke dokumentert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-13,21-3"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4087', null, '§14-2: Opphør medlemskap',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-2"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4095', null,
        '§14-10 første ledd: Mor tar ikke alle 3 ukene før termin', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4096', null, '§14-5: Fødselsvilkåret er ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-5"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4097', null, '§14-5: Adopsjonsvilkåret er ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-5"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4098', null,
        '§14-5: Foreldreansvarsvilkåret er ikke oppfylt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-5"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4099', null, '§14-6: Opptjeningsvilkåret er ikke oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-6"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'IKKE_OPPFYLT_AARSAK', '4100', null,
        '§14-10 andre ledd: Uttak før omsorgsovertakelse', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');

--Innvilget årsaker
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2001', null, '§14-6: Uttak er oppfylt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('01.01.2001', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-6"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2022', null,
        '§14-12: Overføring oppfylt, annen part er innlagt i helseinstitusjon', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2021', null,
        '§14-12: Overføring oppfylt, annen part er helt avhengig av hjelp til å ta seg av barnet',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2023', null,
        '§14-15 første ledd: Overføring oppfylt, søker har aleneomsorg for barnet', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-15"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2010', null,
        '§14-11 første ledd bokstav a: Gyldig utsettelse pga. ferie', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2011', null,
        '§14-11 første ledd bokstav b: Gyldig utsettelse pga. 100% arbeid', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2012', null,
        '§14-11 første ledd bokstav c: Gyldig utsettelse pga. innleggelse', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2013', null,
        '§14-11 første ledd bokstav d: Gyldig utsettelse pga. barn innlagt', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2014', null,
        '§14-11 første ledd bokstav c: Gyldig utsettelse pga. sykdom', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2020', null,
        '§14-12 første ledd: Overføring oppfylt, annen part har ikke rett til foreldrepenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2015', null,
        '§14-11 første ledd bokstav a, jf. §14-14, jf. §14-13: Utsettelse pga. ferie, kun far har rett',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,14-14,14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2016', null,
        '§14-11 første ledd bokstav b, jf. §14-14, jf. §14-13: Utsettelse pga. 100% arbeid, kun far har rett',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,14-14,14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2017', null,
        '§14-11 første ledd bokstav c, jf. §14-14, jf. §14-13: Utsettelse pga. sykdom, skade, kun far har rett',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,14-14,14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2018', null,
        '§14-11 første ledd bokstav c, jf. §14-14, jf. §14-13: Utsettelse pga. egen innleggelse på helseinstiusjon, kun far har rett',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,14-14,14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2019', null,
        '§14-11 første ledd bokstav d, jf. §14-14, jf. §14-13: Utsettelse pga. barnets innleggelse på helseinstitusjon, kun far har rett',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-11,14-14,14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2002', null, '§14-9: Innvilget fellesperiode/foreldrepenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-9"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2003', null,
        '§14-12: Innvilget uttak av kvote/overført kvote', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2004', null,
        '§14-14, jf. §14-13 : Innvilget foreldrepenger, kun far har rett', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-14,14-13"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2005', null,
        '§14-15: Innvilget foreldrepenger ved aleneomsorg', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-15"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2006', null, '§14-10: Innvilget foreldrepenger før fødsel',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2007', null,
        '§14-10: Innvilget foreldrepenger, kun mor har rett', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2030', null,
        '§14-9, jf. §14-16: Gradering av fellesperiode/foreldrepenger', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-9,14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2031', null,
        '§14-12, jf. §14-16: Gradering av kvote/overført kvote', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-12,14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2032', null, '§14-15, jf. §14-16: Gradering ved aleneomsorg',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-15,14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2033', null,
        '§14-14, jf. §14-13, jf. §14-16: Gradering foreldrepenger, kun far har rett',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-14,14-13,14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2034', null,
        '§14-10, jf. §14-16: Gradering foreldrepenger, kun mor har rett', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10,14-16"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2036', null,
        '§14-14 tredje ledd: Innvilget foreldrepenger, kun far har rett og mor er ufør',
        to_date('01.01.2021', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-14"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2037', null,
        '§14-9, jf. §14-13: Innvilget fellesperiode til far', to_date('01.01.2021', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-9"}}}');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'INNVILGET_AARSAK', '2038', null,
        '§ 14-10 sjette ledd: Redusert uttaksgrad pga. den andre forelderens uttak',
        to_date('01.01.2021', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        '{"fagsakYtelseType": {"FP": {"lovreferanse": "14-10"}}}');

--Periode resultat årsaker
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'PERIODE_RESULTAT_AARSAK', '-', null, 'Ikke definert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);


-- Svp Ikke oppfylt


INSERT INTO KODEVERK (kode, kodeverk_synk_nye, kodeverk_synk_eksisterende, navn, beskrivelse)
VALUES ('SVP_PERIODE_IKKE_OPPFYLT_AARSAK', 'N', 'N', 'Svangerskapspenger perioder ikke oppfylt årsak',
        'Internt kodeverk for årsaker til at en periode ikke er oppfylt.');

Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_PERIODE_IKKE_OPPFYLT_AARSAK', '8304', null, 'Bruker er død',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_PERIODE_IKKE_OPPFYLT_AARSAK', '8305', null, 'Barn er dødt',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_PERIODE_IKKE_OPPFYLT_AARSAK', '8306', null, 'Bruker er ikke medlem',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_PERIODE_IKKE_OPPFYLT_AARSAK', '8308', null, 'Søkt for sent',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_PERIODE_IKKE_OPPFYLT_AARSAK', '8309', null, 'Perioden er ikke før fødsel',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_PERIODE_IKKE_OPPFYLT_AARSAK', '8310', null,
        'Perioden må slutte senest tre uker før termin', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_PERIODE_IKKE_OPPFYLT_AARSAK', '8311', null,
        'Perioden er samtidig som en ferie', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_PERIODE_IKKE_OPPFYLT_AARSAK', '-', null, 'Ikke definert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);


--Svp arbeidsforhold ikke oppfylt

INSERT INTO KODEVERK (kode, kodeverk_synk_nye, kodeverk_synk_eksisterende, navn, beskrivelse)
VALUES ('SVP_ARBEIDSFORHOLD_IKKE_OPPFYLT_AARSAK', 'N', 'N', 'Svangerskapspenger arbeidsforhold årsak',
        'Internt kodeverk for årsaker til at hele arbeidsforholdet ikke får svangerskapspenger.');

Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_ARBEIDSFORHOLD_IKKE_OPPFYLT_AARSAK', '8301', null,
        'Hele uttaket er etter 3 uker før termindato', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_ARBEIDSFORHOLD_IKKE_OPPFYLT_AARSAK', '8302', null, 'Uttak kun på helg',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_ARBEIDSFORHOLD_IKKE_OPPFYLT_AARSAK', '8303', null,
        'Arbeidsgiver kan tilrettelegge', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'SVP_ARBEIDSFORHOLD_IKKE_OPPFYLT_AARSAK', '-', null, 'Ikke definert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
