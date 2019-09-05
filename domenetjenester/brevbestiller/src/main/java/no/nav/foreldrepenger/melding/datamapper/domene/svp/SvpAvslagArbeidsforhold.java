package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import java.util.Objects;

import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;

final class SvpAvslagArbeidsforhold {

    private int aarsakskode;
    private String arbeidsgiverNavn;
    private Boolean erFL;
    private Boolean erSN;

    private SvpAvslagArbeidsforhold() {
        // Skjul default constructor
    }

    private SvpAvslagArbeidsforhold(SvpAvslagArbeidsforhold.Builder builder) {
        this.aarsakskode = builder.aarsakskode;
        this.arbeidsgiverNavn = builder.arbeidsgiverNavn;
        this.erFL = builder.erFL;
        this.erSN = builder.erSN;
    }

    @SuppressWarnings("WeakerAccess") // Handlebar trenger public getter
    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    @SuppressWarnings("WeakerAccess") // Handlebar trenger public getter
    public Integer getAarsakskode() {
        return aarsakskode;
    }

    @SuppressWarnings("WeakerAccess") // Handlebar trenger public getter
    public Boolean getErFL() {
        return erFL;
    }

    @SuppressWarnings("WeakerAccess") // Handlebar trenger public getter
    public Boolean getErSN() {
        return erSN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SvpAvslagArbeidsforhold that = (SvpAvslagArbeidsforhold) o;
        return aarsakskode == that.aarsakskode &&
                Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn) &&
                Objects.equals(erFL, that.erFL) &&
                Objects.equals(erSN, that.erSN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aarsakskode, arbeidsgiverNavn, erFL, erSN);
    }

    @Override
    public String toString() {
        return "SvpAvslagArbeidsforhold{" +
                "aarsakskode=" + aarsakskode +
                ", arbeidsgiverNavn='" + arbeidsgiverNavn + '\'' +
                ", erFL=" + erFL +
                ", erSN=" + erSN +
                '}';
    }

    public static final class Builder {

        private int aarsakskode;
        private String arbeidsgiverNavn;
        private Boolean erFL;
        private Boolean erSN;

        private Builder() {
            // Skjul default constructor
        }

        public static Builder ny() {
            return new Builder();
        }

        Builder medAarsakskode(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak) {
            if (!ArbeidsforholdIkkeOppfyltÅrsak.INGEN.equals(arbeidsforholdIkkeOppfyltÅrsak)) {
                this.aarsakskode = Integer.valueOf(arbeidsforholdIkkeOppfyltÅrsak.getKode());
            }
            return this;
        }

        Builder medArbeidsgiverNavn(String arbeidsgiverNavn) {
            this.arbeidsgiverNavn = arbeidsgiverNavn;
            return this;
        }

        Builder medErFL(Boolean erFL) {
            this.erFL = erFL;
            return this;
        }

        Builder medErSN(Boolean erSN) {
            this.erSN = erSN;
            return this;
        }

        public SvpAvslagArbeidsforhold build() {
            return new SvpAvslagArbeidsforhold(this);
        }
        
    }

}
