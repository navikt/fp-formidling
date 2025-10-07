package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BeregningsgrunnlagAndel {
    private String aktivitetStatus;
    private String arbeidsgiverNavn;
    private long dagsats;
    private long månedsinntekt;
    private long årsinntekt;
    private boolean etterlønnSluttpakke;
    private int sistLignedeÅr;

    public String getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public long getDagsats() {
        return dagsats;
    }

    public long getMånedsinntekt() {
        return månedsinntekt;
    }

    public long getÅrsinntekt() {
        return årsinntekt;
    }

    public int getSistLignedeÅr() {
        return sistLignedeÅr;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        var that = (BeregningsgrunnlagAndel) object;
        return Objects.equals(aktivitetStatus, that.aktivitetStatus) && Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn) && Objects.equals(
            dagsats, that.dagsats) && Objects.equals(månedsinntekt, that.månedsinntekt) && Objects.equals(årsinntekt, that.årsinntekt)
            && Objects.equals(etterlønnSluttpakke, that.etterlønnSluttpakke) && Objects.equals(sistLignedeÅr, that.sistLignedeÅr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetStatus, arbeidsgiverNavn, dagsats, månedsinntekt, årsinntekt, etterlønnSluttpakke, sistLignedeÅr);
    }

    public static Builder ny() {
        return new Builder();
    }

    public void setDagsats(long dagsats) {
        this.dagsats = dagsats;
    }

    public static class Builder {
        private BeregningsgrunnlagAndel kladd;

        private Builder() {
            this.kladd = new BeregningsgrunnlagAndel();
        }

        public Builder medAktivitetStatus(String aktivitetStatus) {
            this.kladd.aktivitetStatus = aktivitetStatus;
            return this;
        }

        public Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.kladd.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        public Builder medDagsats(long dagsats) {
            this.kladd.dagsats = dagsats;
            return this;
        }

        public Builder medMånedsinntekt(long månedsinntekt) {
            this.kladd.månedsinntekt = månedsinntekt;
            return this;
        }

        public Builder medÅrsinntekt(long årsinntekt) {
            this.kladd.årsinntekt = årsinntekt;
            return this;
        }

        public Builder medEtterlønnSluttpakke(boolean etterlønnSluttpakke) {
            this.kladd.etterlønnSluttpakke = etterlønnSluttpakke;
            return this;
        }

        public Builder medSistLignedeÅr(int sistLignedeÅr) {
            this.kladd.sistLignedeÅr = sistLignedeÅr;
            return this;
        }

        public BeregningsgrunnlagAndel build() {
            return this.kladd;
        }
    }
}
