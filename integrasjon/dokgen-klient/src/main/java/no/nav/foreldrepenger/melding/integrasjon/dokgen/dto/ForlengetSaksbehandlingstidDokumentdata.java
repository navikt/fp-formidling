package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class ForlengetSaksbehandlingstidDokumentdata extends Dokumentdata {
    private VariantType variantType;
    private boolean død;
    private int behandlingsfristUker;
    private int antallBarn;

    public static Builder ny() {
        return new Builder();
    }

    public VariantType getVariantType() {
        return variantType;
    }

    public boolean getDød() {
        return død;
    }

    public int getBehandlingsfristUker() {
        return behandlingsfristUker;
    }

    public int getAntallBarn() {
        return antallBarn;
    }

    public enum VariantType {
        FORLENGET,
        MEDLEM,
        FORTIDLIG,
        KLAGE;
    }

    public static class Builder {
        private ForlengetSaksbehandlingstidDokumentdata kladd;

        private Builder() {
            this.kladd = new ForlengetSaksbehandlingstidDokumentdata();
        }

        public Builder medFelles(FellesDokumentdata felles) {
            this.kladd.felles = felles;
            return this;
        }

        public Builder medVariantType(VariantType variantType) {
            this.kladd.variantType = variantType;
            return this;
        }

        public Builder medDød(boolean død) {
            this.kladd.død = død;
            return this;
        }

        public Builder medBehandlingsfristUker(int behandlingsfristUker) {
            this.kladd.behandlingsfristUker = behandlingsfristUker;
            return this;
        }

        public Builder medAntallBarn(int antallBarn) {
            this.kladd.antallBarn = antallBarn;
            return this;
        }

        public ForlengetSaksbehandlingstidDokumentdata build() {
            return this.kladd;
        }
    }
}
