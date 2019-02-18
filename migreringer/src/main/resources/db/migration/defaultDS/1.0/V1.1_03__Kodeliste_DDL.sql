--------------------------------------------------------
--  DDL for Table KODELISTE
--------------------------------------------------------

CREATE TABLE KODELISTE
(
  ID             bigint,
  KODEVERK       VARCHAR(100),
  KODE           VARCHAR(100),
  OFFISIELL_KODE VARCHAR(1000),
  NAVN           VARCHAR(256),
  BESKRIVELSE    VARCHAR(4000),
  SPRAK          VARCHAR(3)   DEFAULT 'NB',
  GYLDIG_FOM     DATE         DEFAULT current_timestamp,
  GYLDIG_TOM     DATE         DEFAULT to_date('31.12.9999', 'dd.mm.yyyy'),
  OPPRETTET_AV   VARCHAR(200) DEFAULT 'VL',
  OPPRETTET_TID  TIMESTAMP(3) DEFAULT localtimestamp,
  ENDRET_AV      VARCHAR(200),
  ENDRET_TID     TIMESTAMP(3),
  EKSTRA_DATA    VARCHAR(4000)
);

CREATE SEQUENCE SEQ_KODELISTE MINVALUE 1 MAXVALUE 999999999999999999 INCREMENT BY 50 START WITH 1368400 NO CYCLE;
create index IDX_KODELISTE_6 on KODELISTE (KODEVERK);

COMMENT ON COLUMN KODELISTE.ID IS 'Primary Key';
COMMENT ON COLUMN KODELISTE.KODEVERK IS '(PK) og FK - kodeverk';
COMMENT ON COLUMN KODELISTE.KODE IS '(PK) Unik kode innenfor kodeverk. Denne koden er alltid brukt internt';
COMMENT ON COLUMN KODELISTE.OFFISIELL_KODE IS '(Optional) Offisiell kode hos kodeverkeier. Denne kan avvike fra kode der systemet har egne koder. Kan brukes til å veksle inn kode i offisiell kode når det trengs for integrasjon med andre systemer';
COMMENT ON COLUMN KODELISTE.NAVN IS 'Navn på Kodeverket. Offsielt navn synkes dersom Offsiell kode er satt';
COMMENT ON COLUMN KODELISTE.BESKRIVELSE IS 'Beskrivelse av koden';
COMMENT ON COLUMN KODELISTE.SPRAK IS 'Språk Kodeverket er definert for, default NB (norsk bokmål). Bruker ISO 639-1 standard men med store bokstaver siden det representert slik i NAVs offisielle Kodeverk';
COMMENT ON COLUMN KODELISTE.GYLDIG_FOM IS 'Dato Kodeverket er gyldig fra og med';
COMMENT ON COLUMN KODELISTE.GYLDIG_TOM IS 'Dato Kodeverket er gyldig til og med';
COMMENT ON COLUMN KODELISTE.EKSTRA_DATA IS '(Optional) Tilleggsdata brukt av kodeverket.  Format er kodeverk spesifikt - eks. kan være tekst, json, key-value, etc.';
COMMENT ON TABLE KODELISTE IS 'Inneholder lister av koder for alle Kodeverk som benyttes i applikasjonen.  Både offisielle (synkronisert fra sentralt hold i Nav) såvel som interne Kodeverk.  Offisielle koder skiller seg ut ved at nav_offisiell_kode er populert. Følgelig vil gyldig_tom/fom, navn, språk og beskrivelse lastes ned fra Kodeverkklienten eller annen kilde sentralt';

--------------------------------------------------------
--  DDL for Index IDX_KODELISTE_1
--------------------------------------------------------

CREATE INDEX IDX_KODELISTE_1
  ON KODELISTE (KODE);
--------------------------------------------------------
--  DDL for Index IDX_KODELISTE_2
--------------------------------------------------------

CREATE INDEX IDX_KODELISTE_2
  ON KODELISTE (OFFISIELL_KODE);
--------------------------------------------------------
--  DDL for Index IDX_KODELISTE_3
--------------------------------------------------------

CREATE INDEX IDX_KODELISTE_3
  ON KODELISTE (GYLDIG_FOM);
--------------------------------------------------------
--  DDL for Index UIDX_KODELISTE_1
--------------------------------------------------------

CREATE UNIQUE INDEX UIDX_KODELISTE_1
  ON KODELISTE (KODE, KODEVERK);
--------------------------------------------------------
--  Constraints for Table KODELISTE
--------------------------------------------------------

ALTER TABLE KODELISTE
  ADD CONSTRAINT CHK_UNIQUE_KODELISTE UNIQUE (KODE, KODEVERK);
ALTER TABLE KODELISTE
  ADD CONSTRAINT PK_KODELISTE PRIMARY KEY (ID);
ALTER TABLE KODELISTE
  ALTER COLUMN OPPRETTET_TID SET NOT NULL;
ALTER TABLE KODELISTE
  ALTER COLUMN OPPRETTET_AV SET NOT NULL;
ALTER TABLE KODELISTE
  ALTER COLUMN SPRAK SET NOT NULL;
ALTER TABLE KODELISTE
  ALTER COLUMN GYLDIG_TOM SET NOT NULL;
ALTER TABLE KODELISTE
  ALTER COLUMN GYLDIG_FOM SET NOT NULL;
ALTER TABLE KODELISTE
  ALTER COLUMN KODE SET NOT NULL;
ALTER TABLE KODELISTE
  ALTER COLUMN KODEVERK SET NOT NULL;
ALTER TABLE KODELISTE
  ALTER COLUMN ID SET NOT NULL;
--------------------------------------------------------
--  Ref Constraints for Table KODELISTE
--------------------------------------------------------

ALTER TABLE KODELISTE
  ADD CONSTRAINT FK_KODELISTE_01 FOREIGN KEY (KODEVERK)
    REFERENCES KODEVERK (KODE);
