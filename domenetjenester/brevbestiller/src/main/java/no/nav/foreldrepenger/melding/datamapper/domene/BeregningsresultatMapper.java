package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;
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
import no.nav.foreldrepenger.melding.uttak.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

@ApplicationScoped
public class BeregningsresultatMapper {

    private static ObjectFactory objectFactory = new ObjectFactory();

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
        List<UttakResultatPeriode> ikkeMatchedePerioder = new ArrayList<>(plukkIkkeUkjentePerioder(uttaksperioder));
        for (BeregningsresultatPeriode beregningsresultatPeriode : beregningsresultatPerioder) {
            UttakResultatPeriode matchetUttaksperiode = PeriodeBeregner.finnUttaksPeriode(beregningsresultatPeriode, uttaksperioder);
            if (PeriodeResultatÅrsak.UKJENT.equals(matchetUttaksperiode.getPeriodeResultatÅrsak())) {
                continue;
            }
            ikkeMatchedePerioder.remove(matchetUttaksperiode);
            periodelisteFørSammenslåing.add(mapEnkelPeriode(beregningsresultatPeriode,
                    matchetUttaksperiode,
                    PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder)
            ));
        }
        periodelisteFørSammenslåing.addAll(mapAvslåttePerioder(ikkeMatchedePerioder));
        periodeListe.getPeriode().addAll(PeriodeMergerInnvilgelse.mergePerioder(periodelisteFørSammenslåing));
        return periodeListe;
    }

    private static List<PeriodeType> mapAvslåttePerioder(List<UttakResultatPeriode> ikkeMatchedePerioder) {
        List<PeriodeType> avslåttePerioder = new ArrayList<>();
        for (UttakResultatPeriode uttakperiode : ikkeMatchedePerioder) {
            avslåttePerioder.add(mapEnkelUttaksperiode(uttakperiode));
        }
        return avslåttePerioder;
    }

    private static List<UttakResultatPeriode> plukkIkkeUkjentePerioder(List<UttakResultatPeriode> uttaksperioder) {
        return uttaksperioder.stream()
                .filter(Predicate.not(up -> PeriodeResultatÅrsak.UKJENT.equals(up.getPeriodeResultatÅrsak())))
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

    static PeriodeType mapEnkelPeriode(BeregningsresultatPeriode beregningsresultatPeriode,
                                       UttakResultatPeriode uttakResultatPeriode,
                                       BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        PeriodeType periode = objectFactory.createPeriodeType();
        periode.setAntallTapteDager(BigInteger.valueOf(mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter())));
        periode.setInnvilget(uttakResultatPeriode.isInnvilget() && !erGraderingAvslått(uttakResultatPeriode));
        ÅrsakskodeMedLovreferanse årsakskodeMedLovreferanse = utledÅrsakskode(uttakResultatPeriode);
        periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
        periode.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeTom()));
        periode.setÅrsak(årsakskodeMedLovreferanse.getKode());
        periode.setPeriodeDagsats(beregningsresultatPeriode.getDagsats());

        periode.setArbeidsforholdListe(AktivtetsMapper.mapArbeidsforholdliste(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode));
        periode.setNæringListe(AktivtetsMapper.mapNæringsliste(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode));
        periode.setAnnenAktivitetListe(AktivtetsMapper.mapAnnenAktivtetListe(beregningsresultatPeriode, uttakResultatPeriode));
        return periode;
    }


    private static int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ?
                uttakAktiviteter.stream()
                        .map(UttakResultatPeriodeAktivitet::getTrekkdager)
                        .mapToInt(BigDecimal::intValue)
                        .max()
                        .orElse(0) : 0;
    }


    private static ÅrsakskodeMedLovreferanse utledÅrsakskode(UttakResultatPeriode uttakPeriode) {
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
        return !uttakPeriode.erGraderingInnvilget() && erGraderingÅrsakKjent(uttakPeriode.getGraderingAvslagÅrsak());
    }

    private static boolean erGraderingÅrsakKjent(GraderingAvslagÅrsak årsak) {
        return årsak != null && !årsak.equals(GraderingAvslagÅrsak.UKJENT);
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
