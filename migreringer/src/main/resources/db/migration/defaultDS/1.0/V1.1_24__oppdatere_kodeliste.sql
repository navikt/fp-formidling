Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'ARBEID', null,
        'Registrert arbeidsforhold i AA-registeret', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'FRILANS', null, 'Frilanser',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'NÆRING', null,
        'Registrert i Enhetsregisteret som selvstendig næringsdrivende', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'DAGPENGER', null, 'Mottar ytelse for Dagpenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'AAP', null, 'Mottar ytelse for Arbeidsavklaringspenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'FORELDREPENGER', null,
        'Mottar ytelse for Foreldrepenger', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'SYKEPENGER', null, 'Mottar ytelse for Sykepenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'SVANGERSKAPSPENGER', null,
        'Mottar ytelse for Svangerskapspenger', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'OPPLÆRINGSPENGER', null,
        'Mottar ytelse for Opplæringspenger', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'),
        null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'OMSORGSPENGER', null, 'Mottar ytelse for Omsorgspenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'PLEIEPENGER', null, 'Mottar ytelse for Pleiepenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'VIDERE_ETTERUTDANNING', null,
        'Lønn fra arbeidsgiver ifbm. videre- og etterutdanning. Pensjonsgivende inntekt som likestilles med yrkesaktivitet',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'MILITÆR_ELLER_SIVILTJENESTE', null,
        'vtjening av militær- eller siviltjeneste eller obligatorisk sivilforsvarstjeneste. Pensjonsgivende inntekt som likestilles med yrkesaktivitet',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', '-', null, 'Udefinert',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'UTDANNINGSPERMISJON', 'UTDANNINGSPERMISJON',
        'Kode for utdanningspermisjon', to_date('25.03.2019', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'UTENLANDSK_ARBEIDSFORHOLD', null, null,
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'VENTELØNN_VARTPENGER', null,
        'Ventelønn eller vartpenger', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'OPPTJENING_AKTIVITET_TYPE', 'ETTERLØNN_SLUTTPAKKE', null,
        'Etterlønn eller sluttpakke', to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);

Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'VANLIG', 'VANLIG', null, to_date('07.12.2017', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', '-', null, 'Ikke definert', to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'ORDINÆRT_ARBEIDSFORHOLD', 'ordinaertArbeidsforhold', null,
        to_date('01.01.2014', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'FORENKLET_OPPGJØRSORDNING', 'forenkletOppgjoersordning', null,
        to_date('01.01.2014', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'FRILANSER_OPPDRAGSTAKER', 'frilanserOppdragstakerHonorarPersonerMm',
        null, to_date('01.01.2014', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'MARITIMT_ARBEIDSFORHOLD', 'maritimtArbeidsforhold', null,
        to_date('01.01.2014', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'PENSJON_OG_ANDRE_TYPER_YTELSER_UTEN_ANSETTELSESFORHOLD',
        'pensjonOgAndreTyperYtelserUtenAnsettelsesforhold', null, to_date('01.01.2014', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'NÆRING', null, null, to_date('01.01.2014', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), null);
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'LØNN_UNDER_UTDANNING', null, null,
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), '{ "gui": "true" }');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'MILITÆR_ELLER_SIVILTJENESTE', null, null,
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), '{ "gui": "true" }');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'UTENLANDSK_ARBEIDSFORHOLD', null, null,
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), '{ "gui": "true" }');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'FRILANSER', null, null, to_date('01.01.2000', 'DD.MM.RRRR'),
        to_date('31.12.9999', 'DD.MM.RRRR'), '{ "gui": "true" }');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'VENTELØNN_VARTPENGER', null, 'Ventelønn eller vartpenger',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), '{ "gui": "true" }');
Insert into KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE, GYLDIG_FOM, GYLDIG_TOM, EKSTRA_DATA)
values (nextval('seq_kodeliste'), 'ARBEID_TYPE', 'ETTERLØNN_SLUTTPAKKE', null, 'Etterlønn eller sluttpakke',
        to_date('01.01.2000', 'DD.MM.RRRR'), to_date('31.12.9999', 'DD.MM.RRRR'), '{ "gui": "true" }');
