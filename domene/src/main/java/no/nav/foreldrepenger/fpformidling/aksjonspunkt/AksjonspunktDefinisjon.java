package no.nav.foreldrepenger.fpformidling.aksjonspunkt;

public enum AksjonspunktDefinisjon {

    VARSEL_REVURDERING_ETTERKONTROLL("5025"),
    VARSEL_REVURDERING_MANUELL("5026"),
    FASTSETT_BEREGNINGSGRUNNLAG_ARBEIDSTAKER_FRILANS("5038"), //$NON-NLS-1$
    AVKLAR_FAKTA_ANNEN_FORELDER_HAR_IKKE_RETT("5086"),
    UDEFINERT("-");

    private final String kode;

    AksjonspunktDefinisjon(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return this.kode;
    }
}
