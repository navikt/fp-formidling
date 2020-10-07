alter table dokument_felles alter column kl_spraak_kode drop not null;
alter table dokument_hendelse alter column kl_ytelse_type drop not null;
alter table dokument_hendelse alter column kl_historikk_aktoer drop not null;
alter table dokument_hendelse alter column kl_revurdering_varsling_arsak drop not null;
alter table dokument_hendelse alter column kl_vedtaksbrev drop not null;
alter table eventmottak_feillogg alter column kl_status drop not null;
alter table dokument_historikkinnslag alter column kl_historikk_aktoer drop not null;
alter table dokument_historikkinnslag alter column kl_historikkinnslag_type drop not null;

alter table konfig_verdi_kode drop constraint fk_konfig_verdi_kode_80;
alter table konfig_verdi_kode drop constraint fk_konfig_verdi_kode_81;
alter table konfig_verdi drop constraint fk_konfig_verdi_1;
alter table dokument_mal_type drop constraint fk_dokument_mal_type_1;
alter table dokument_data drop constraint fk_dokument_data_3;
alter table dokument_felles drop constraint fk_dokument_felles_4;
alter table dokument_hendelse drop constraint fk_dokument_hendelse_1;
alter table dokument_hendelse drop constraint fk_dokument_hendelse_3;
alter table dokument_hendelse drop constraint fk_dokument_hendelse_4;
alter table dokument_hendelse drop constraint fk_dokument_hendelse_5;
alter table dokument_hendelse drop constraint fk_dokument_hendelse_6;
alter table eventmottak_feillogg drop constraint fk_eventmottak_feillogg_1;
alter table dokument_historikkinnslag drop constraint fk_dokument_historikkinnslag_1;
alter table dokument_historikkinnslag drop constraint fk_dokument_historikkinnslag_2;
alter table dokument_historikkinnslag drop constraint fk_dokument_historikkinnslag_3;
alter table kodeliste_relasjon drop constraint fk_kodeliste_relasjon_1;
alter table kodeliste_relasjon drop constraint fk_kodeliste_relasjon_2;

create table poststed (
    poststednummer varchar(16) not null
        constraint pk_poststed
            primary key,
    poststednavn varchar(256) not null,
    gyldigfom date not null,
    gyldigtom date DEFAULT to_date('31.12.9999', 'dd.mm.yyyy') not null,
    opprettet_av varchar(20) default 'VL' not null,
    opprettet_tid timestamp(3) default LOCALTIMESTAMP not null,
    endret_av varchar(20),
    endret_tid timestamp(3)
);

comment on table poststed is 'Tabell for sentralt kodeverk Postnummer';
comment on column poststed.poststednummer is 'Postnummer';
comment on column poststed.poststednavn is 'Poststed';
comment on column poststed.gyldigFom is 'Gyldig fra dato';
comment on column poststed.gyldigTom is 'Gyldig til dato';

