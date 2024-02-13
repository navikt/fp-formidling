ALTER TABLE dokument_hendelse ALTER COLUMN YTELSE_TYPE DROP NOT NULL;

ALTER TABLE dokument_hendelse ADD COLUMN journalfoer_som VARCHAR(100);
COMMENT ON COLUMN dokument_hendelse.journalfoer_som is 'Dokumenttypen som skal journalføres med. Benyttes kun når vedtak overstyrt med FRITEKSTBREV mal skal sendes.';

ALTER TABLE dokument_hendelse ADD COLUMN dokument_mal VARCHAR(100);
COMMENT ON COLUMN dokument_hendelse.dokument_mal is 'Dokument mal som brukes til å generere dokumentet.';

ALTER TABLE dokument_hendelse ADD COLUMN revurdering_aarsak VARCHAR(100);
COMMENT ON COLUMN dokument_hendelse.revurdering_aarsak is 'Årsak til revurdering - brukes kun i sammenheng med VARSEL_OM_REVURDERING dokument mal.';
