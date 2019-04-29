package no.nav.foreldrepenger.melding.datamapper.domene;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.UtbetaltKode;
import no.nav.foreldrepenger.melding.uttak.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.melding.uttak.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.UttakUtsettelseType;

@ApplicationScoped
public class UttakMapper {

    public UttakMapper() {
        //CDI
    }

    public static final IkkeOppfyltÅrsak FAR_HAR_IKKE_OMSORG = new IkkeOppfyltÅrsak("4012");
    public static final IkkeOppfyltÅrsak MOR_HAR_IKKE_OMSORG = new IkkeOppfyltÅrsak("4003");

    public static Optional<XMLGregorianCalendar> finnSisteDagIFelleseriodeHvisFinnes(UttakResultatPerioder uttakResultatPerioder) {
        return uttakResultatPerioder.getPerioder()
                .stream()
                .map(UttakResultatPeriode::getAktiviteter)
                .flatMap(List::stream)
                .filter(aktivitet -> StønadskontoType.FELLESPERIODE.equals(aktivitet.getTrekkonto()))
                .map(UttakResultatPeriodeAktivitet::getTom)
                .max(LocalDate::compareTo)
                .map(XmlUtil::finnDatoVerdiAvUtenTidSone);
    }

    public static Optional<XMLGregorianCalendar> finnSisteDagMedUtsettelseHvisFinnes(UttakResultatPerioder uttakResultatPerioder) {
        return uttakResultatPerioder.getPerioder()
                .stream()
                .filter(Predicate.not(UttakResultatPeriode::isInnvilget))
                .filter(Predicate.not(periode -> UttakUtsettelseType.UDEFINERT.equals(periode.getUttakUtsettelseType())))
                .map(UttakResultatPeriode::getTom)
                .max(LocalDate::compareTo)
                .map(XmlUtil::finnDatoVerdiAvUtenTidSone);
    }

    public static boolean finnesPeriodeMedIkkeOmsorg(List<PeriodeType> perioder) {
        return perioder.
                stream().map(PeriodeType::getÅrsak).anyMatch(årsak -> MOR_HAR_IKKE_OMSORG.getKode().equals(årsak) || FAR_HAR_IKKE_OMSORG.getKode().equals(årsak));
    }

    public static UtbetaltKode forMyeUtbetaltKode(PeriodeListeType periodeListe, Behandling behandling) {
        if (!behandling.erRevurdering()) {
            return UtbetaltKode.INGEN;
        }
        return PeriodeBeregner.forMyeUtbetalt(periodeListe, null);
    }

    public String mapLovhjemlerForUttak(UttakResultatPerioder uttakResultatPerioder, String konsekvensForYtelse, boolean innvilgetRevurdering) {
        Set<String> lovhjemler = new TreeSet<>();
        for (UttakResultatPeriode periode : uttakResultatPerioder.getPerioder()) {
            ÅrsakskodeMedLovreferanse årsak = utledÅrsakskode(periode);
            if (erUkjent(årsak)) {
                continue;
            }
            if (årsak instanceof GraderingAvslagÅrsak) {
                LovhjemmelUtil.hentLovhjemlerFraJson(FagsakYtelseType.FORELDREPENGER, periode.getPeriodeResultatÅrsak());
            }
            lovhjemler.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(FagsakYtelseType.FORELDREPENGER, årsak));
        }
        return FellesMapper.formaterLovhjemlerUttak(lovhjemler, konsekvensForYtelse, innvilgetRevurdering);
    }


    private boolean erUkjent(ÅrsakskodeMedLovreferanse årsaksKode) {
        return PeriodeResultatÅrsak.UKJENT.getKode().equals(årsaksKode.getKode());
    }

    private ÅrsakskodeMedLovreferanse utledÅrsakskode(UttakResultatPeriode uttakPeriode) {
        if (erGraderingAvslått(uttakPeriode) && uttakPeriode.isInnvilget()) {
            return uttakPeriode.getGraderingAvslagÅrsak();
        } else if (uttakPeriode.getPeriodeResultatÅrsak() != null) {
            return uttakPeriode.getPeriodeResultatÅrsak();
        }
        return PeriodeResultatÅrsak.UKJENT;
    }

    private boolean erGraderingAvslått(UttakResultatPeriode uttakPeriode) {
        return !uttakPeriode.erGraderingInnvilget()
                && erGraderingÅrsakKjent(uttakPeriode.getGraderingAvslagÅrsak());
    }

    private boolean erGraderingÅrsakKjent(GraderingAvslagÅrsak årsak) {
        return årsak != null
                && !årsak.getKode().equals(GraderingAvslagÅrsak.UKJENT.getKode());
    }
}
