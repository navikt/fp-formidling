ALTER TABLE dokument_hendelse ADD COLUMN fritekst_html text;
COMMENT ON COLUMN dokument_hendelse.fritekst_html is 'Fritekstbrev på html format';
