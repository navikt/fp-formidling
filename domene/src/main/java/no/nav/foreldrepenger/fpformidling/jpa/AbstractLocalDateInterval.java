package no.nav.foreldrepenger.fpformidling.jpa;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import no.nav.vedtak.konfig.Tid;

/**
 * Basis klasse for modellere et dato interval.
 */
public abstract class AbstractLocalDateInterval implements Comparable<AbstractLocalDateInterval>, Serializable {

    private static final LocalDate TIDENES_BEGYNNELSE = Tid.TIDENES_BEGYNNELSE;
    public static final LocalDate TIDENES_ENDE = Tid.TIDENES_ENDE;

    static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public abstract LocalDate getFomDato();

    public abstract LocalDate getTomDato();

    protected static LocalDate finnTomDato(LocalDate fom, int antallArbeidsdager) {
        if (antallArbeidsdager < 1) {
            throw new IllegalArgumentException("Antall arbeidsdager må være 1 eller større.");
        }
        var tom = fom;
        var antallArbeidsdagerTmp = antallArbeidsdager;

        while (antallArbeidsdagerTmp > 0) {
            if (antallArbeidsdagerTmp > antallArbeidsdager) {
                throw new IllegalArgumentException("Antall arbeidsdager beregnes feil.");
            }
            if (erArbeidsdag(tom)) {
                antallArbeidsdagerTmp--;
            }
            if (antallArbeidsdagerTmp > 0) {
                tom = tom.plusDays(1);
            }
        }
        return tom;
    }

    protected static LocalDate finnFomDato(LocalDate tom, int antallArbeidsdager) {
        if (antallArbeidsdager < 1) {
            throw new IllegalArgumentException("Antall arbeidsdager må være 1 eller større.");
        }
        var fom = tom;
        var antallArbeidsdagerTmp = antallArbeidsdager;

        while (antallArbeidsdagerTmp > 0) {
            if (antallArbeidsdagerTmp > antallArbeidsdager) {
                throw new IllegalArgumentException("Antall arbeidsdager beregnes feil.");
            }
            if (erArbeidsdag(fom)) {
                antallArbeidsdagerTmp--;
            }
            if (antallArbeidsdagerTmp > 0) {
                fom = fom.minusDays(1);
            }
        }
        return fom;
    }

    public boolean erFørEllerLikPeriodeslutt(ChronoLocalDate dato) {
        return getTomDato() == null || getTomDato().isAfter(dato) || getTomDato().isEqual(dato);
    }

    public boolean erEtterEllerLikPeriodestart(ChronoLocalDate dato) {
        return getFomDato().isBefore(dato) || getFomDato().isEqual(dato);
    }

    public boolean inkluderer(ChronoLocalDate dato) {
        return erEtterEllerLikPeriodestart(dato) && erFørEllerLikPeriodeslutt(dato);
    }

    public static LocalDate forrigeArbeidsdag(LocalDate dato) {
        if (dato == TIDENES_BEGYNNELSE || dato == TIDENES_ENDE) {
            return dato;
        }

        return switch (dato.getDayOfWeek()) {
            case SATURDAY -> dato.minusDays(1);
            case SUNDAY -> dato.minusDays(2);
            default -> dato;
        };
    }

    public static LocalDate nesteArbeidsdag(LocalDate dato) {
        if (dato == TIDENES_BEGYNNELSE || dato == TIDENES_ENDE) {
            return dato;
        }

        return switch (dato.getDayOfWeek()) {
            case SATURDAY -> dato.plusDays(2);
            case SUNDAY -> dato.plusDays(1);
            default -> dato;
        };
    }

    protected static boolean erArbeidsdag(LocalDate dato) {
        return !dato.getDayOfWeek().equals(SATURDAY) && !dato.getDayOfWeek().equals(SUNDAY); // NOSONAR
    }

    @Override
    public int compareTo(AbstractLocalDateInterval periode) {
        return getFomDato().compareTo(periode.getFomDato());
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof AbstractLocalDateInterval)) {
            return false;
        }
        var annen = (AbstractLocalDateInterval) object;
        return likFom(annen) && likTom(annen);
    }

    private boolean likFom(AbstractLocalDateInterval annen) {
        var likFom = Objects.equals(this.getFomDato(), annen.getFomDato());
        if (this.getFomDato() == null || annen.getFomDato() == null) {
            return likFom;
        }
        return likFom || Objects.equals(nesteArbeidsdag(this.getFomDato()), nesteArbeidsdag(annen.getFomDato()));
    }

    private boolean likTom(AbstractLocalDateInterval annen) {
        var likTom = Objects.equals(getTomDato(), annen.getTomDato());
        if (this.getTomDato() == null || annen.getTomDato() == null) {
            return likTom;
        }
        return likTom || Objects.equals(forrigeArbeidsdag(this.getTomDato()), forrigeArbeidsdag(annen.getTomDato()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFomDato(), getTomDato());
    }

    @Override
    public String toString() {
        return String.format("Periode: %s - %s", getFomDato().format(FORMATTER), getTomDato().format(FORMATTER));
    }
}
