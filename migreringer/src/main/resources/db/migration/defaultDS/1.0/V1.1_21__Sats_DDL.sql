--------------------------------------------------------
--  DDL for Table SATS
--------------------------------------------------------

-- Tabell SATS
CREATE TABLE SATS (
  id                         DECIMAL(19) NOT NULL,
  sats_type                  VARCHAR(100) NOT NULL,
  fom                        DATE NOT NULL,
  tom                        DATE NOT NULL,
  verdi                      BIGINT NOT NULL,
  versjon                    DECIMAL(19) DEFAULT 0 NOT NULL,
  opprettet_av               VARCHAR(20) DEFAULT 'VL' NOT NULL,
  opprettet_tid              TIMESTAMP(3) DEFAULT current_timestamp NOT NULL,
  endret_av                  VARCHAR(20),
  endret_tid                 TIMESTAMP(3),
  kl_sats_type               VARCHAR(100) NOT NULL,
  CONSTRAINT PK_SATS PRIMARY KEY ( id )
);

CREATE SEQUENCE SEQ_SATS INCREMENT BY 50 MINVALUE 1 NO MAXVALUE START WITH 1 NO CYCLE;
CREATE INDEX IDX_SATS_1 ON  SATS ( sats_type ASC );

--------------------------------------------------------
--  Constraints for Table SATS
--------------------------------------------------------

ALTER TABLE SATS ADD CONSTRAINT FK_SATS_80 FOREIGN KEY ( sats_type, kl_sats_type ) REFERENCES KODELISTE ( kode, kodeverk );

--------------------------------------------------------
--  Comments for Table SATS
--------------------------------------------------------

COMMENT ON TABLE SATS IS 'Satser brukt ifm beregning av ytelser';
COMMENT ON COLUMN SATS.ID IS 'Primary Key';
COMMENT ON COLUMN SATS.SATS_TYPE IS 'FK: SATS_TYPE';
COMMENT ON COLUMN SATS.FOM IS 'Gyldig Fra-Og-Med';
COMMENT ON COLUMN SATS.TOM IS 'Gyldig Til-Og-Med';
COMMENT ON COLUMN SATS.VERDI is 'Sats verdi.';
COMMENT ON COLUMN SATS.KL_SATS_TYPE IS 'Referanse til KODEVERK-kolonnen i KODELISTE-tabellen';
