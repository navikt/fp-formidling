package no.nav.foreldrepenger.melding.sikkerhet.pdp;

import no.nav.vedtak.sikkerhet.abac.AbacAttributtType;
import no.nav.vedtak.sikkerhet.abac.StandardAbacAttributtType;

public enum AppAbacAttributtType implements AbacAttributtType {

    BEHANDLINGSSTATUS("behandlingsStatus"),
    SAKSSTATUS("saksStatus")
    ;

    public static AbacAttributtType AKTØR_ID = StandardAbacAttributtType.AKTØR_ID;

    public static AbacAttributtType BEHANDLING_UUID = StandardAbacAttributtType.BEHANDLING_UUID;

    public static final String RESOURCE_FORELDREPENGER_SAK_BEHANDLINGSSTATUS = "no.nav.abac.attributter.resource.foreldrepenger.sak.behandlingsstatus";
    public static final String RESOURCE_FORELDREPENGER_SAK_SAKSSTATUS = "no.nav.abac.attributter.resource.foreldrepenger.sak.saksstatus";

    private final boolean maskerOutput;
    private final String sporingsloggEksternKode;

    AppAbacAttributtType() {
        sporingsloggEksternKode = null;
        maskerOutput = false;
    }

    AppAbacAttributtType(String sporingsloggEksternKode) {
        this.sporingsloggEksternKode = sporingsloggEksternKode;
        this.maskerOutput = false;
    }

    AppAbacAttributtType(String sporingsloggEksternKode, boolean maskerOutput) {
        this.sporingsloggEksternKode = sporingsloggEksternKode;
        this.maskerOutput = maskerOutput;
    }

    @Override
    public boolean getMaskerOutput() {
        return maskerOutput;
    }

    @Override
    public String getSporingsloggKode() {
        return sporingsloggEksternKode;
    }

}
