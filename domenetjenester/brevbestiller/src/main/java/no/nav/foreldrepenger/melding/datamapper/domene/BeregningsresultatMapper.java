package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeMergerInnvilgelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public final class BeregningsresultatMapper {

    private static ObjectFactory objectFactory = new ObjectFactory();

    private BeregningsresultatMapper() {
    }

    public static BigInteger antallArbeidsgivere(BeregningsresultatFP beregningsresultat) {
        return BigInteger.valueOf(beregningsresultat.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(Collection::stream)
                .filter(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus()))
                .map(BeregningsresultatAndel::getArbeidsgiver)
                .flatMap(Optional::stream)
                .map(Arbeidsgiver::getNavn)
                .distinct()
                .count());

    }

    public static PeriodeListeType mapPeriodeListe(List<BeregningsresultatPeriode> beregningsresultatPerioder,
                                                   UttakResultatPerioder uttakResultatPerioder,
                                                   List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder) {
        PeriodeListeType periodeListe = objectFactory.createPeriodeListeType();
        List<PeriodeType> periodelisteFørSammenslåing = new ArrayList<>();
        List<UttakResultatPeriode> uttaksperioder = uttakResultatPerioder.getPerioder();
        List<UttakResultatPeriode> uttaksperioderMedÅrsak = new ArrayList<>(filtrerBortUkjentÅrsak(uttaksperioder));
        for (BeregningsresultatPeriode beregningsresultatPeriode : beregningsresultatPerioder) {
            UttakResultatPeriode matchetUttaksperiode = PeriodeBeregner.finnUttaksPeriode(beregningsresultatPeriode, uttaksperioder);
            if (matchetUttaksperiode.getPeriodeResultatÅrsak().erUkjent() || avslåttManglendeSøktUtenTrekkdager(matchetUttaksperiode)) {
                continue;
            }
            uttaksperioderMedÅrsak.remove(matchetUttaksperiode);
            periodelisteFørSammenslåing.add(mapEnkelPeriode(beregningsresultatPeriode,
                    matchetUttaksperiode,
                    PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder)
            ));
        }
        periodelisteFørSammenslåing.addAll(mapPerioderUtenBeregningsgrunnlag(uttaksperioderMedÅrsak));
        periodeListe.getPeriode().addAll(PeriodeMergerInnvilgelse.mergePerioder(periodelisteFørSammenslåing));
        return periodeListe;
    }

    private static boolean ingenTrekkdager(UttakResultatPeriode p) {
        return mapAntallTapteDagerFra(p.getAktiviteter()) == 0;
    }

    private static boolean avslåttManglendeSøktPeriode(UttakResultatPeriode matchetUttaksperiode) {
        return PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode().equals(matchetUttaksperiode.getPeriodeResultatÅrsak().getKode());
    }

    private static List<PeriodeType> mapPerioderUtenBeregningsgrunnlag(List<UttakResultatPeriode> perioderUtenBeregningsgrunnlag) {
        List<PeriodeType> perioder = new ArrayList<>();
        for (UttakResultatPeriode uttakperiode : perioderUtenBeregningsgrunnlag) {
            if (!avslåttManglendeSøktUtenTrekkdager(uttakperiode)) {
                perioder.add(mapEnkelUttaksperiode(uttakperiode));
            }
        }
        return perioder;
    }

    private static boolean avslåttManglendeSøktUtenTrekkdager(UttakResultatPeriode uttakperiode) {
        return avslåttManglendeSøktPeriode(uttakperiode) && ingenTrekkdager(uttakperiode);
    }

    private static List<UttakResultatPeriode> filtrerBortUkjentÅrsak(List<UttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
                .filter(Predicate.not(up -> up.getPeriodeResultatÅrsak().erUkjent()))
                .collect(Collectors.toList());
    }

    private static PeriodeType mapEnkelUttaksperiode(UttakResultatPeriode uttakperiode) {
        PeriodeType periode = objectFactory.createPeriodeType();
        periode.setAntallTapteDager(BigInteger.valueOf(mapAntallTapteDagerFra(uttakperiode.getAktiviteter())));
        periode.setInnvilget(uttakperiode.isInnvilget() && !erGraderingAvslått(uttakperiode));
        periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(uttakperiode.getFom()));
        periode.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(uttakperiode.getTom()));
        periode.setÅrsak(uttakperiode.getPeriodeResultatÅrsak().getKode());
        return periode;
    }

    private static PeriodeType mapEnkelPeriode(BeregningsresultatPeriode beregningsresultatPeriode,
                                               UttakResultatPeriode uttakResultatPeriode,
                                               BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        PeriodeType periode = objectFactory.createPeriodeType();
        periode.setAntallTapteDager(BigInteger.valueOf(mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter())));
        periode.setInnvilget(uttakResultatPeriode.isInnvilget() && !erGraderingAvslått(uttakResultatPeriode));
        PeriodeResultatÅrsak periodeResultatÅrsak = utledÅrsakskode(uttakResultatPeriode);
        if(uttakResultatPeriode.getFom().isBefore(beregningsresultatPeriode.getBeregningsresultatPeriodeFom())&& periodeResultatÅrsak.getKode().equals(PeriodeResultatÅrsak.HULL_MELLOM_FORELDRENES_PERIODER.getKode()) )
            periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(uttakResultatPeriode.getFom()));
        else
            periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
        periode.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeTom()));
        periode.setÅrsak(periodeResultatÅrsak.getKode());
        periode.setPeriodeDagsats(beregningsresultatPeriode.getDagsats());

        periode.setArbeidsforholdListe(AktivitetsMapper.mapArbeidsforholdliste(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode));
        periode.setNæringListe(AktivitetsMapper.mapNæringsliste(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode));
        periode.setAnnenAktivitetListe(AktivitetsMapper.mapAnnenAktivtetListe(beregningsresultatPeriode, uttakResultatPeriode));
        return periode;
    }


    private static int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ?
                uttakAktiviteter.stream()
                        .map(UttakResultatPeriodeAktivitet::getTrekkdager)
                        .filter(Objects::nonNull)
                        .mapToInt(BigDecimal::intValue)
                        .max()
                        .orElse(0) : 0;
    }


    private static PeriodeResultatÅrsak utledÅrsakskode(UttakResultatPeriode uttakPeriode) {
        if (erGraderingAvslått(uttakPeriode) && erInnvilget(uttakPeriode)) {
            return uttakPeriode.getGraderingAvslagÅrsak();
        } else if (uttakPeriode.getPeriodeResultatÅrsak() != null) {
            return uttakPeriode.getPeriodeResultatÅrsak();
        }
        return PeriodeResultatÅrsak.UKJENT;
    }

    private static boolean erInnvilget(UttakResultatPeriode uttakPeriode) {
        return PeriodeResultatType.INNVILGET.equals(uttakPeriode.getPeriodeResultatType());
    }

    private static boolean erGraderingAvslått(UttakResultatPeriode uttakPeriode) {
        return !uttakPeriode.erGraderingInnvilget() && !uttakPeriode.getGraderingAvslagÅrsak().erUkjent();
    }

    public static long finnTotalBrukerAndel(BeregningsresultatFP beregningsresultatFP) {
        return harBrukerAndel(beregningsresultatFP) ? 1 : 0; // Dette er i praksis en boolean, så holder med å bruke 0 og 1
    }

    private static boolean harBrukerAndel(BeregningsresultatFP beregningsresultatFP) {
        return beregningsresultatFP.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(List::stream)
                .anyMatch(BeregningsresultatAndel::erBrukerMottaker);
    }

    public static long finnTotalArbeidsgiverAndel(BeregningsresultatFP beregningsresultatFP) {
        return harArbeidsgiverAndel(beregningsresultatFP) ? 1 : 0; // Dette er i praksis en boolean, så holder med å bruke 0 og 1
    }

    private static boolean harArbeidsgiverAndel(BeregningsresultatFP beregningsresultatFP) {
        return beregningsresultatFP.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(List::stream)
                .anyMatch(BeregningsresultatAndel::erArbeidsgiverMottaker);
    }

    public static BigInteger tellAntallAvslag(PeriodeListeType periodeListe) {
        return BigInteger.valueOf(periodeListe.getPeriode().stream()
                .filter(Predicate.not(PeriodeType::isInnvilget))
                //Skal ikke opplyse søker om tapt fpff siden bruker ikke har søkt om det
                .filter(p -> !PeriodeResultatÅrsak.MOR_TAR_IKKE_ALLE_UKENE.getKode().equals(p.getÅrsak()))
                .count());
    }

    public static BigInteger tellAntallInnvilget(PeriodeListeType periodeListe) {
        return BigInteger.valueOf(periodeListe.getPeriode().stream()
                .filter(PeriodeType::isInnvilget)
                .count());
    }

    public static Optional<XMLGregorianCalendar> finnStønadsperiodeFomHvisFinnes(PeriodeListeType periodeListe) {
        return periodeListe.getPeriode().stream()
                .filter(p -> Boolean.TRUE.equals(p.isInnvilget()))
                .map(PeriodeType::getPeriodeFom)
                .min(XMLGregorianCalendar::compare);
    }


    public static Optional<XMLGregorianCalendar> finnStønadsperiodeTomHvisFinnes(PeriodeListeType periodeListe) {
        return periodeListe.getPeriode().stream()
                .filter(p -> Boolean.TRUE.equals(p.isInnvilget()))
                .map(PeriodeType::getPeriodeTom)
                .max(XMLGregorianCalendar::compare);
    }

    public static long finnDagsats(BeregningsresultatFP beregningsresultat) {
        return finnFørsteInnvilgedePeriode(beregningsresultat).map(BeregningsresultatPeriode::getDagsats).orElse(0L);
    }

    private static Optional<BeregningsresultatPeriode> finnFørsteInnvilgedePeriode(BeregningsresultatFP beregningsresultat) {
        return beregningsresultat.getBeregningsresultatPerioder()
                .stream()
                .filter(harDagsatsOverNull())
                .min(Comparator.comparing(BeregningsresultatPeriode::getBeregningsresultatPeriodeFom));
    }

    private static Predicate<BeregningsresultatPeriode> harDagsatsOverNull() {
        return beregningsresultatPeriode -> beregningsresultatPeriode.getDagsats() != null && beregningsresultatPeriode.getDagsats() > 0;
    }

    public static long finnMånedsbeløp(BeregningsresultatFP beregningsresultat) {
        return finnFørsteInnvilgedePeriode(beregningsresultat).map(BeregningsresultatMapper::getMånedsbeløp).orElse(0L);
    }

    private static long getMånedsbeløp(BeregningsresultatPeriode førstePeriode) {
        return førstePeriode.getDagsats() * 260 / 12;
    }
}
