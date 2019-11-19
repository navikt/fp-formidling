package no.nav.vedtak.felles.prosesstask.impl;

import no.nav.vedtak.log.sporingslogg.SporingsloggId;
import no.nav.vedtak.log.sporingslogg.StandardSporingsloggId;

public enum ProsessTaskSporingsloggId implements SporingsloggId {
    FNR("fnr"),
    AKTOR_ID("aktorId"),
    ABAC_DECISION("decision"),
    ABAC_ACTION("abac_action"),
    ABAC_RESOURCE_TYPE("abac_resource_type"),
    FAGSAK_ID("fagsakId"),
    BEHANDLING_ID("behandlingId"),
    BEHANDLING_UUID("behandlingUUId"),
    HENDELSE_ID("hendelseId");




    private String eksternKode;

    private ProsessTaskSporingsloggId(String eksternKode) {
        this.eksternKode = eksternKode;
    }

    public String getSporingsloggKode() {
        return this.eksternKode;
    }
}
