package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;

public class PeriodeVerktøy {

    private static Boolean periodeStreamInneholderGradering(List<PeriodeType> innvilgedePerioder) {
        return arbeidsforholdMedGraderingFinnes(innvilgedePerioder.stream()) ||
                annenAktivtitetMedGraderingFinnes(innvilgedePerioder.stream()) ||
                næringMedGraderingFinnes(innvilgedePerioder.stream());
    }

    public static boolean periodeHarGradering(PeriodeType periodeType) {
        return periodeStreamInneholderGradering(List.of(periodeType));
    }

    private static boolean annenAktivtitetMedGraderingFinnes(Stream<PeriodeType> periodeStream) {
        return periodeStream
                .map(PeriodeType::getAnnenAktivitetListe)
                .filter(Objects::nonNull)
                .map(AnnenAktivitetListeType::getAnnenAktivitet)
                .flatMap(Collection::stream)
                .anyMatch(AnnenAktivitetType::isGradering);
    }

    private static boolean næringMedGraderingFinnes(Stream<PeriodeType> periodeStream) {
        return periodeStream
                .map(PeriodeType::getNæringListe)
                .filter(Objects::nonNull)
                .map(NæringListeType::getNæring)
                .filter(Objects::nonNull)
                .anyMatch(NæringType::isGradering);
    }

    private static boolean arbeidsforholdMedGraderingFinnes(Stream<PeriodeType> periodeStream) {
        return periodeStream
                .map(PeriodeType::getArbeidsforholdListe)
                .filter(Objects::nonNull)
                .map(ArbeidsforholdListeType::getArbeidsforhold)
                .flatMap(Collection::stream)
                .anyMatch(ArbeidsforholdType::isGradering);
    }

    public static LocalDate xmlGregorianTilLocalDate(XMLGregorianCalendar dato) {
        return dato.toGregorianCalendar().toZonedDateTime().toLocalDate();
    }
}
