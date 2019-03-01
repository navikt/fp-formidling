--------------------------------------------------------
--  DDL for Table DOKUMENT_HENDELSE
--------------------------------------------------------

CREATE TABLE DOKUMENT_HENDELSE
(
  ID                 bigint,
  BEHANDLING_ID      bigint                                 NOT NULL,
  BEHANDLING_TYPE    VARCHAR(100),
  YTELSE_TYPE        VARCHAR(100),
  DOKUMENT_MAL_NAVN  VARCHAR(7),
  GJELDER_VEDTAK     BOOLEAN      DEFAULT FALSE,
  FRITEKST           TEXT,
  TITTEL             VARCHAR(200),
  OPPRETTET_AV       VARCHAR(20)  DEFAULT 'VL',
  OPPRETTET_TID      TIMESTAMP(3) DEFAULT localtimestamp,
  ENDRET_AV          VARCHAR(20),
  ENDRET_TID         TIMESTAMP(3),
  KL_BEHANDLING_TYPE VARCHAR(100) DEFAULT 'BEHANDLING_TYPE' NOT NULL,
  KL_YTELSE_TYPE     VARCHAR(100) DEFAULT 'FAGSAK_YTELSE'   NOT NULL,
  CONSTRAINT FK_DOKUMENT_HENDELSE_1 FOREIGN KEY (DOKUMENT_MAL_NAVN) REFERENCES DOKUMENT_MAL_TYPE (KODE),
  CONSTRAINT FK_DOKUMENT_HENDELSE_2 FOREIGN KEY (BEHANDLING_TYPE, KL_BEHANDLING_TYPE) REFERENCES KODELISTE (KODE, KODEVERK),
  CONSTRAINT FK_DOKUMENT_HENDELSE_3 FOREIGN KEY (YTELSE_TYPE, KL_YTELSE_TYPE) REFERENCES KODELISTE (KODE, KODEVERK)
);

CREATE SEQUENCE SEQ_DOKUMENT_HENDELSE MINVALUE 1000000 MAXVALUE 999999999999999999 INCREMENT BY 50 START WITH 1003000 NO CYCLE;

ALTER TABLE DOKUMENT_HENDELSE
  ADD CONSTRAINT PK_HENDELSE PRIMARY KEY (ID);


COMMENT ON COLUMN DOKUMENT_HENDELSE.ID IS 'Primary Key';
COMMENT ON COLUMN DOKUMENT_HENDELSE.BEHANDLING_ID IS 'Id til tilhørende behandling i foreldrepengeløsningen';
COMMENT ON COLUMN DOKUMENT_HENDELSE.BEHANDLING_TYPE IS 'Behandlingstype  i foreldrepengeløsningen - kodeverk';
COMMENT ON COLUMN DOKUMENT_HENDELSE.YTELSE_TYPE IS 'Ytelse type til fagsaken i foreldrepengeløsningen - kodeverk';
COMMENT ON COLUMN DOKUMENT_HENDELSE.DOKUMENT_MAL_NAVN IS 'Navn på ønsket dokument';
COMMENT ON COLUMN DOKUMENT_HENDELSE.GJELDER_VEDTAK IS 'Om det er vedtaksdokument';
COMMENT ON COLUMN DOKUMENT_HENDELSE.FRITEKST IS 'Fritekst som skal vises i dokumentet';
COMMENT ON COLUMN DOKUMENT_HENDELSE.TITTEL IS 'Tittel for fritekstbrev';
COMMENT ON COLUMN DOKUMENT_HENDELSE.KL_YTELSE_TYPE IS 'Referanse til KODEVERK-kolonnen i KODELISTE-tabellen';
COMMENT ON COLUMN DOKUMENT_HENDELSE.KL_BEHANDLING_TYPE IS 'Referanse til KODEVERK-kolonnen i KODELISTE-tabellen';
COMMENT ON TABLE DOKUMENT_HENDELSE IS 'Hendelser om dokumenter bestilt eller forhåndvist fra foreldrepengeløsningen';
