------------------------ BEHANDLING_TYPE -----------------------------------------------
INSERT INTO KODELISTE (ID, KODEVERK, KODE, OFFISIELL_KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'),'BEHANDLING_TYPE','BT-008','ae0046','Anke');


------------------------ VURDERING -----------------------------------------------------
INSERT INTO KODEVERK (KODE, NAVN, BESKRIVELSE)
VALUES ('ANKEVURDERING', 'Ankevurdering', 'Resultat av ankevurderingen');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKEVURDERING', 'ANKE_STADFESTE_YTELSESVEDTAK', 'Ytelsesvedtaket stadfestes');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKEVURDERING', 'ANKE_OPPHEVE_OG_HJEMSENDE', 'Ytelsesvedtaket oppheve og hjemsende');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKEVURDERING', 'ANKE_OMGJOER', 'Anken omgjør');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKEVURDERING', 'ANKE_AVVIS', 'Anken avvises');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKEVURDERING', '-', 'udefinert');


------------------------ AVVIST -----------------------------------------------------
INSERT INTO KODEVERK (KODE, NAVN, BESKRIVELSE)
VALUES ('ANKE_AVVIST_AARSAK', 'Anke avvist årsak', 'Årsak til at anken er avvist');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIST_AARSAK', 'ANKE_FOR_SENT', 'Bruker har anket for sent');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIST_AARSAK' , 'ANKE_UGYLDIG', 'Anke er ugyldig');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIST_AARSAK', 'ANKE_IKKE_PAANKET_VEDTAK', 'Ikke påanket et vedtak');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIST_AARSAK', 'ANKE_IKKE_PART', 'Anke er ikke part');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIST_AARSAK', 'ANKE_IKKE_KONKRET', 'Anke er ikke konkret');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIST_AARSAK', 'ANKE_IKKE_SIGNERT', 'Anke er ikke signert');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIST_AARSAK', '-', 'udefinert');


------------------------ VURDERING OMGJOER----------------------------------------------------
INSERT INTO KODEVERK (KODE, NAVN, BESKRIVELSE)
VALUES ('ANKE_VURDERING_OMGJOER', 'Ankevurdering', 'Resultat av ankevurderingen');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_VURDERING_OMGJOER', 'ANKE_TIL_GUNST', 'Gunst omgjør i anke');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_VURDERING_OMGJOER', 'ANKE_DELVIS_OMGJOERING_TIL_GUNST', 'Delvis omgjøring, til gunst i anke');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_VURDERING_OMGJOER', 'ANKE_TIL_UGUNST', 'Ugunst omgjør i anke');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_VURDERING_OMGJOER', '-', 'udefinert');


------------------------ OMGJOER -----------------------------------------------------
INSERT INTO KODEVERK (KODE, NAVN, BESKRIVELSE)
VALUES ('ANKE_OMGJOER_AARSAK', 'Anke omgjør', 'Årsak til omgjør til anken');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_OMGJOER_AARSAK', 'NYE_OPPLYSNINGER', 'Nye opplysninger som oppfyller vilkår');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_OMGJOER_AARSAK', 'ULIK_REGELVERKSTOLKNING', 'Ulik regelverkstolkning');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_OMGJOER_AARSAK', 'ULIK_VURDERING', 'Ulik skjønnsmessig vurdering');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_OMGJOER_AARSAK', 'PROSESSUELL_FEIL', 'Prosessuell feil');

INSERT INTO KODELISTE (ID, KODEVERK, KODE, BESKRIVELSE)
VALUES (nextval('seq_kodeliste'), 'ANKE_OMGJOER_AARSAK', '-', 'udefinert');


------------------------ HISTORIKK_RESULTAT_TYPE -----------------------------------------------------
INSERT INTO KODELISTE (ID, KODE, KODEVERK, BESKRIVELSE) VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIS', 'HISTORIKK_RESULTAT_TYPE', 'Anken er avvist');
INSERT INTO KODELISTE (ID, KODE, KODEVERK, BESKRIVELSE) VALUES (nextval('seq_kodeliste'), 'ANKE_OMGJOER', 'HISTORIKK_RESULTAT_TYPE', 'Omgjør i anke');
INSERT INTO KODELISTE (ID, KODE, KODEVERK, BESKRIVELSE) VALUES (nextval('seq_kodeliste'), 'ANKE_OPPHEVE_OG_HJEMSENDE', 'HISTORIKK_RESULTAT_TYPE', 'Ytelsesvedtaket oppheve og hjemsende');
INSERT INTO KODELISTE (ID, KODE, KODEVERK, BESKRIVELSE) VALUES (nextval('seq_kodeliste'), 'ANKE_DELVIS_OMGJOERING_TIL_GUNST', 'HISTORIKK_RESULTAT_TYPE', 'Delvis omgjøring, til gunst i anke');
INSERT INTO KODELISTE (ID, KODE, KODEVERK, BESKRIVELSE) VALUES (nextval('seq_kodeliste'), 'ANKE_STADFESTET_VEDTAK', 'HISTORIKK_RESULTAT_TYPE', 'Vedtaket er stadfestet');
INSERT INTO KODELISTE (ID, KODE, KODEVERK, BESKRIVELSE) VALUES (nextval('seq_kodeliste'), 'ANKE_TIL_UGUNST', 'HISTORIKK_RESULTAT_TYPE', 'Ugunst omgjør i anke');
INSERT INTO KODELISTE (ID, KODE, KODEVERK, BESKRIVELSE) VALUES (nextval('seq_kodeliste'), 'ANKE_TIL_GUNST', 'HISTORIKK_RESULTAT_TYPE', 'til gunst omgjør i anke');


------------------------ BEHANDLING_RESULTAT_TYPE -----------------------------------------------------
insert into KODELISTE (id, kode, beskrivelse, kodeverk) values (nextval('seq_kodeliste'), 'ANKE_AVVIST', 'Anke er avvist', 'BEHANDLING_RESULTAT_TYPE');


insert into KODELISTE (id, kode, beskrivelse, kodeverk) values (nextval('seq_kodeliste'), 'ANKE_OMGJOER', 'Bruker har fått omgjøring i anke', 'BEHANDLING_RESULTAT_TYPE');


insert into KODELISTE (id, kode, beskrivelse, kodeverk) values (nextval('seq_kodeliste'), 'ANKE_OPPHEVE_OG_HJEMSENDE', 'Bruker har fått vedtaket opphevet og hjemsendt i anke', 'BEHANDLING_RESULTAT_TYPE');


insert into KODELISTE (id, kode, beskrivelse, kodeverk) values (nextval('seq_kodeliste'), 'ANKE_YTELSESVEDTAK_STADFESTET', 'Anken er stadfestet/opprettholdt', 'BEHANDLING_RESULTAT_TYPE');


insert into KODELISTE (id, kode, beskrivelse, kodeverk) values (nextval('seq_kodeliste'), 'ANKE_DELVIS_OMGJOERING_TIL_GUNST', 'Anke er delvis omgjøring, til gunst', 'BEHANDLING_RESULTAT_TYPE');


insert into KODELISTE (id, kode, beskrivelse, kodeverk) values (nextval('seq_kodeliste'), 'ANKE_TIL_UGUNST', 'Gunst omgjør i anke', 'BEHANDLING_RESULTAT_TYPE');


--Nye skjermlenke for anke
INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ANKE_VURDERING', 'Anke vurdering', 'SKJERMLENKE_TYPE');


INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ANKE_MERKNADER', 'Anke merknader', 'SKJERMLENKE_TYPE');


insert into KODELISTE (id, kodeverk, kode, gyldig_fom, ekstra_data)
values(nextval('seq_kodeliste'), 'HISTORIKKINNSLAG_TYPE', 'ANKEBEH_STARTET', to_date('2017-01-01', 'YYYY-MM-DD'), '{"mal": "TYPE1"}');


insert into KODELISTE (id, kodeverk, kode, gyldig_fom, ekstra_data)
values(nextval('seq_kodeliste'), 'HISTORIKKINNSLAG_TYPE', 'ANKE_BEH', to_date('2017-01-01', 'YYYY-MM-DD'), '{"mal": "TYPE5"}');


INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ER_ANKER_IKKE_PART', 'Angir om anker ikke er part i saken.', 'HISTORIKK_ENDRET_FELT_TYPE');


INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ER_ANKE_IKKE_KONKRET', 'Er anke ikke konkret.', 'HISTORIKK_ENDRET_FELT_TYPE');


INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ER_ANKEFRIST_IKKE_OVERHOLDT', 'Er ankefrist ikke overholdt', 'HISTORIKK_ENDRET_FELT_TYPE');


INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ER_ANKEN_IKKE_SIGNERT', 'er anken ikke signert.', 'HISTORIKK_ENDRET_FELT_TYPE');



INSERT INTO KODELISTE (id, kodeverk, kode, beskrivelse)
VALUES  (nextval('seq_kodeliste'), 'VENT_AARSAK', 'ANKE_VENTER_PAA_MERKNADER_FRA_BRUKER',
        'Venter på merknader fra bruker');


INSERT INTO KODELISTE (id, kodeverk, kode, beskrivelse)
VALUES  (nextval('seq_kodeliste'), 'VENT_AARSAK', 'ANKE_OVERSENDT_TIL_TRYGDERETTEN',
        'Venter på at saken blir behandlet hos Trygderetten');



-- Historikk endret feltype for anke
INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ANKE_RESULTAT', 'anke resultat', 'HISTORIKK_ENDRET_FELT_TYPE');


INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ANKE_OMGJØR_ÅRSAK', 'Årsak til omgjøring', 'HISTORIKK_ENDRET_FELT_TYPE');


INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'ANKE_AVVIST_ÅRSAK', 'Årsak til avvist anke', 'HISTORIKK_ENDRET_FELT_TYPE');


INSERT INTO KODELISTE (id, kode, beskrivelse, kodeverk)
VALUES (nextval('seq_kodeliste'), 'PA_ANKET_BEHANDLINGID', 'på anket behandlingsId.', 'HISTORIKK_ENDRET_FELT_TYPE');


-- Ny VedtakResultatType for KLAGE
insert into KODELISTE (id, kode, beskrivelse, kodeverk) values (nextval('seq_kodeliste'), 'VEDTAK_I_ANKEBEHANDLING', 'vedtak i ankebehandling', 'VEDTAK_RESULTAT_TYPE');



