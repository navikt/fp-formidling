ALTER TABLE dokument_hendelse
ADD COLUMN bestilling_uuid UUID;

ALTER TABLE dokument_historikkinnslag
    ADD COLUMN historikk_uuid UUID;

ALTER TABLE dokument_hendelse
    ALTER COLUMN bestilling_uuid SET NOT NULL;

ALTER TABLE dokument_historikkinnslag
    ALTER COLUMN historikk_uuid SET NOT NULL;


CREATE INDEX IDX_DOKUMENT_HENDELSE_2 on DOKUMENT_HENDELSE (bestilling_uuid);
COMMENT ON COLUMN DOKUMENT_HENDELSE.bestilling_uuid IS 'Unik ID for bestillingen fra FPSAK';


CREATE INDEX IDX_DOKUMENT_HENDELSE_8 on dokument_historikkinnslag (historikk_uuid);
COMMENT ON COLUMN DOKUMENT_HISTORIKKINNSLAG.historikk_uuid IS 'Unik ID for historikkinnslaget';
