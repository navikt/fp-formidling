create sequence seq_dokument_hendelse
    minvalue 1000000
    increment by 50
    maxvalue 999999999999999999;

create sequence seq_prosess_task
    minvalue 1000000
    increment by 50
    maxvalue 999999999999999999;

create sequence seq_prosess_task_gruppe
    minvalue 10000000
    increment by 50
    maxvalue 999999999999999999;

create table dokument_hendelse
(
    id                         bigint                                      not null
        constraint pk_hendelse
            primary key,
    behandling_uuid            uuid                                        not null,
    ytelse_type                varchar(100)                                not null,
    dokument_mal_navn          varchar(7),
    gjelder_vedtak             boolean      default false,
    fritekst                   text,
    tittel                     varchar(200),
    revurdering_varsling_arsak varchar(100),
    opprettet_av               varchar(20)  default 'VL'::character varying,
    opprettet_tid              timestamp(3) default LOCALTIMESTAMP,
    endret_av                  varchar(20),
    endret_tid                 timestamp(3),
    bestilling_uuid            uuid                                        not null,
    behandlende_enhet_navn     varchar,
    vedtaksbrev                varchar(100) default '-'::character varying not null
);

comment on table dokument_hendelse is 'Hendelser om dokumenter bestilt eller forhåndvist fra foreldrepengeløsningen';

comment on column dokument_hendelse.id is 'Primary Key';

comment on column dokument_hendelse.behandling_uuid is 'UUID til tilhørende behandling i foreldrepengeløsningen';

comment on column dokument_hendelse.ytelse_type is 'Ytelse type til fagsaken i foreldrepengeløsningen - kodeverk';

comment on column dokument_hendelse.dokument_mal_navn is 'Navn på ønsket dokument';

comment on column dokument_hendelse.gjelder_vedtak is 'Om det er vedtaksdokument';

comment on column dokument_hendelse.fritekst is 'Fritekst som skal vises i dokumentet';

comment on column dokument_hendelse.tittel is 'Tittel for fritekstbrev';

comment on column dokument_hendelse.revurdering_varsling_arsak is 'FK:REVURDERING_VARSLING_ARSAK Fremmednøkkel til';

comment on column dokument_hendelse.bestilling_uuid is 'Unik ID for bestillingen fra FPSAK';

comment on column dokument_hendelse.behandlende_enhet_navn is 'Navn på behandlende enhet som har bestilt dokumentet';

comment on column dokument_hendelse.vedtaksbrev is 'FK: Kodeverk VEDTAKSBREV i KODELISTE-tabellen';

create index idx_dokument_hendelse_1
    on dokument_hendelse (behandling_uuid);

create index idx_dokument_hendelse_2
    on dokument_hendelse (bestilling_uuid);


create table prosess_task
(
    id                        numeric                                                                            not null,
    task_type                 varchar(50)                                                                        not null,
    prioritet                 numeric(3)   default 0                                                             not null,
    status                    varchar(20)  default 'KLAR'::character varying                                     not null,
    task_parametere           varchar(4000),
    task_payload              text,
    task_gruppe               varchar(250),
    task_sekvens              varchar(100) default '1'::character varying                                        not null,
    partition_key             varchar(4)   default to_char((CURRENT_DATE)::timestamp with time zone, 'MM'::text) not null,
    neste_kjoering_etter      timestamp(0) default CURRENT_TIMESTAMP,
    feilede_forsoek           numeric(5)   default 0,
    siste_kjoering_ts         timestamp(6),
    siste_kjoering_feil_kode  varchar(50),
    siste_kjoering_feil_tekst text,
    siste_kjoering_server     varchar(50),
    opprettet_av              varchar(20)  default 'VL'::character varying                                       not null,
    opprettet_tid             timestamp(6) default CURRENT_TIMESTAMP                                             not null,
    blokkert_av               numeric,
    versjon                   numeric      default 0                                                             not null,
    siste_kjoering_slutt_ts   timestamp(6),
    siste_kjoering_plukk_ts   timestamp(6),
    constraint pk_prosess_task
        primary key (id, status, partition_key)
)
    partition by LIST (status);

comment on table prosess_task is 'Inneholder tasks som skal kjøres i bakgrunnen';

comment on column prosess_task.id is 'Primary Key';

comment on column prosess_task.task_type is 'navn på task. Brukes til å matche riktig implementasjon';

comment on column prosess_task.prioritet is 'prioritet på task.  Høyere tall har høyere prioritet';

comment on column prosess_task.status is 'status på task: KLAR, NYTT_FORSOEK, FEILET, VENTER_SVAR, FERDIG';

comment on column prosess_task.task_parametere is 'parametere angitt for en task';

comment on column prosess_task.task_payload is 'inputdata for en task';

comment on column prosess_task.task_gruppe is 'angir en unik id som grupperer flere ';

comment on column prosess_task.task_sekvens is 'angir rekkefølge på task innenfor en gruppe ';

comment on column prosess_task.neste_kjoering_etter is 'tasken skal ikke kjøeres før tidspunkt er passert';

comment on column prosess_task.feilede_forsoek is 'antall feilede forsøk';

comment on column prosess_task.siste_kjoering_ts is 'siste gang tasken ble forsøkt kjørt (før kjøring)';

comment on column prosess_task.siste_kjoering_feil_kode is 'siste feilkode tasken fikk';

comment on column prosess_task.siste_kjoering_feil_tekst is 'siste feil tasken fikk';

comment on column prosess_task.siste_kjoering_server is 'navn på node som sist kjørte en task (server@pid)';

comment on column prosess_task.blokkert_av is 'Id til ProsessTask som blokkerer kjøring av denne (når status=VETO)';

comment on column prosess_task.versjon is 'angir versjon for optimistisk låsing';

comment on column prosess_task.siste_kjoering_slutt_ts is 'siste gang tasken ble forsøkt plukket (klargjort til kjøring)';

CREATE INDEX IDX_PROSESS_TASK_2
    ON PROSESS_TASK (TASK_TYPE);
CREATE INDEX IDX_PROSESS_TASK_3
    ON PROSESS_TASK (NESTE_KJOERING_ETTER);
CREATE INDEX IDX_PROSESS_TASK_5
    ON PROSESS_TASK (TASK_GRUPPE);
CREATE INDEX IDX_PROSESS_TASK_1
    ON PROSESS_TASK (STATUS);
CREATE INDEX IDX_PROSESS_TASK_4
    ON PROSESS_TASK (ID);
CREATE INDEX IDX_PROSESS_TASK_7
    ON PROSESS_TASK (PARTITION_KEY);
CREATE UNIQUE INDEX UIDX_PROSESS_TASK
    ON PROSESS_TASK (ID, STATUS, PARTITION_KEY);

CREATE INDEX IDX_PROSESS_TASK_6 ON PROSESS_TASK (BLOKKERT_AV);

-- Etablerer et sett med bøtter som ferdig tasks kan legge seg i avhengig av hvilken måned de er opprettet i.
-- Legger opp til at disse bøttene kan prunes etter kontinuerlig for å bevare ytelsen
CREATE TABLE PROSESS_TASK_PARTITION_DEFAULT PARTITION OF PROSESS_TASK
    DEFAULT;

CREATE TABLE PROSESS_TASK_PARTITION_FERDIG PARTITION OF PROSESS_TASK
    FOR VALUES IN ('FERDIG') PARTITION BY LIST (PARTITION_KEY);
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_01 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('01');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_02 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('02');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_03 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('03');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_04 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('04');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_05 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('05');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_06 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('06');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_07 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('07');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_08 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('08');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_09 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('09');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_10 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('10');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_11 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('11');
CREATE TABLE PROSESS_TASK_PARTITION_FERDIG_12 PARTITION OF PROSESS_TASK_PARTITION_FERDIG
    FOR VALUES IN ('12');
