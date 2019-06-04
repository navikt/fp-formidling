package no.nav.foreldrepenger.melding.aksjonspunkt;

public enum AksjonspunktStatus {

    OPPRETTET("OPPR"),
    UTFØRT("UTFØRT"),
    AVBRUTT("AVBR"), //$NON-NLS-1$
    UDEFINERT("-");

    private final String kode;

    AksjonspunktStatus(String kode) {
        this.kode = kode;
    }

    @Override
    public String toString() {
        return kode;
    }

    public String getKode() {
        return this.kode;
    }
}
