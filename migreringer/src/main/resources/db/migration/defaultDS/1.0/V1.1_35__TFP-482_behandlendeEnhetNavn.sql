alter table DOKUMENT_HENDELSE add column BEHANDLENDE_ENHET_NAVN character varying;
comment on column DOKUMENT_HENDELSE.BEHANDLENDE_ENHET_NAVN is 'Navn på behandlende enhet som har bestilt dokumentet';