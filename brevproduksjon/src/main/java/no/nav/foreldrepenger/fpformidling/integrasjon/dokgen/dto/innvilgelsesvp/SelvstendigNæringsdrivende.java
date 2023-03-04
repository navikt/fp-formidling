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
    private boolean inntektLavere_AT_SN;
    private boolean inntektLavere_AT_FL_SN;
    private boolean inntektLavere_FL_SN;

    public boolean getNyoppstartet() {
        return nyoppstartet;
    }

    public Beløp getÅrsinntekt() {
        return årsinntekt;
    }

    public int getSistLignedeÅr() {
        return sistLignedeÅr;
    }

    public boolean getInntektLavere_AT_SN() {
        return inntektLavere_AT_SN;
    }

    public boolean getInntektLavere_AT_FL_SN() {
        return inntektLavere_AT_FL_SN;
    }

    public boolean getInntektLavere_FL_SN() {
        return inntektLavere_FL_SN;
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
            that.sistLignedeÅr) && Objects.equals(inntektLavere_AT_SN, that.inntektLavere_AT_SN) && Objects.equals(inntektLavere_AT_FL_SN,
            that.inntektLavere_AT_FL_SN) && Objects.equals(inntektLavere_FL_SN, that.inntektLavere_FL_SN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nyoppstartet, årsinntekt, sistLignedeÅr, inntektLavere_AT_SN, inntektLavere_AT_FL_SN, inntektLavere_FL_SN);
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
                this.kladd.inntektLavere_AT_SN = eksisterende.getInntektLavere_AT_SN();
                this.kladd.inntektLavere_AT_FL_SN = eksisterende.getInntektLavere_AT_FL_SN();
                this.kladd.inntektLavere_FL_SN = eksisterende.getInntektLavere_FL_SN();
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
            this.kladd.inntektLavere_AT_SN = inntektLavereATSN;
            return this;
        }

        public Builder medInntektLavereAtFlSn(boolean inntektLavereATFLSN) {
            this.kladd.inntektLavere_AT_FL_SN = inntektLavereATFLSN;
            return this;
        }

        public Builder medInntektLavereFlSn(boolean inntektLavereFLSN) {
            this.kladd.inntektLavere_FL_SN = inntektLavereFLSN;
            return this;
        }

        public SelvstendigNæringsdrivende build() {
            return this.kladd;
        }
    }
}
