package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Utbetalingsperiode {
    private boolean innvilget;
    private String årsak;
    private String periodeFom;
    private LocalDate periodeFomDate;
    private String periodeTom;
    private LocalDate periodeTomDate;
    private long periodeDagsats;
    private int antallTapteDager;
    private int prioritertUtbetalingsgrad;
    private List<Arbeidsforhold> arbeidsforholdsliste = new ArrayList<>();
    private Næring næring;
    private List<AnnenAktivitet> annenAktivitetsliste = new ArrayList<>();

    public boolean isInnvilget() {
        return innvilget;
    }

    public String getÅrsak() {
        return årsak;
    }

    public LocalDate getPeriodeFom() {
        return periodeFomDate;
    }

    public LocalDate getPeriodeTom() {
        return periodeTomDate;
    }

    public void setPeriodeTom(LocalDate periodeTom) {
        this.periodeTom = formaterDatoNorsk(periodeTom);
        this.periodeTomDate = periodeTom;
    }

    public long getPeriodeDagsats() {
        return periodeDagsats;
    }

    public int getAntallTapteDager() {
        return antallTapteDager;
    }

    public void setAntallTapteDager(int antallTapteDager) {
        this.antallTapteDager = antallTapteDager;
    }

    public List<Arbeidsforhold> getArbeidsforholdsliste() {
        return arbeidsforholdsliste;
    }

    public Næring getNæring() {
        return næring;
    }

    public List<AnnenAktivitet> getAnnenAktivitetsliste() {
        return annenAktivitetsliste;
    }

    public int getPrioritertUtbetalingsgrad() {
        return prioritertUtbetalingsgrad;
    }

    public static Builder ny() {
        return new Builder();
    }

    public static class Builder {
        private Utbetalingsperiode kladd;

        private Builder() {
            this.kladd = new Utbetalingsperiode();
        }

        public Builder medInnvilget(Boolean innvilget) {
            if (innvilget != null) {
                this.kladd.innvilget = innvilget;
            }
            return this;
        }

        public Builder medÅrsak(String årsak) {
            this.kladd.årsak = årsak;
            return this;
        }

        public Builder medPeriodeFom(LocalDate periodeFom) {
            this.kladd.periodeFom = formaterDatoNorsk(periodeFom);
            this.kladd.periodeFomDate = periodeFom;
            return this;
        }

        public Builder medPeriodeTom(LocalDate periodeTom) {
            this.kladd.periodeTom = formaterDatoNorsk(periodeTom);
            this.kladd.periodeTomDate = periodeTom;
            return this;
        }

        public Builder medPeriodeDagsats(long periodeDagsats) {
            this.kladd.periodeDagsats = periodeDagsats;
            return this;
        }

        public Builder medAntallTapteDager(int antallTapteDager) {
            this.kladd.antallTapteDager = antallTapteDager;
            return this;
        }

        public Builder medPrioritertUtbetalingsgrad(int prioritertUtbetalingsgrad) {
            this.kladd.prioritertUtbetalingsgrad = prioritertUtbetalingsgrad;
            return this;
        }

        public Builder medArbeidsforhold(List<Arbeidsforhold> arbeidsfoholdListe) {
            this.kladd.arbeidsforholdsliste = arbeidsfoholdListe;
            return this;
        }

        public Builder medNæring(Næring næring) {
            this.kladd.næring = næring;
            return this;
        }

        public Builder medAnnenAktivitet(List<AnnenAktivitet> annenAktivitetsListe) {
            this.kladd.annenAktivitetsliste = annenAktivitetsListe;
            return this;
        }

        public Utbetalingsperiode build() {
            return this.kladd;
        }
    }
}
