package no.nav.foreldrepenger.melding.anke;

public class Anke {

    private String ankeVurdering;
    private String begrunnelse;
    private String fritekstTilBrev;
    private String ankeOmgjoerArsak;
    private String ankeOmgjoerArsakNavn;
    private String ankeVurderingOmgjoer;
    private boolean godkjentAvMedunderskriver;
    private boolean erAnkerIkkePart;
    private boolean erFristIkkeOverholdt;
    private boolean erIkkeKonkret;
    private boolean erIkkeSignert;
    private boolean erSubsidiartRealitetsbehandles;
    private Long paAnketBehandlingId;

    private Anke(Builder builder) {
        ankeVurdering = builder.ankeVurdering;
        begrunnelse = builder.begrunnelse;
        fritekstTilBrev = builder.fritekstTilBrev;
        ankeOmgjoerArsak = builder.ankeOmgjoerArsak;
        ankeOmgjoerArsakNavn = builder.ankeOmgjoerArsakNavn;
        ankeVurderingOmgjoer = builder.ankeVurderingOmgjoer;
        godkjentAvMedunderskriver = builder.godkjentAvMedunderskriver;
        erAnkerIkkePart = builder.erAnkerIkkePart;
        erFristIkkeOverholdt = builder.erFristIkkeOverholdt;
        erIkkeKonkret = builder.erIkkeKonkret;
        erIkkeSignert = builder.erIkkeSignert;
        erSubsidiartRealitetsbehandles = builder.erSubsidiartRealitetsbehandles;
        paAnketBehandlingId = builder.paAnketBehandlingId;
    }

    public static Builder ny() {
        return new Builder();
    }

    public String getAnkeVurdering() {
        return ankeVurdering;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public String getAnkeOmgjoerArsak() {
        return ankeOmgjoerArsak;
    }

    public String getAnkeOmgjoerArsakNavn() {
        return ankeOmgjoerArsakNavn;
    }

    public String getAnkeVurderingOmgjoer() {
        return ankeVurderingOmgjoer;
    }

    public boolean isGodkjentAvMedunderskriver() {
        return godkjentAvMedunderskriver;
    }

    public boolean isErAnkerIkkePart() {
        return erAnkerIkkePart;
    }

    public boolean isErFristIkkeOverholdt() {
        return erFristIkkeOverholdt;
    }

    public boolean isErIkkeKonkret() {
        return erIkkeKonkret;
    }

    public boolean isErIkkeSignert() {
        return erIkkeSignert;
    }

    public boolean isErSubsidiartRealitetsbehandles() {
        return erSubsidiartRealitetsbehandles;
    }

    public Long getPaAnketBehandlingId() {
        return paAnketBehandlingId;
    }

    public static final class Builder {
        private String ankeVurdering;
        private String begrunnelse;
        private String fritekstTilBrev;
        private String ankeOmgjoerArsak;
        private String ankeOmgjoerArsakNavn;
        private String ankeVurderingOmgjoer;
        private boolean godkjentAvMedunderskriver;
        private boolean erAnkerIkkePart;
        private boolean erFristIkkeOverholdt;
        private boolean erIkkeKonkret;
        private boolean erIkkeSignert;
        private boolean erSubsidiartRealitetsbehandles;
        private Long paAnketBehandlingId;

        public Builder() {
        }

        public Builder medAnkeVurdering(String val) {
            ankeVurdering = val;
            return this;
        }

        public Builder medBegrunnelse(String val) {
            begrunnelse = val;
            return this;
        }

        public Builder medFritekstTilBrev(String val) {
            fritekstTilBrev = val;
            return this;
        }

        public Builder medAnkeOmgjoerArsak(String val) {
            ankeOmgjoerArsak = val;
            return this;
        }

        public Builder medAnkeOmgjoerArsakNavn(String val) {
            ankeOmgjoerArsakNavn = val;
            return this;
        }

        public Builder medAnkeVurderingOmgjoer(String val) {
            ankeVurderingOmgjoer = val;
            return this;
        }

        public Builder medGodkjentAvMedunderskriver(boolean val) {
            godkjentAvMedunderskriver = val;
            return this;
        }

        public Builder medErAnkerIkkePart(boolean val) {
            erAnkerIkkePart = val;
            return this;
        }

        public Builder medErFristIkkeOverholdt(boolean val) {
            erFristIkkeOverholdt = val;
            return this;
        }

        public Builder medErIkkeKonkret(boolean val) {
            erIkkeKonkret = val;
            return this;
        }

        public Builder medErIkkeSignert(boolean val) {
            erIkkeSignert = val;
            return this;
        }

        public Builder medErSubsidiartRealitetsbehandles(boolean val) {
            erSubsidiartRealitetsbehandles = val;
            return this;
        }

        public Builder medPaAnketBehandlingId(Long val) {
            paAnketBehandlingId = val;
            return this;
        }

        public Anke build() {
            return new Anke(this);
        }
    }
}
