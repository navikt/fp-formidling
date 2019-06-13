insert into kodeliste (ID, kode, kodeverk, beskrivelse, gyldig_fom, ekstra_data)
values (nextval('seq_kodeliste'), '1060', 'AVSLAGSARSAK', 'Søker er ikke gravid kvinne',
        to_date('2000-01-01', 'YYYY-MM-DD'),
        '{"fagsakYtelseType": [{"SVP": [{"kategori": "SVP_VK_1", "lovreferanse": "14-4 1. ledd"}]}]}');
insert into kodeliste (ID, kode, kodeverk, beskrivelse, gyldig_fom, ekstra_data)
values (nextval('seq_kodeliste'), '1061', 'AVSLAGSARSAK',
        'Søker er ikke i arbeid/har ikke tap av pensjonsgivende inntekt', to_date('2000-01-01', 'YYYY-MM-DD'),
        '{"fagsakYtelseType": [{"SVP": [{"kategori": "SVP_VK_1", "lovreferanse": "14-4 3. ledd"}]}]}');
insert into kodeliste (ID, kode, kodeverk, beskrivelse, gyldig_fom, ekstra_data)
values (nextval('seq_kodeliste'), '1062', 'AVSLAGSARSAK', 'Søker skulle ikke søkt svangerskapspenger',
        to_date('2000-01-01', 'YYYY-MM-DD'),
        '{"fagsakYtelseType": [{"SVP": [{"kategori": "SVP_VK_1", "lovreferanse": "14-4 1. ledd"}]}]}');
insert into kodeliste (ID, kode, kodeverk, beskrivelse, gyldig_fom, ekstra_data)
values (nextval('seq_kodeliste'), '1063', 'AVSLAGSARSAK', 'Arbeidstaker har ikke dokumentert risikofaktorer',
        to_date('2000-01-01', 'YYYY-MM-DD'),
        '{"fagsakYtelseType": [{"SVP": [{"kategori": "SVP_VK_1", "lovreferanse": "14-4 1. ledd"}]}]}');
insert into kodeliste (ID, kode, kodeverk, beskrivelse, gyldig_fom, ekstra_data)
values (nextval('seq_kodeliste'), '1064', 'AVSLAGSARSAK', 'Arbeidstaker kan omplasseres til annet høvelig arbeid',
        to_date('2000-01-01', 'YYYY-MM-DD'),
        '{"fagsakYtelseType": [{"SVP": [{"kategori": "SVP_VK_1", "lovreferanse": "14-4 1. ledd"}]}]}');
insert into kodeliste (ID, kode, kodeverk, beskrivelse, gyldig_fom, ekstra_data)
values (nextval('seq_kodeliste'), '1065', 'AVSLAGSARSAK',
        'Næringsdrivende/frilanser har ikke dokumentert risikofaktorer', to_date('2000-01-01', 'YYYY-MM-DD'),
        '{"fagsakYtelseType": [{"SVP": [{"kategori": "SVP_VK_1", "lovreferanse": "14-4 2. ledd"}]}]}');
insert into kodeliste (ID, kode, kodeverk, beskrivelse, gyldig_fom, ekstra_data)
values (nextval('seq_kodeliste'), '1066', 'AVSLAGSARSAK',
        'Næringsdrivende/frilanser har mulighet til å tilrettelegge sitt virke', to_date('2000-01-01', 'YYYY-MM-DD'),
        '{"fagsakYtelseType": [{"SVP": [{"kategori": "SVP_VK_1", "lovreferanse": "14-4 2. ledd"}]}]}');
