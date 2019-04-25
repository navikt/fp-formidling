package no.nav.foreldrepenger.melding.datamapper.domene;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.uttak.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.UttakUtsettelseType;

public class UttakMapper {

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
}
