alter table dokument_felles alter column navn_avsender_enhet drop not null;
alter table dokument_felles alter column kontakt_tlf drop not null;

alter table dokument_felles drop column kl_spraak_kode;
alter table dokument_hendelse drop column kl_ytelse_type;
alter table dokument_hendelse drop column kl_historikk_aktoer;
alter table dokument_hendelse drop column kl_revurdering_varsling_arsak;
alter table dokument_hendelse drop column kl_vedtaksbrev;
alter table eventmottak_feillogg drop column kl_status;
alter table dokument_historikkinnslag drop column kl_historikk_aktoer;
alter table dokument_historikkinnslag drop column kl_historikkinnslag_type;
