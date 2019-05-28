CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE dokument_hendelse
ADD COLUMN bestilling_uuid  UUID NOT NULL DEFAULT uuid_generate_v4();


ALTER TABLE dokument_historikkinnslag
    ADD COLUMN historikk_uuid  UUID NOT NULL DEFAULT uuid_generate_v4();

CREATE INDEX IDX_DOKUMENT_HENDELSE_2 on DOKUMENT_HENDELSE (bestilling_uuid);
COMMENT ON COLUMN DOKUMENT_HENDELSE.bestilling_uuid IS 'Unik ID for bestillingen fra FPSAK';


CREATE INDEX IDX_DOKUMENT_HENDELSE_8 on dokument_historikkinnslag (historikk_uuid);
COMMENT ON COLUMN DOKUMENT_HISTORIKKINNSLAG.historikk_uuid IS 'Unik ID for historikkinnslaget';
