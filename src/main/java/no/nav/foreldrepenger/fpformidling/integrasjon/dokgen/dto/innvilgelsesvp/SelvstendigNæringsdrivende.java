package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Beløp;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SelvstendigNæringsdrivende {
    private Boolean nyoppstartet;
    private Beløp årsinntekt;
    private int sistLignedeÅr;
    private boolean inntektLavereATSN;
    private boolean inntektLavereATFLSN;
    private boolean inntektLavereFLSN;

    public boolean getNyoppstartet() {
        return nyoppstartet;
    }

    public Beløp getÅrsinntekt() {
        return årsinntekt;
    }

    public int getSistLignedeÅr() {
        return sistLignedeÅr;
    }

    public boolean getInntektLavereATSN() {
        return inntektLavereATSN;
    }

    public boolean getInntektLavereATFLSN() {
        return inntektLavereATFLSN;
    }

    public boolean getInntektLavereFLSN() {
        return inntektLavereFLSN;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (SelvstendigNæringsdrivende) object;
        return Objects.equals(nyoppstartet, that.nyoppstartet) && Objects.equals(årsinntekt, that.årsinntekt) && Objects.equals(sistLignedeÅr,
            that.sistLignedeÅr) && Objects.equals(inntektLavereATSN, that.inntektLavereATSN) && Objects.equals(inntektLavereATFLSN,
            that.inntektLavereATFLSN) && Objects.equals(inntektLavereFLSN, that.inntektLavereFLSN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nyoppstartet, årsinntekt, sistLignedeÅr, inntektLavereATSN, inntektLavereATFLSN, inntektLavereFLSN);
    }

    public static Builder ny() {
        return new Builder();
    }

    public static Builder ny(SelvstendigNæringsdrivende eksisterende) {
        return new Builder(eksisterende);
    }

    public static class Builder {
        private SelvstendigNæringsdrivende kladd;

        private Builder() {
            this.kladd = new SelvstendigNæringsdrivende();
        }

        private Builder(SelvstendigNæringsdrivende eksisterende) {
            this.kladd = new SelvstendigNæringsdrivende();
            if (eksisterende != null) {
                this.kladd.nyoppstartet = eksisterende.getNyoppstartet();
                this.kladd.årsinntekt = eksisterende.getÅrsinntekt();
                this.kladd.sistLignedeÅr = eksisterende.getSistLignedeÅr();
                this.kladd.inntektLavereATSN = eksisterende.getInntektLavereATSN();
                this.kladd.inntektLavereATFLSN = eksisterende.getInntektLavereATFLSN();
                this.kladd.inntektLavereFLSN = eksisterende.getInntektLavereFLSN();
            }
        }

        public Builder medNyoppstartet(boolean nyoppstartet) {
            if (this.kladd.nyoppstartet != null) {
                this.kladd.nyoppstartet = this.kladd.nyoppstartet && nyoppstartet;
            } else {
                this.kladd.nyoppstartet = nyoppstartet;
            }
            return this;
        }

        public Builder leggTilÅrsinntekt(BigDecimal årsinntekt) {
            if (this.kladd.årsinntekt != null) {
                this.kladd.årsinntekt = Beløp.of(this.kladd.årsinntekt.getVerdi() + årsinntekt.longValue());
            } else {
                this.kladd.årsinntekt = Beløp.of(årsinntekt);
            }
            return this;
        }

        public Builder medSistLignedeÅr(int sistLignedeÅr) {
            this.kladd.sistLignedeÅr = sistLignedeÅr;
            return this;
        }

        public Builder medInntektLavereAtSn(boolean inntektLavereATSN) {
            this.kladd.inntektLavereATSN = inntektLavereATSN;
            return this;
        }

        public Builder medInntektLavereAtFlSn(boolean inntektLavereATFLSN) {
            this.kladd.inntektLavereATFLSN = inntektLavereATFLSN;
            return this;
        }

        public Builder medInntektLavereFlSn(boolean inntektLavereFLSN) {
            this.kladd.inntektLavereFLSN = inntektLavereFLSN;
            return this;
        }

        public SelvstendigNæringsdrivende build() {
            return this.kladd;
        }
    }
}
