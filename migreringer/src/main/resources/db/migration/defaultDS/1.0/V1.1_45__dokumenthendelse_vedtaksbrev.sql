alter table DOKUMENT_HENDELSE add VEDTAKSBREV varchar(100) default '-' not null;
alter table DOKUMENT_HENDELSE add KL_VEDTAKSBREV varchar(100) default 'VEDTAKSBREV' not null;
alter table DOKUMENT_HENDELSE add constraint FK_DOKUMENT_HENDELSE_6 foreign key (VEDTAKSBREV, KL_VEDTAKSBREV) references KODELISTE (KODE, KODEVERK);
comment on column DOKUMENT_HENDELSE.VEDTAKSBREV is 'FK: Kodeverk VEDTAKSBREV i KODELISTE-tabellen';