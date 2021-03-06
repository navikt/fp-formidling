CREATE TABLE PROSESS_TASK
(
    ID                        NUMERIC,
    TASK_TYPE                 VARCHAR(50),
    PRIORITET                 NUMERIC(3, 0) DEFAULT 0,
    STATUS                    VARCHAR(20)   DEFAULT 'KLAR',
    TASK_PARAMETERE           VARCHAR(4000),
    TASK_PAYLOAD              TEXT,
    TASK_GRUPPE               VARCHAR(250),
    TASK_SEKVENS              VARCHAR(100)  DEFAULT '1',
    PARTITION_KEY             VARCHAR(4)    DEFAULT to_char(current_date, 'MM'),
    NESTE_KJOERING_ETTER      TIMESTAMP(0)  DEFAULT current_timestamp,
    FEILEDE_FORSOEK           NUMERIC(5, 0) DEFAULT 0,
    SISTE_KJOERING_TS         TIMESTAMP(6),
    SISTE_KJOERING_FEIL_KODE  VARCHAR(50),
    SISTE_KJOERING_FEIL_TEKST TEXT,
    SISTE_KJOERING_SERVER     VARCHAR(50),
    OPPRETTET_AV              VARCHAR(20)   DEFAULT 'VL',
    OPPRETTET_TID             TIMESTAMP(6)  DEFAULT current_timestamp NOT NULL,
    BLOKKERT_AV               NUMERIC                                 NULL,
    VERSJON                   NUMERIC       DEFAULT 0
) PARTITION BY LIST (STATUS);
COMMENT ON COLUMN PROSESS_TASK.ID IS 'Primary Key';
COMMENT ON COLUMN PROSESS_TASK.TASK_TYPE IS 'navn på task. Brukes til å matche riktig implementasjon';
COMMENT ON COLUMN PROSESS_TASK.PRIORITET IS 'prioritet på task.  Høyere tall har høyere prioritet';
COMMENT ON COLUMN PROSESS_TASK.STATUS IS 'status på task: KLAR, NYTT_FORSOEK, FEILET, VENTER_SVAR, FERDIG';
COMMENT ON COLUMN PROSESS_TASK.TASK_PARAMETERE IS 'parametere angitt for en task';
COMMENT ON COLUMN PROSESS_TASK.TASK_PAYLOAD IS 'inputdata for en task';
COMMENT ON COLUMN PROSESS_TASK.TASK_GRUPPE IS 'angir en unik id som grupperer flere ';
COMMENT ON COLUMN PROSESS_TASK.TASK_SEKVENS IS 'angir rekkefølge på task innenfor en gruppe ';
COMMENT ON COLUMN PROSESS_TASK.NESTE_KJOERING_ETTER IS 'tasken skal ikke kjøeres før tidspunkt er passert';
COMMENT ON COLUMN PROSESS_TASK.FEILEDE_FORSOEK IS 'antall feilede forsøk';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_TS IS 'siste gang tasken ble forsøkt kjørt';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_FEIL_KODE IS 'siste feilkode tasken fikk';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_FEIL_TEKST IS 'siste feil tasken fikk';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_SERVER IS 'navn på node som sist kjørte en task (server@pid)';
COMMENT ON COLUMN PROSESS_TASK.VERSJON IS 'angir versjon for optimistisk låsing';
COMMENT ON COLUMN PROSESS_TASK.BLOKKERT_AV IS 'Id til ProsessTask som blokkerer kjøring av denne (når status=VETO)';
COMMENT ON TABLE PROSESS_TASK IS 'Inneholder tasks som skal kjøres i bakgrunnen';

CREATE TABLE PROSESS_TASK_FEILHAND
(
  KODE            VARCHAR(20),
  NAVN            VARCHAR(50),
  BESKRIVELSE     VARCHAR(2000),
  OPPRETTET_AV    VARCHAR(20)  DEFAULT 'VL',
  OPPRETTET_TID   TIMESTAMP(3) DEFAULT localtimestamp,
  ENDRET_AV       VARCHAR(20),
  ENDRET_TID      TIMESTAMP(3),
  INPUT_VARIABEL1 bigint,
  INPUT_VARIABEL2 bigint
);

COMMENT ON COLUMN PROSESS_TASK_FEILHAND.KODE IS 'Kodeverk Primary Key';
COMMENT ON COLUMN PROSESS_TASK_FEILHAND.NAVN IS 'Lesbart navn på type feilhåndtering brukt i prosesstask';
COMMENT ON COLUMN PROSESS_TASK_FEILHAND.BESKRIVELSE IS 'Utdypende beskrivelse av koden';
COMMENT ON COLUMN PROSESS_TASK_FEILHAND.INPUT_VARIABEL1 IS 'Variabel 1: Dynamisk konfigurasjon for en feilhåndteringsstrategi.  Verdi og betydning er bestemt av feilhåndteringsalgoritmen';
COMMENT ON COLUMN PROSESS_TASK_FEILHAND.INPUT_VARIABEL2 IS 'Variabel 2: Dynamisk konfigurasjon for en feilhåndteringsstrategi.  Verdi og betydning er bestemt av feilhåndteringsalgoritmen';
COMMENT ON TABLE PROSESS_TASK_FEILHAND IS 'Kodetabell for feilhåndterings-metoder. For eksempel antall ganger å prøve på nytt og til hvilke tidspunkt';

CREATE TABLE PROSESS_TASK_TYPE
(
  KODE                     VARCHAR(50),
  NAVN                     VARCHAR(50),
  FEIL_MAKS_FORSOEK        integer      DEFAULT 1,
  FEIL_SEK_MELLOM_FORSOEK  integer      DEFAULT 30,
  CRON_EXPRESSION          VARCHAR(200) NULL,
  FEILHANDTERING_ALGORITME VARCHAR(20)  DEFAULT 'DEFAULT',
  BESKRIVELSE              VARCHAR(2000),
  OPPRETTET_AV             VARCHAR(20)  DEFAULT 'VL',
  OPPRETTET_TID            TIMESTAMP(3) DEFAULT localtimestamp,
  ENDRET_AV                VARCHAR(20),
  ENDRET_TID               TIMESTAMP(3)
);

COMMENT ON COLUMN PROSESS_TASK_TYPE.KODE IS 'Kodeverk Primary Key';
COMMENT ON COLUMN PROSESS_TASK_TYPE.NAVN IS 'Lesbart navn på prosesstasktype';
COMMENT ON COLUMN PROSESS_TASK_TYPE.FEIL_MAKS_FORSOEK IS 'Maksimalt anntall forsøk på rekjøring om noe går galt';
COMMENT ON COLUMN PROSESS_TASK_TYPE.CRON_EXPRESSION IS 'Cron-expression for når oppgaven skal kjøres på nytt';
COMMENT ON COLUMN PROSESS_TASK_TYPE.FEIL_SEK_MELLOM_FORSOEK IS 'Ventetid i sekunder mellom hvert forsøk på rekjøring om noe har gått galt';
COMMENT ON COLUMN PROSESS_TASK_TYPE.FEILHANDTERING_ALGORITME IS 'FK:PROSESS_TASK_FEILHAND Fremmednøkkel til tabell som viser detaljer om hvordan en feilsituasjon skal håndteres';
COMMENT ON COLUMN PROSESS_TASK_TYPE.BESKRIVELSE IS 'Utdypende beskrivelse av koden';
COMMENT ON TABLE PROSESS_TASK_TYPE IS 'Kodetabell for typer prosesser med beskrivelse og informasjon om hvilken feilhåndteringen som skal benyttes';

CREATE SEQUENCE SEQ_PROSESS_TASK MINVALUE 1000000 MAXVALUE 999999999999999999 INCREMENT BY 50 START WITH 1032200 NO CYCLE;

CREATE SEQUENCE SEQ_PROSESS_TASK_GRUPPE MINVALUE 10000000 MAXVALUE 999999999999999999 INCREMENT BY 50 START WITH 21384000000 NO CYCLE;


ALTER TABLE PROSESS_TASK
  ALTER COLUMN OPPRETTET_TID SET NOT NULL;
ALTER TABLE PROSESS_TASK
  ALTER COLUMN OPPRETTET_AV SET NOT NULL;
ALTER TABLE PROSESS_TASK
    ADD CONSTRAINT PK_PROSESS_TASK PRIMARY KEY (ID, STATUS, PARTITION_KEY);
ALTER TABLE PROSESS_TASK
  ADD CONSTRAINT CHK_PROSESS_TASK_STATUS CHECK (STATUS in
                                                ('KLAR', 'FEILET', 'VENTER_SVAR', 'SUSPENDERT', 'VETO', 'FERDIG'));
ALTER TABLE PROSESS_TASK
  ALTER COLUMN VERSJON SET NOT NULL;
ALTER TABLE PROSESS_TASK
  ALTER COLUMN TASK_SEKVENS SET NOT NULL;
ALTER TABLE PROSESS_TASK
  ALTER COLUMN STATUS SET NOT NULL;
ALTER TABLE PROSESS_TASK
  ALTER COLUMN PRIORITET SET NOT NULL;
ALTER TABLE PROSESS_TASK
  ALTER COLUMN TASK_TYPE SET NOT NULL;
ALTER TABLE PROSESS_TASK
  ALTER COLUMN ID SET NOT NULL;

ALTER TABLE PROSESS_TASK_FEILHAND
  ADD CONSTRAINT PK_PROSESS_TASK_FEILHAND PRIMARY KEY (KODE);
ALTER TABLE PROSESS_TASK_FEILHAND
  ALTER COLUMN OPPRETTET_TID SET NOT NULL;
ALTER TABLE PROSESS_TASK_FEILHAND
  ALTER COLUMN OPPRETTET_AV SET NOT NULL;
ALTER TABLE PROSESS_TASK_FEILHAND
  ALTER COLUMN NAVN SET NOT NULL;
ALTER TABLE PROSESS_TASK_FEILHAND
  ALTER COLUMN KODE SET NOT NULL;

ALTER TABLE PROSESS_TASK_TYPE
  ADD CONSTRAINT PK_PROSESS_TASK_TYPE PRIMARY KEY (KODE);
ALTER TABLE PROSESS_TASK_TYPE
  ALTER COLUMN OPPRETTET_TID SET NOT NULL;
ALTER TABLE PROSESS_TASK_TYPE
  ALTER COLUMN OPPRETTET_AV SET NOT NULL;
ALTER TABLE PROSESS_TASK_TYPE
  ALTER COLUMN FEIL_SEK_MELLOM_FORSOEK SET NOT NULL;
ALTER TABLE PROSESS_TASK_TYPE
  ALTER COLUMN FEIL_MAKS_FORSOEK SET NOT NULL;
ALTER TABLE PROSESS_TASK_TYPE
  ALTER COLUMN KODE SET NOT NULL;


ALTER TABLE PROSESS_TASK
  ADD CONSTRAINT FK_PROSESS_TASK_1 FOREIGN KEY (TASK_TYPE)
    REFERENCES PROSESS_TASK_TYPE (KODE);

ALTER TABLE PROSESS_TASK_TYPE
  ADD CONSTRAINT FK_PROSESS_TASK_TYPE_1 FOREIGN KEY (FEILHANDTERING_ALGORITME)
    REFERENCES PROSESS_TASK_FEILHAND (KODE);

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
CREATE UNIQUE INDEX UIDX_PROSESS_TASK_FEILHAND
    ON PROSESS_TASK_FEILHAND (KODE);
CREATE UNIQUE INDEX UIDX_PROSESS_TASK_TYPE
    ON PROSESS_TASK_TYPE (KODE);
CREATE INDEX IDX_PROSESS_TASK_TYPE_1
    ON PROSESS_TASK_TYPE (FEILHANDTERING_ALGORITME);
CREATE INDEX IDX_PROSESS_TASK_6 ON PROSESS_TASK (BLOKKERT_AV);

alter table prosess_task
    add column SISTE_KJOERING_SLUTT_TS timestamp(6);
alter table prosess_task
    add column SISTE_KJOERING_PLUKK_TS timestamp(6);

COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_TS IS 'siste gang tasken ble forsøkt kjørt (før kjøring)';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_SLUTT_TS IS 'tidsstempel siste gang tasken ble kjørt (etter kjøring)';
COMMENT ON COLUMN PROSESS_TASK.SISTE_KJOERING_SLUTT_TS IS 'siste gang tasken ble forsøkt plukket (klargjort til kjøring)';


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
