package no.nav.foreldrepenger.fpformidling.aksjonspunkt;

public enum AksjonspunktStatus {

    OPPRETTET("OPPR"),
    UTFØRT("UTFO"),
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
