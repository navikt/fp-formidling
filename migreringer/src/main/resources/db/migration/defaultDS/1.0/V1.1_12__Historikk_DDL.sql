CREATE TABLE DOKUMENT_HISTORIKKINNSLAG
(
  ID                 BIGINT                                       NOT NULL,
  BEHANDLING_ID      BIGINT                                       NOT NULL,
  JOURNALPOST_ID     VARCHAR(20)                                  NOT NULL,
  HISTORIKK_AKTOER   VARCHAR(100),
  HISTORIKK_TYPE     VARCHAR(100),
  DOKUMENT_MAL_NAVN  VARCHAR(7)                                   NOT NULL,
  DOKUMENT_ID        VARCHAR(20)                                  NOT NULL,
  KL_HISTORIKK_AKTOR VARCHAR(100) DEFAULT 'HISTORIKK_AKTOER'      NOT NULL,
  KL_HISTORIKK_TYPE  VARCHAR(100) DEFAULT 'HISTORIKKINNSLAG_TYPE' NOT NULL,
  VERSJON            INT          DEFAULT 0                       NOT NULL,
  OPPRETTET_AV       VARCHAR(20)  DEFAULT 'VL'                    NOT NULL,
  OPPRETTET_TID      TIMESTAMP(3) DEFAULT LOCALTIMESTAMP          NOT NULL,
  ENDRET_AV          VARCHAR(20),
  ENDRET_TID         TIMESTAMP(3),
  CONSTRAINT PK_DOKUMENT_HISTORIKKINNSLAG PRIMARY KEY (ID),
  CONSTRAINT FK_DOKUMENT_HISTORIKKINNSLAG_1 FOREIGN KEY (DOKUMENT_MAL_NAVN) REFERENCES DOKUMENT_MAL_TYPE (KODE),
  CONSTRAINT FK_DOKUMENT_HISTORIKKINNSLAG_2 FOREIGN KEY (KL_HISTORIKK_AKTOR, HISTORIKK_AKTOER) REFERENCES KODELISTE (kodeverk, kode),
  CONSTRAINT FK_DOKUMENT_HISTORIKKINNSLAG_3 FOREIGN KEY (KL_HISTORIKK_TYPE, HISTORIKK_TYPE) REFERENCES KODELISTE (kodeverk, kode)
);

CREATE SEQUENCE SEQ_DOKUMENT_HISTORIKK MINVALUE 1000000 MAXVALUE 999999999999999999 INCREMENT BY 50 START WITH 1003000 NO CYCLE;

CREATE INDEX IDX_DOKUMENT_HISTORIKKINNSLAG_1 on DOKUMENT_HISTORIKKINNSLAG (BEHANDLING_ID);
CREATE INDEX IDX_DOKUMENT_HISTORIKKINNSLAG_2 on DOKUMENT_HISTORIKKINNSLAG (JOURNALPOST_ID);
CREATE INDEX IDX_DOKUMENT_HISTORIKKINNSLAG_3 on DOKUMENT_HISTORIKKINNSLAG (DOKUMENT_ID);

COMMENT ON TABLE DOKUMENT_HISTORIKKINNSLAG is 'Historikk for bestilte dokumenter';

COMMENT ON COLUMN DOKUMENT_HISTORIKKINNSLAG.ID IS 'PK';
COMMENT ON COLUMN DOKUMENT_HISTORIKKINNSLAG.BEHANDLING_ID IS 'Behandlingen dokumentet gjelder';
COMMENT ON COLUMN DOKUMENT_HISTORIKKINNSLAG.JOURNALPOST_ID IS 'Dokumentets JournalpostID i joark';
COMMENT ON COLUMN DOKUMENT_HISTORIKKINNSLAG.HISTORIKK_AKTOER IS 'Kodeverk, hvem som bestilte dokumentet';
COMMENT ON COLUMN DOKUMENT_HISTORIKKINNSLAG.DOKUMENT_ID IS 'Dokumentets Dokument ID i JOARK';
COMMENT ON COLUMN DOKUMENT_HISTORIKKINNSLAG.KL_HISTORIKK_AKTOR IS 'Kodeliste';