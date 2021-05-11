alter table DOKUMENT_FELLES add BREV_DATA text;
comment on column DOKUMENT_FELLES.BREV_DATA is 'Data for brevet slik det ble sendt til Dokprod (XML) eller Dokgen (JSON)';