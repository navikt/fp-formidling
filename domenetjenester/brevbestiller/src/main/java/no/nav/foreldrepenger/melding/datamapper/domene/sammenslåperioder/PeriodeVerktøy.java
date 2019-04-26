package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;

public class PeriodeVerktøy {


    public static Boolean graderingFinnes(PeriodeListeType periodeListe) {
        Stream<PeriodeType> innvilgedePerioderStream = periodeListe.getPeriode().stream().filter(PeriodeType::isInnvilget);
        return periodeStreamInneholderGradering(innvilgedePerioderStream);
    }

    private static Boolean periodeStreamInneholderGradering(Stream<PeriodeType> innvilgedePerioderStream) {
        return arbeidsforholdMedGraderingFinnes(innvilgedePerioderStream) ||
                annenAktivtitetMedGraderingFinnes(innvilgedePerioderStream) ||
                næringMedGraderingFinnes(innvilgedePerioderStream);
    }

    public static boolean periodeHarGradering(PeriodeType periodeType) {
        return periodeStreamInneholderGradering(Stream.of(periodeType));
    }

    private static boolean annenAktivtitetMedGraderingFinnes(Stream<PeriodeType> periodeStream) {
        return periodeStream
                .map(PeriodeType::getAnnenAktivitetListe)
                .map(AnnenAktivitetListeType::getAnnenAktivitet)
                .flatMap(Collection::stream)
                .anyMatch(AnnenAktivitetType::isGradering);
    }

    private static boolean næringMedGraderingFinnes(Stream<PeriodeType> periodeStream) {
        return periodeStream
                .map(PeriodeType::getNæringListe)
                .map(NæringListeType::getNæring)
                .anyMatch(NæringType::isGradering);
    }

    private static boolean arbeidsforholdMedGraderingFinnes(Stream<PeriodeType> periodeStream) {
        return periodeStream
                .map(PeriodeType::getArbeidsforholdListe)
                .map(ArbeidsforholdListeType::getArbeidsforhold)
                .flatMap(Collection::stream)
                .anyMatch(ArbeidsforholdType::isGradering);
    }

    static LocalDate xmlGregorianTilLocalDate(XMLGregorianCalendar dato) {
        return dato.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }
}
