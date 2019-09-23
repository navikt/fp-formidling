package no.nav.foreldrepenger.melding.datamapper.domene;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.UtbetaltKode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;

public class UttakMapper {

    public UttakMapper() {
        //CDI
    }

    public static boolean finnesPeriodeMedIkkeOmsorg(List<PeriodeType> perioder) {
        return perioder.
                stream().map(PeriodeType::getÅrsak).anyMatch(årsak -> PeriodeResultatÅrsak.MOR_HAR_IKKE_OMSORG.getKode().equals(årsak) || PeriodeResultatÅrsak.FAR_HAR_IKKE_OMSORG.getKode().equals(årsak));
    }

    public static UtbetaltKode forMyeUtbetaltKode(PeriodeListeType periodeListe, Behandling behandling) {
        if (!behandling.erRevurdering()) {
            return UtbetaltKode.INGEN;
        }
        return PeriodeBeregner.forMyeUtbetalt(periodeListe, behandling.getAvsluttet() != null ? behandling.getAvsluttet().toLocalDate() : null);
    }

    public static String mapLovhjemlerForUttak(UttakResultatPerioder uttakResultatPerioder, String konsekvensForYtelse, boolean innvilgetRevurdering) {
        Set<String> lovhjemler = new TreeSet<>(new LovhjemmelComparator());
        for (UttakResultatPeriode periode : uttakResultatPerioder.getPerioder()) {
            PeriodeResultatÅrsak årsak = utledÅrsakskode(periode);
            if (årsak.erUkjent()) {
                continue;
            }
            if (årsak.erGraderingAvslagÅrsak()) {
                LovhjemmelUtil.hentLovhjemlerFraJson(FagsakYtelseType.FORELDREPENGER, periode.getPeriodeResultatÅrsak());
            }
            lovhjemler.addAll(LovhjemmelUtil.hentLovhjemlerFraJson(FagsakYtelseType.FORELDREPENGER, årsak));
        }
        return FellesMapper.formaterLovhjemlerUttak(lovhjemler, konsekvensForYtelse, innvilgetRevurdering);
    }

    private static PeriodeResultatÅrsak utledÅrsakskode(UttakResultatPeriode uttakPeriode) {
        if (erGraderingAvslått(uttakPeriode) && uttakPeriode.isInnvilget()) {
            return uttakPeriode.getGraderingAvslagÅrsak();
        } else if (uttakPeriode.getPeriodeResultatÅrsak() != null) {
            return uttakPeriode.getPeriodeResultatÅrsak();
        }
        return PeriodeResultatÅrsak.UKJENT;
    }

    private static boolean erGraderingAvslått(UttakResultatPeriode uttakPeriode) {
        return !uttakPeriode.erGraderingInnvilget()
                && !uttakPeriode.getGraderingAvslagÅrsak().erUkjent();
    }

    public static XMLGregorianCalendar finnSisteDagAvSistePeriode(UttakResultatPerioder uttakResultatPerioder) {
        return Stream.concat(
                uttakResultatPerioder.getPerioder().stream(),
                uttakResultatPerioder.getPerioderAnnenPart().stream()
        ).filter(UttakResultatPeriode::isInnvilget)
                .map(UttakResultatPeriode::getTom)
                .max(LocalDate::compareTo)
                .map(XmlUtil::finnDatoVerdiAvUtenTidSone)
                .orElse(null);
    }
}
