-- EXPORT FRA FPSAK
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('AKSJONSPUNKT_STATUS', 'VL', null, null, null, 'N', 'N', 'AksjonspunktStatus',
        'Kodetabell som definerer status på aksjonspunktet.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null,
        null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('AKSJONSPUNKT_TYPE', 'VL', null, null, null, 'N', 'N', 'AksjonspunktType',
        'Kodetabell som definerer type aksjonspunktet faller under (Autopunkt, Manuell, OVERSTYRING)', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BEHANDLING_AARSAK', 'VL', null, null, null, 'N', 'N', 'BehandlingÅrsakType', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BEHANDLING_RESULTAT_TYPE', 'VL', null, null, null, 'N', 'N', 'BehandlingResultatType',
        'Internt kodeverk for behandlingsresultat utfall.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BEHANDLING_STATUS', 'VL', null, null, null, 'N', 'N', 'BehandlingStatus',
        'Angir definerte statuser en behandling kan være i (faglig sett). Statusene er definert av Forretning og Fag',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BEHANDLING_STEG_STATUS', 'VL', null, null, null, 'N', 'N', 'BehandlingStegStatus',
        'Angir hvilke status et BehanlingSteg kan ha når det kjøres', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'),
        null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BRUKER_KJOENN', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Kj_c3_b8nnstyper', '1', 'Kjønnstyper',
        'N', 'N', 'NavBrukerKjønn', 'Internt kodeverk for kjønn.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null,
        null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('DOKUMENT_MAL_RESTRIKSJON', 'VL', null, null, null, 'N', 'N', 'DokumentMalRestriksjon',
        'Angir restriksjon av dokument mal', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FAGSAK_STATUS', 'VL', null, null, null, 'N', 'N', 'FagsakStatus', 'Internt kodeverk for statuser på fagsaker.',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FAGSAK_YTELSE', 'VL', null, null, null, 'N', 'N', 'FagsakYtelseType', 'Internt kodeverk for ytelsestype.',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FAR_SOEKER_TYPE', 'VL', null, null, null, 'N', 'N', 'FarSøkerType',
        'Internt kodeverk for grunner til at far søker.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FORELDRE_TYPE', 'VL', null, null, null, 'N', 'N', 'ForeldreType', 'Kodeverk for foreldreansvarstype', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKK_AKTOER', 'VL', null, null, null, 'N', 'N', 'HistorikkAktør',
        'Angir definerte typer av aktører som kan opprette historikkinnslag', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNSENDINGSVALG', 'VL', null, null, null, 'N', 'N', 'Innsendingsvalg',
        'Hvordan et vedlegg sendes inn, eks: LASTET_OPP, SEND_SENERE, osv', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('IVERKSETTING_STATUS', 'VL', null, null, null, 'N', 'N', 'IverksettingStatus',
        'Internt kodeverk for status på iverksetting.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('KONFIG_VERDI_TYPE', 'VL', null, null, null, 'N', 'N', 'KonfigVerdiType',
        'Angir type den konfigurerbare verdien er av slik at dette kan brukes til validering og fremstilling.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('KONFIG_VERDI_GRUPPE', 'VL', null, null, null, 'N', 'N', 'KonfigVerdiGruppe',
        'Angir en gruppe konfigurerbare verdier tilhører. Det åpner for å kunne ha lister og Maps av konfigurerbare verdier',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('LAGRET_VEDTAK_TYPE', 'VL', null, null, null, 'N', 'N', 'LagretVedtakType',
        'Type av lagret vedtak, eks. FODSEL og ADOPSJON', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('LANDKODER', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Landkoder', '1', 'Landkoder', 'J', 'J',
        'Landkoder', 'NAV kopi av ISO 3166-1-alpha-3 3-bokstav landkoder', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('MEDLEMSKAP_TYPE', 'VL', null, null, null, 'N', 'N', 'MedlemskapType',
        'Kodeverk for type medlemsskap i folketrygden', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('MEDLEMSKAP_DEKNING', 'VL', null, null, null, 'N', 'N', 'MedlemskapDekningType',
        'Kodeverk for medlemsskapsdekning i folketrygden', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('MEDLEMSKAP_KILDE', 'VL', null, null, null, 'N', 'N', 'MedlemskapKildeType',
        'Kodeverk for kilde til informasjon om medlemsskap i folketrygden', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('MEDLEMSKAP_MANUELL_VURD', 'VL', null, null, null, 'N', 'N', 'MedlemskapManuellVurderingType',
        'Kodeverk for manuell vurdering av medlemsskapsperioder i folketrygden', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('OMSORGSOVERTAKELSE_VILKAR', 'VL', null, null, null, 'N', 'N', 'OmsorgsovertakelseVilkårType',
        'Kodeverk for vilkår for omsorgsovertakelse', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('OPPGAVE_AARSAK', 'VL', null, null, null, 'N', 'N', 'OppgaveÅrsak',
        'Internt kodeverk for årsaker til at oppgave i GSAK ble opprettet.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('PERSONSTATUS_TYPE', 'VL', null, null, null, 'N', 'N', 'PersonstatusType', 'Kodeverk for personstatus', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('POSTSTED', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Postnummer', '7', 'Postnummer', 'J', 'J',
        'Poststed', 'Kodeverk for postnummer og poststed', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('REGION', 'VL', null, null, null, 'N', 'N', 'Region', 'Kodeverk for landregion', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('RELATERT_YTELSE_TYPE', 'VL', null, null, null, 'N', 'N', 'RelatertYtelseType',
        'Kodeverk for type på relaterte ytelser.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('RELATERT_YTELSE_RESULTAT', 'Arena', null, null, null, 'N', 'N', 'RelatertYtelseResultat',
        'Kodeverk for resultat på relaterte ytelser.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('RELATERT_YTELSE_SAKSTYPE', 'Arena', null, null, null, 'N', 'N', 'RelatertYtelseSakstype',
        'Kodeverk for sakstype på relaterte ytelser.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('RELATERT_YTELSE_TILSTAND', 'VL', null, null, null, 'N', 'N', 'RelatertYtelseTilstand',
        'Kodeverk for tilstand på relaterte ytelser.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('SATS_TYPE', 'VL', null, null, null, 'N', 'N', 'SatsType', 'Type av sats - eks. Engangsstoenad', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('SOEKNAD_ANNEN_PART', 'VL', null, null, null, 'N', 'N', 'SøknadAnnenPartType',
        'Type på den andre parten i søknaden, eks: mor, far, medmor, medfar', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('SPRAAK_KODE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Spr_c3_a5k', '1', 'Språk', 'N', 'N',
        'Språkkode', 'NAV kopi av ISO 639-1 2-bokstavsspråk kode i store bokstaver', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VEDTAK_RESULTAT_TYPE', 'VL', null, null, null, 'N', 'N', 'VedtakResultatType',
        'Internt kodeverk for vedtak resultat. (INNVILGET/AVSLAG)', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'),
        null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VILKAR_RESULTAT_TYPE', 'VL', null, null, null, 'N', 'N', 'VilkarResultatType',
        'Internt kodeverk for vilkårsresultattype.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VILKAR_UTFALL_TYPE', 'VL', null, null, null, 'N', 'N', 'VilkarUtfallType',
        'Internt kodeverk for vilkår utfall type.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VILKAR_UTFALL_MERKNAD', 'VL', null, null, null, 'N', 'N', 'VilkarUtfallMerknad',
        'Merknader fra regler ifm et utfall på vilkår.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('DISKRESJONSKODE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Diskresjonskoder', '1',
        'Diskresjonskoder', 'N', 'N', 'Diskresjonskoder', null, 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null,
        null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('OPPLYSNINGSKILDE', 'VL', null, null, null, 'N', 'N', 'OpplysningsKilde', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('RELASJONSROLLE_TYPE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Familierelasjoner', '1',
        'Familierelasjoner', 'N', 'N', 'Familierelasjoner', null, 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null,
        null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('SIVILSTAND_TYPE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Sivilstander', '1', 'Sivilstander',
        'N', 'N', 'Sivilstander', null, 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BEHANDLING_TYPE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Behandlingstyper', '5',
        'Behandlingstyper', 'N', 'N', 'Behandlingstyper', 'Internt kodeverk for behandlingstyper.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VILKAR_TYPE', 'VL', null, null, null, 'N', 'N', 'VilkarType', 'Internt koderverk for vilkårstype.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('RELATERT_YTELSE_STATUS', 'Arena', null, null, null, 'N', 'N', 'RelatertYtelseStatus',
        'Kodeverk for status på relaterte ytelser.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('RELATERT_YTELSE_TEMA', 'Arena', null, null, null, 'N', 'N', 'RelatertYtelseTema',
        'Kodeverk for tema på relaterte ytelser.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VERGE_TYPE', 'VL', null, null, null, 'N', 'N', 'Kodeverk over gyldige typer av verge', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('ADRESSE_TYPE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Adressetyper', '1', 'Adressetyper', 'N',
        'N', 'Adressetyper', 'NAV Adressetyper', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('ARKIV_FILTYPE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Arkivfiltyper', '3', 'Arkivfiltyper',
        'N', 'N', 'Arkivfiltyper', 'NAV Arkivfiltyper', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BEHANDLING_TEMA', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Behandlingstema', '9',
        'Behandlingstema', 'N', 'N', 'Behandlingstema', 'NAV Behandlingstema', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('KOMMUNER', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Kommuner', '3', 'Kommuner', 'J', 'J',
        'Kommuner', 'NAV Kommuner', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('KLAGE_VURDERT_AV', 'VL', null, null, null, 'N', 'N', 'Klage vurdert av',
        'Hvilken type enhet har vurdert klagen', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('KLAGEVURDERING', 'VL', null, null, null, 'N', 'N', 'Klagevurdering', 'Resultat av klagevurderingen', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('KLAGE_AVVIST_AARSAK', 'VL', null, null, null, 'N', 'N', 'Klage avvist årsak', 'Årsak til at klagen er avvist',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('KLAGE_MEDHOLD_AARSAK', 'VL', null, null, null, 'N', 'N', 'Klage medhold årsak', 'Årsak til medhold til klagen',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKKINNSLAG_TYPE', 'VL', null, null, null, 'N', 'N', 'Historikkinnslag type',
        'Hvilken type historikkinnslag', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('LANDKODE_ISO2', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/LandkoderISO2', '2', 'LandkoderISO2',
        'J', 'J', 'Landkode ISO2', 'Landkode 2 bokstav', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('GEOPOLITISK', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Geopolitisk', '1', 'Geopolitisk', 'J',
        'J', 'Geopolitisk', 'Geopolitiske områder', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('LANDGRUPPER', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Landgrupper', '2', 'Landgrupper', 'J',
        'J', 'Landgruppe', 'Landgruppe', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FYLKER', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Fylker', '5', 'Fylker', 'J', 'J', 'Fylke',
        'Fylke', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('AVSLAGSARSAK', 'VL', null, null, null, 'N', 'N', 'Avslagsårsak',
        'Kodetabell som definerer avslagsårsaken på bakgrunn av et vilkår.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('DOKUMENT_KATEGORI', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Dokumentkategorier', '1',
        'Dokumentkategorier', 'N', 'N', 'Dokumentkategorier', 'NAV Dokumentkategorier', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FAMILIE_HENDELSE_TYPE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over gyldige typer av familie hendelser (fødsel, adopsjon, omsorgovertakelse', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('MOTTAK_KANAL', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Mottakskanaler', '1', 'Mottakskanaler',
        'J', 'J', 'Mottakskanaler', 'NAV Mottakskanaler', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VARIANT_FORMAT', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Variantformater', '1',
        'Variantformater', 'J', 'J', 'Variantformater', 'NAV Variantformater', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('TEMA', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/Tema', '2', 'Tema', 'N', 'N', 'Tema',
        'NAV Tema', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('DOKUMENT_TYPE_ID', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kodeverk/DokumentTypeId-er', '4',
        'DokumentTypeId-er', 'J', 'J', 'DokumentTypeId-er',
        'Typen til et mottatt dokument. Dette er et subset av DokumentTyper; inngående dokumenter, for eksempel søknad, terminbekreftelse o.l',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('DOKUMENT_TYPE', 'Dokkat', null, '2', 'Dokumenttyper', 'J', 'J', 'DokumentType',
        'Unik identifikator av dokumenttype på tvers av fagsystemer. Finnes ikke i felles kodeverk, men kan hentes fra et av endepunktene her: https://fasit.adeo.no/search/DokumenttypeInfo_v3?type=resource',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FAGSYSTEM', 'VL', null, null, 'Fagsystemer', 'N', 'N', 'Fagsystemer',
        'Konstanter for NAVs fagsystemer. Det finnes intet offisielt kodeverk for fagsystem-navn, men det eksisterer likevel entydige fagsystemnavn',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BREV_MOTTAKER', 'VL', null, null, null, 'N', 'N', 'BrevMottaker',
        'Internt kodeverk for brev mottaker i forbindelse med verge.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'),
        null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('TYPE_FISKE', 'VL', null, null, null, 'N', 'N', 'Type fiske', 'Kodeverk for type fiske', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('MORS_AKTIVITET', 'VL', null, null, null, 'N', 'N', 'Mors aktivitet', 'Kodeverk for mors aktivitet', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('SOKNAD_TYPE_TILLEGG', 'VL', null, null, null, 'N', 'N', 'Søknadtype-tillegg',
        'Kodeverk for tillegg til soknadtype', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('AKTIVITET_STATUS', 'VL', null, null, null, 'N', 'N', 'Aktivitet status',
        'Hvilken type aktivitet status som gjelder for beregning', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null,
        null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('STOENADSKONTOTYPE', 'VL', null, null, null, 'N', 'N', 'Stønadskontotype',
        'Internt kodeverk for typene av stønadskontoer som finnes. Feks mødrekvote, fedrevote og fellesperiode.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNSYN_RESULTAT_TYPE', 'VL', null, null, null, 'J', 'J', 'Type for innsyn resultat', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VENT_AARSAK', 'VL', null, null, null, 'N', 'N', 'Venteårsak', 'Internt kodeverk for vente årsak', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('ARBEID_TYPE', 'VL', null, null, null, 'N', 'N', 'Arbeid type', 'Kodeverk for arbeid typer', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNTEKTS_KILDE', 'VL', null, null, null, 'N', 'N', 'Arbeid type', 'Kodeverk for inntekts kilder', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNTEKTSPOST_TYPE', 'VL', null, null, null, 'N', 'N', 'Arbeid type', 'Kodeverk for inntektspost type', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('PERMISJONSBESKRIVELSE_TYPE', 'VL', null, null, null, 'N', 'N', 'Arbeid type',
        'Kodeverk for arbeid permisjonsbeskrivelse type', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VURDER_AARSAK', 'VL', null, null, null, 'N', 'N', 'VurderÅrsak', 'Internt kodeverk for vurder årsak', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VILKAR_KATEGORI', 'VL', null, null, null, 'N', 'N', 'Vilkår kategori', 'Kodeverk for vilkårskategorier', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('UTTAK_PERIODE_TYPE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over periode søker kan spesifisere i søknaden.', null, 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'),
        null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('AARSAK_TYPE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over årsaker til avvik(Utsettelse, opphold eller overføring) i perioder.', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('OPPHOLD_AARSAK_TYPE', 'VL', null, null, null, 'J', 'J', 'Kodeverk over opphold i uttak.', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('OVERFOERING_AARSAK_TYPE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over årsaker til avvik(Utsettelse, opphold eller overføring) i perioder.', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('UTSETTELSE_AARSAK_TYPE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over årsaker til avvik(Utsettelse, opphold eller overføring) i perioder.', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('PENSJON_TRYGD_BESKRIVELSE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kode/PensjonEllerTrygdeBeskrivelse',
        '6', 'PensjonEllerTrygdeBeskrivelse', 'J', 'J', 'Pensjon Eller Trygde Beskrivelse',
        'Beskrivelse av pensjon eller trygde beskrivelse', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'J');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('YTELSE_FRA_OFFENTLIGE', 'Kodeverkforvaltning', 'http://nav.no/kodeverk/Kode/YtelseFraOffentligeBeskrivelse',
        '4', 'YtelseFraOffentligeBeskrivelse', 'J', 'J', 'Ytelser fra offentlige', 'Ytelse fra offentlige', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'J');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('POSTERINGSTYPE', 'VL', null, null, null, 'N', 'N', 'Posteringstype',
        'Internt kodeverk for typen av posteringer.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('AVKORTING_AARSAK_TYPE', 'VL', null, null, null, 'J', 'J', 'Kodeverk over opphold i uttak.', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FORRETNINGSHENDELSE_TYPE', 'VL', null, null, null, 'N', 'N', 'ForretningshendelseType',
        'Internt kodeverk som definerer forretningshendelser.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null,
        null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('DOKUMENT_GRUPPE', 'VL', null, null, null, 'N', 'N', 'Dokumentgruppe',
        'Internt kodeverk som grupperer dokument i dokumentmottak', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'),
        null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('OPPTJENING_AKTIVITET_TYPE', 'VL', null, null, null, 'J', 'J', 'Opptjening aktivitet type',
        'Typer av aktivitet (eks. Arbeid, Næring, Likestilt med Yrkesaktivitet, andre Ytelser) som er tatt med i vurdering av Opptjening, etter vilkår FP_VK_23',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('OPPTJENING_AKTIVITET_KLASSIFISERING', 'VL', null, null, null, 'J', 'J', 'Opptjening aktivitet status',
        'Klassifisering av aktivitet (godkjent, antatt, ikke godkjent, etc.)', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('PERIODE_RESULTAT_TYPE', 'VL', null, null, null, 'N', 'N', 'Internt kodeverk for perioderesultat',
        'Internt kodeverk for perioderesultat.', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('NATURAL_YTELSE_TYPE', 'VL', null, null, null, 'J', 'J', 'Natural ytelse typer',
        'Forskjellige former for natural ytelser fra inntektsmeldingen.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('REFERANSE_TYPE', 'VL', null, null, null, 'J', 'J', 'Type referanse, for å referer til aktør, organisasjon',
        'Type aktivitetReferanse (eks. Orgnummer, aktørid)', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNTEKT_PERIODE_TYPE', 'VL', null, null, null, 'N', 'N', 'Inntektsperiodetyper',
        'Perioder som inntekter kan beregnes på', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNTEKTS_FORMAAL', 'VL', null, null, null, 'N', 'N', 'Formaal', 'Kodeverk for formål til inntekt.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VIRKSOMHET_TYPE', 'VL', null, null, null, 'N', 'N', 'Virksomhet type', 'Kodeverk for virksomhetstyper', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('IKKE_OPPFYLT_AARSAK', 'VL', null, null, null, 'N', 'N', 'Årsak til ikke oppfylt stønadsperiode',
        'Årsak til ikke oppfylt stønadsperiode', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKK_ENDRET_FELT_TYPE', 'VL', null, null, null, 'J', 'J', 'Kodeverk for faktaendringtyper',
        'Kodeverk for faktaendringtyper', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('SKJERMLENKE_TYPE', 'VL', null, null, null, 'J', 'J', 'Kodeverk for skjermlenketyper',
        'Kodeverk for skjermlenketyper', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKKINNSLAG_FELT_TYPE', 'VL', null, null, null, 'J', 'J', 'Kodeverk for endrede felt i historikkinnslag',
        'Kodeverk for endrede felt i historikkinnslag', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKK_ENDRET_FELT_VERDI_TYPE', 'VL', null, null, null, 'J', 'J', 'Kodeverk for endret felt verdier',
        'Kodeverk for endret felt verdier i historikkinnslag', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null,
        null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKK_OPPLYSNING_TYPE', 'VL', null, null, null, 'J', 'J', 'Opplysningtype', 'Opplysningtype', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKK_RESULTAT_TYPE', 'VL', null, null, null, 'J', 'J', 'Historikkinnslag resultat type',
        'Historikkinnslag resultat type', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKK_BEGRUNNELSE_TYPE', 'VL', null, null, null, 'J', 'J', 'Historikkinnslag begrunnelse type',
        'Historikkinnslag begrunnelse type', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('PERIODE_AARSAK', 'VL', null, null, null, 'N', 'N', 'Periodeårsak',
        'Årsaken til at beregningsgrunnlagperioden har blitt splittet', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'),
        null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('UTTAK_DOKUMENTASJON_TYPE', 'VL', null, null, null, 'N', 'N', 'Dokumentasjonstype for søknadsperioder',
        'Dokumentasjonstype for søknadsperioder', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('UTTAK_DOKUMENTASJON_KLASSE', 'VL', null, null, null, 'N', 'N', 'Dokumentasjonsklasse for søknadsperioder',
        'Dokumentasjonsklasse for søknadsperioder - brukes av hibernate for å velge riktig implementasjon', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNTEKTS_FILTER', 'VL', null, null, null, 'N', 'N', 'A-inntektsfilter', 'Kodeverk for inntektsfilter', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('TEMA_UNDERKATEGORI', 'VL', null, null, null, 'J', 'J', 'Kodeverk for tema underkategori i ytelse',
        'Kodeverk for tema underkategori i ytelse', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('REVURDERING_VARSLING_AARSAK', 'VL', null, null, null, 'N', 'N', 'Årsak til revurdering',
        'Kodeverk for årsak til revurdering', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('GRADERING_AVSLAG_AARSAK', 'VL', null, null, null, 'N', 'N', 'Årsak til avslag om gradering av periode',
        'Årsak til avslag om gradering av periode', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('MANUELL_BEHANDLING_AARSAK', 'VL', null, null, null, 'N', 'N', 'Årsak til manuell behandling av periode',
        'Årsak til manuell behandling av periode', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('PERIODE_RESULTAT_AARSAK', 'VL', null, null, null, 'N', 'N', 'Innvilgelses/avslagÅrsak for periode',
        'Innvilgelses/avslagÅrsak for periode', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('HISTORIKK_AVKLART_SOEKNADSPERIODE_TYPE', 'VL', null, null, null, 'J', 'J', 'Avklart soeknadsperiode type',
        'Avklart soeknadsperiode type', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BEREGNINGSGRUNNLAG_TILSTAND', 'VL', null, null, null, 'J', 'J', 'Kodeverk for tilstand til beregningsgrunnlag',
        'Kodeverk for tilstand til beregningsgrunnlag', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNTEKTSKATEGORI', 'VL', null, null, null, 'J', 'J', 'Inntektskategori', 'Inntektskategori', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('UTTAK_ARBEID_TYPE', 'VL', null, null, null, 'N', 'N', 'Uttak arbeidtype',
        'Internt kodeverk for typene av arbeid', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('ARBEIDSKATEGORI', 'VL', null, null, null, 'J', 'J', 'Arbeidskategori', 'Arbeidskategori', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BEREGNINGSGRUNNLAG_ANDELTYPE', 'VL', null, null, null, 'J', 'J', 'Beregningsgrunnlag andeltype',
        'Internt kodeverk for andelstyper ved fastsetting av beregningsgrunnlag ved tilstøtende ytelser.', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FAGSYSTEM_UNDERKATEGORI', 'VL', null, null, null, 'J', 'J',
        'Kodeverk for mer spesifik kategorisering av kilde', 'Kodeverk for mer spesifik kategorisering av kilde', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('BG_HJEMMEL', 'VL', null, null, null, 'N', 'N', 'Hjemmel for beregningsgrunnlag',
        'Internt kodeverk for hjemmel for beregningsgrunnlag', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null,
        null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('REAKTIVERING_STATUS', 'VL', null, null, null, 'N', 'N', 'Aksjonspunktets reaktiveringsstatus',
        'Hvilken reaktiveringsstatus som aksjonspunktet har. Default aktiv ved opprettelse, men starter som inaktiv ved revurdering.',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNTEKTSMELDING_INNSENDINGSAARSAK', 'VL', null, null, null, 'N', 'N', 'Inntektsmelding innsendingsårsak',
        'Begrunnelse for innsending av inntektsmelding', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null,
        'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('UTTAK_UTSETTELSE_TYPE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over årsaker til avvik(Utsettelse, opphold eller overføring) i perioder.', null, 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('ARBEIDSFORHOLD_HANDLING_TYPE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over gyldige typer av handlinger saksbehandler kan utføre av overstyringer på arbeidsforhold', null,
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('RETTEN_TIL', 'VL', null, null, null, 'J', 'J', 'Brukers rett til foreldrepenger',
        'Brukers rett til foreldrepenger', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('KONSEKVENS_FOR_YTELSEN', 'VL', null, null, null, 'J', 'J', 'Konsekvens for ytelsen', 'Konsekvens for ytelsen',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('VEDTAKSBREV', 'VL', null, null, null, 'J', 'J', 'Vedtaksbrev', 'Vedtaksbrev', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('INNVILGET_AARSAK', 'VL', null, null, null, 'N', 'N', 'Årsak til oppfylt stønadsperiode',
        'Årsak til oppfylt stønadsperiode', 'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('UTTAK_PERIODE_VURDERING_TYPE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over mulige vurderinger av uttaksperioder',
        'Kodeverk over mulige vurderinger av uttaksperioder som saksbehandler kan gjøre i forbindelse med avklaring av fakta.',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FAKTA_OM_BEREGNING_TILFELLE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk for mer spesifik kategorisering av kilde', 'Kodeverk for mer spesifik kategorisering av kilde', 'VL',
        to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
Insert into KODEVERK (KODE, KODEVERK_EIER, KODEVERK_EIER_REF, KODEVERK_EIER_VER, KODEVERK_EIER_NAVN, KODEVERK_SYNK_NYE,
                      KODEVERK_SYNK_EKSISTERENDE, NAVN, BESKRIVELSE, OPPRETTET_AV, OPPRETTET_TID, ENDRET_AV, ENDRET_TID,
                      SAMMENSATT)
values ('FORDELING_PERIODE_KILDE', 'VL', null, null, null, 'J', 'J',
        'Kodeverk over mulige kilder til fordeling perioder', 'Kodeverk over mulige kilder til fordeling perioder.',
        'VL', to_timestamp('23.08.2018', 'DD.MM.RRRR'), null, null, 'N');
