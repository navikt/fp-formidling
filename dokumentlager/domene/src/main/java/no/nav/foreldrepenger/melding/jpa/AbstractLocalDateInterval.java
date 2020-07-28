package no.nav.foreldrepenger.melding.jpa;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static java.time.temporal.ChronoUnit.DAYS;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.threeten.extra.Interval;

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

    protected abstract AbstractLocalDateInterval lagNyPeriode(LocalDate fomDato, LocalDate tomDato);

    protected static LocalDate finnTomDato(LocalDate fom, int antallArbeidsdager) {
        if (antallArbeidsdager < 1) {
            throw new IllegalArgumentException("Antall arbeidsdager må være 1 eller større.");
        }
        LocalDate tom = fom;
        int antallArbeidsdagerTmp = antallArbeidsdager;

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
        LocalDate fom = tom;
        int antallArbeidsdagerTmp = antallArbeidsdager;

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

    public Interval tilIntervall() {
        return getIntervall(getFomDato(), getTomDato());
    }

    private static Interval getIntervall(LocalDate fomDato, LocalDate tomDato) {
        LocalDateTime døgnstart = TIDENES_ENDE.equals(tomDato) ? tomDato.atStartOfDay() : tomDato.atStartOfDay().plusDays(1);
        return Interval.of(
                fomDato.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant(),
                døgnstart.atZone(ZoneId.systemDefault()).toInstant());
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

    public boolean inkludererArbeidsdag(LocalDate dato) {
        return erEtterEllerLikPeriodestart(nesteArbeidsdag(dato)) && erFørEllerLikPeriodeslutt(forrigeArbeidsdag(dato));
    }

    public static LocalDate forrigeArbeidsdag(LocalDate dato) {
        if (dato != TIDENES_BEGYNNELSE && dato != TIDENES_ENDE) {
            switch (dato.getDayOfWeek()) {
                case SATURDAY:
                    return dato.minusDays(1);
                case SUNDAY:
                    return dato.minusDays(2);
                default:
                    break;
            }
        }
        return dato;
    }

    public static LocalDate nesteArbeidsdag(LocalDate dato) {
        if (dato != TIDENES_BEGYNNELSE && dato != TIDENES_ENDE) {
            switch (dato.getDayOfWeek()) {
                case SATURDAY:
                    return dato.plusDays(2);
                case SUNDAY:
                    return dato.plusDays(1);
                default:
                    break;
            }
        }
        return dato;
    }

    public long antallDager() {
        return DAYS.between(getFomDato(), getTomDato());
    }

    public boolean overlapper(AbstractLocalDateInterval periode) {
        return tilIntervall().overlaps(getIntervall(periode.getFomDato(), periode.getTomDato()));
    }

    public int antallArbeidsdager() {
        if (getTomDato().isEqual(TIDENES_ENDE)) {
            throw new IllegalStateException("Både fra og med og til og med dato må være satt for å regne ut arbeidsdager.");
        }
        return arbeidsdager().size();
    }

    public int maksAntallArbeidsdager() {
        if (getTomDato().isEqual(TIDENES_ENDE)) {
            throw new IllegalStateException("Både fra og med og til og med dato må være satt for å regne ut arbeidsdager.");
        }

        LocalDate månedsstart = getFomDato().minusDays(getFomDato().getDayOfMonth() - 1L);
        LocalDate månedsslutt = getTomDato().minusDays(getTomDato().getDayOfMonth() - 1L).plusDays(getTomDato().lengthOfMonth() - 1L);
        return listArbeidsdager(månedsstart, månedsslutt).size();
    }

    public List<LocalDate> arbeidsdager() {
        return listArbeidsdager(getFomDato(), getTomDato());
    }

    private static List<LocalDate> listArbeidsdager(LocalDate fomDato, LocalDate tomDato) { // NOSONAR
        List<LocalDate> arbeidsdager = new ArrayList<>();
        LocalDate dato = fomDato;
        while (!dato.isAfter(tomDato)) {
            if (erArbeidsdag(dato)) {
                arbeidsdager.add(dato);
            }
            dato = dato.plusDays(1L);
        }
        return arbeidsdager;
    }

    protected static boolean erArbeidsdag(LocalDate dato) {
        return !dato.getDayOfWeek().equals(SATURDAY) && !dato.getDayOfWeek().equals(SUNDAY); // NOSONAR
    }

    public boolean grenserTil(AbstractLocalDateInterval periode2) {
        return getTomDato().equals(periode2.getFomDato().minusDays(1)) || periode2.getTomDato().equals(getFomDato().minusDays(1));
    }

    public List<AbstractLocalDateInterval> splittVedMånedsgrenser() {
        List<AbstractLocalDateInterval> perioder = new ArrayList<>();

        LocalDate dato = getFomDato().minusDays(getFomDato().getDayOfMonth() - 1L);
        LocalDate periodeFomDato = getFomDato();

        while (dato.isBefore(getTomDato())) {
            int dagerIMåned = dato.lengthOfMonth();
            LocalDate sisteDagIMåneden = dato.plusDays(dagerIMåned - 1L);
            boolean harMånedsslutt = inkluderer(sisteDagIMåneden);
            if (harMånedsslutt) {
                perioder.add(lagNyPeriode(periodeFomDato, sisteDagIMåneden));
                dato = sisteDagIMåneden.plusDays(1);
                periodeFomDato = dato;
            } else {
                perioder.add(lagNyPeriode(periodeFomDato, getTomDato()));
                dato = getTomDato();
            }
        }

        return perioder;
    }

    public double finnMånedeskvantum() {
        Collection<AbstractLocalDateInterval> perioder = splittVedMånedsgrenser();

        double kvantum = 0d;
        for (AbstractLocalDateInterval periode : perioder) {
            int antallArbeidsdager = periode.antallArbeidsdager();
            if (antallArbeidsdager != 0) {
                int diff = periode.maksAntallArbeidsdager() - antallArbeidsdager;
                kvantum += diff == 0 ? 1 : (double) diff / (double) periode.maksAntallArbeidsdager();
            }
        }

        return kvantum;
    }

    public List<AbstractLocalDateInterval> splittPeriodePåDatoer(LocalDate... datoer) {
        List<LocalDate> datoListe = Arrays.asList(datoer);
        Collections.sort(datoListe);
        List<AbstractLocalDateInterval> perioder = new ArrayList<>();
        AbstractLocalDateInterval periode = this;
        for (LocalDate dato : datoListe) {
            if (periode.inkluderer(dato) && dato.isAfter(periode.getFomDato())) {
                perioder.add(lagNyPeriode(periode.getFomDato(), dato.minusDays(1)));
                periode = lagNyPeriode(dato, periode.getTomDato());
            }
        }

        perioder.add(periode);

        return perioder;
    }

    public List<AbstractLocalDateInterval> splittPeriodePåDatoerAvgrensTilArbeidsdager(LocalDate... datoer) {
        List<LocalDate> datoListe = Arrays.asList(datoer);
        Collections.sort(datoListe);
        List<AbstractLocalDateInterval> perioder = new ArrayList<>();
        AbstractLocalDateInterval periode = this.avgrensTilArbeidsdager();
        for (LocalDate dato : datoListe) {
            if (periode.inkluderer(dato) && dato.isAfter(periode.getFomDato())) {
                perioder.add(lagNyPeriode(periode.getFomDato(), dato.minusDays(1)).avgrensTilArbeidsdager());
                periode = lagNyPeriode(dato, periode.getTomDato()).avgrensTilArbeidsdager();
            }
        }

        perioder.add(periode);

        return perioder;
    }

    public AbstractLocalDateInterval avgrensTilArbeidsdager() {
        LocalDate nyFomDato = nesteArbeidsdag(getFomDato());
        LocalDate nyTomDato = forrigeArbeidsdag(getTomDato());
        if (nyFomDato.equals(getFomDato()) && nyTomDato.equals(getTomDato())) {
            return this;
        }
        return lagNyPeriode(nyFomDato, nyTomDato);
    }

    public AbstractLocalDateInterval kuttPeriodePåGrenseneTil(AbstractLocalDateInterval periode) {
        LocalDate nyFomDato = getFomDato().isBefore(periode.getFomDato()) ? periode.getFomDato() : getFomDato();
        LocalDate nyTomDato = getTomDato().isAfter(periode.getTomDato()) ? periode.getTomDato() : getTomDato();
        return lagNyPeriode(nyFomDato, nyTomDato);
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
        AbstractLocalDateInterval annen = (AbstractLocalDateInterval) object;
        return likFom(annen) && likTom(annen);
    }

    private boolean likFom(AbstractLocalDateInterval annen) {
        boolean likFom = Objects.equals(this.getFomDato(), annen.getFomDato());
        if (this.getFomDato() == null || annen.getFomDato() == null) {
            return likFom;
        }
        return likFom
                || Objects.equals(nesteArbeidsdag(this.getFomDato()), nesteArbeidsdag(annen.getFomDato()));
    }

    private boolean likTom(AbstractLocalDateInterval annen) {
        boolean likTom = Objects.equals(getTomDato(), annen.getTomDato());
        if (this.getTomDato() == null || annen.getTomDato() == null) {
            return likTom;
        }
        return likTom
                || Objects.equals(forrigeArbeidsdag(this.getTomDato()), forrigeArbeidsdag(annen.getTomDato()));
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
