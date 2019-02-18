--------------------------------------------------------
--  DDL for Table KODEVERK
--------------------------------------------------------

CREATE TABLE KODEVERK
(
  KODE                       VARCHAR(100),
  KODEVERK_EIER              VARCHAR(100) DEFAULT 'VL',
  KODEVERK_EIER_REF          VARCHAR(1000),
  KODEVERK_EIER_VER          VARCHAR(20),
  KODEVERK_EIER_NAVN         VARCHAR(100),
  KODEVERK_SYNK_NYE          CHAR(1)      DEFAULT 'J',
  KODEVERK_SYNK_EKSISTERENDE CHAR(1)      DEFAULT 'J',
  NAVN                       VARCHAR(256),
  BESKRIVELSE                VARCHAR(4000),
  OPPRETTET_AV               VARCHAR(200) DEFAULT 'VL',
  OPPRETTET_TID              TIMESTAMP(3) DEFAULT current_timestamp,
  ENDRET_AV                  VARCHAR(200),
  ENDRET_TID                 TIMESTAMP(3),
  SAMMENSATT                 VARCHAR(1)   DEFAULT 'N'
);

COMMENT ON COLUMN KODEVERK.KODE IS 'PK - definerer kodeverk';
COMMENT ON COLUMN KODEVERK.KODEVERK_EIER IS 'Offisielt kodeverk eier (kode)';
COMMENT ON COLUMN KODEVERK.KODEVERK_EIER_REF IS 'Offisielt kodeverk referanse (url)';
COMMENT ON COLUMN KODEVERK.KODEVERK_EIER_VER IS 'Offisielt kodeverk versjon';
COMMENT ON COLUMN KODEVERK.KODEVERK_EIER_NAVN IS 'Offisielt kodeverk navn';
COMMENT ON COLUMN KODEVERK.KODEVERK_SYNK_NYE IS 'Om nye koder fra kodeverkeier skal legges til ved oppdatering.';
COMMENT ON COLUMN KODEVERK.KODEVERK_SYNK_EKSISTERENDE IS 'Om eksisterende koder fra kodeverkeier skal endres ved oppdatering.';
COMMENT ON COLUMN KODEVERK.NAVN IS 'Navn på kodeverk';
COMMENT ON COLUMN KODEVERK.BESKRIVELSE IS 'Beskrivelse av kodeverk';
COMMENT ON COLUMN KODEVERK.SAMMENSATT IS 'Skiller mellom sammensatt kodeverk og enkel kodeliste';
COMMENT ON TABLE KODEVERK IS 'Registrerte kodeverk. Representerer grupperinger av koder';

--------------------------------------------------------
--  Constraints for Table KODEVERK
--------------------------------------------------------

ALTER TABLE KODEVERK
  ALTER COLUMN NAVN SET NOT NULL;
ALTER TABLE KODEVERK
  ALTER COLUMN KODEVERK_SYNK_EKSISTERENDE SET NOT NULL;
ALTER TABLE KODEVERK
  ALTER COLUMN KODEVERK_SYNK_NYE SET NOT NULL;
ALTER TABLE KODEVERK
  ALTER COLUMN KODEVERK_EIER SET NOT NULL;
ALTER TABLE KODEVERK
  ALTER COLUMN KODE SET NOT NULL;
ALTER TABLE KODEVERK
  ALTER COLUMN OPPRETTET_TID SET NOT NULL;
ALTER TABLE KODEVERK
  ALTER COLUMN OPPRETTET_AV SET NOT NULL;
ALTER TABLE KODEVERK
  ADD CONSTRAINT PK_KODEVERK PRIMARY KEY (KODE);
ALTER TABLE KODEVERK
  ADD CHECK (kodeverk_synk_eksisterende IN ('J', 'N'));
ALTER TABLE KODEVERK
  ADD CHECK (kodeverk_synk_nye IN ('J', 'N'));

