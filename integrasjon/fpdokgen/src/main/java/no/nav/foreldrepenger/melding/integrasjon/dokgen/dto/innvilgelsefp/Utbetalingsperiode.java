package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp;

import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;

import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;

@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public class Utbetalingsperiode {
    private boolean innvilget;
    private Årsak årsak;
    @JsonIgnore
    private StønadskontoType stønadskontoType;
    private String periodeFom;
    @JsonIgnore
    private LocalDate periodeFomDate;
    private String periodeTom;
    @JsonIgnore
    private LocalDate periodeTomDate;
    private long periodeDagsats;
    private int antallTapteDager;
    private Prosent prioritertUtbetalingsgrad = Prosent.NULL;
    private List<Arbeidsforhold> arbeidsforholdsliste = new ArrayList<>();
    private Næring næring;
    private List<AnnenAktivitet> annenAktivitetsliste = new ArrayList<>();
    @JsonIgnore
    private Språkkode språkkode;

    public boolean isInnvilget() {
        return innvilget;
    }

    public boolean isAvslått() {
        return !innvilget;
    }

    public Årsak getÅrsak() {
        return årsak;
    }

    public StønadskontoType getStønadskontoType() {
        return stønadskontoType;
    }
    public LocalDate getPeriodeFom() {
        return periodeFomDate;
    }

    public LocalDate getPeriodeTom() {
        return periodeTomDate;
    }

    public long getPeriodeDagsats() {
        return periodeDagsats;
    }

    public int getAntallTapteDager() {
        return antallTapteDager;
    }

    public Prosent getPrioritertUtbetalingsgrad() {
        return prioritertUtbetalingsgrad;
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

    public Språkkode getSpråkkode() {
        return språkkode;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        var that = (Utbetalingsperiode) object;
        return Objects.equals(innvilget, that.innvilget)
                && Objects.equals(årsak, that.årsak)
                && Objects.equals(periodeFom, that.periodeFom)
                && Objects.equals(periodeTom, that.periodeTom)
                && Objects.equals(periodeDagsats, that.periodeDagsats)
                && Objects.equals(antallTapteDager, that.antallTapteDager)
                && Objects.equals(prioritertUtbetalingsgrad, that.prioritertUtbetalingsgrad)
                && Objects.equals(arbeidsforholdsliste, that.arbeidsforholdsliste)
                && Objects.equals(næring, that.næring)
                && Objects.equals(annenAktivitetsliste, that.annenAktivitetsliste);
    }

    @Override
    public int hashCode() {
        return Objects.hash(innvilget, årsak, periodeFom, periodeTom, periodeDagsats, antallTapteDager,
                prioritertUtbetalingsgrad, arbeidsforholdsliste, næring, annenAktivitetsliste);
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

        public Builder medÅrsak(Årsak årsak) {
            this.kladd.årsak = årsak;
            return this;
        }

        public Builder medStønadskontoType(StønadskontoType stønadskontoType) {
            this.kladd.stønadskontoType = stønadskontoType;
            return this;
        }

        public Builder medPeriodeFom(LocalDate periodeFom, Språkkode språkkode) {
            this.kladd.periodeFom = formaterDato(periodeFom, språkkode);
            this.kladd.periodeFomDate = periodeFom;
            this.kladd.språkkode = språkkode;
            return this;
        }

        public Builder medPeriodeTom(LocalDate periodeTom, Språkkode språkkode) {
            this.kladd.periodeTom = formaterDato(periodeTom, språkkode);
            this.kladd.periodeTomDate = periodeTom;
            this.kladd.språkkode = språkkode;
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

        public Builder medPrioritertUtbetalingsgrad(Prosent prioritertUtbetalingsgrad) {
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
