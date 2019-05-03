package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdType;

public class DuplikatVerktøy {

    private DuplikatVerktøy() {
        //for sonar
    }

    public static List<ArbeidsforholdType> slåSammenLikeArbeidsforhold(List<ArbeidsforholdType> eksisterendeListe) {
        List<ArbeidsforholdType> nyListe = new ArrayList<>(eksisterendeListe);

        for (ArbeidsforholdType arbeidsforhold : eksisterendeListe) {
            for (ArbeidsforholdType arbeidsforhold2 : eksisterendeListe) {
                if (nyListe.containsAll(List.of(arbeidsforhold, arbeidsforhold2)) && sammeArbeidsforhold(arbeidsforhold, arbeidsforhold2)) {
                    summerDagsats(arbeidsforhold, arbeidsforhold2);
                    nyListe.remove(arbeidsforhold2);
                }
            }
        }
        return nyListe;
    }

    private static void summerDagsats(ArbeidsforholdType arbeidsforhold, ArbeidsforholdType arbeidsforhold2) {
        arbeidsforhold.setAktivitetDagsats(arbeidsforhold.getAktivitetDagsats() + arbeidsforhold2.getAktivitetDagsats());
    }

    private static boolean sammeArbeidsforhold(ArbeidsforholdType arb1, ArbeidsforholdType arb2) {
        return !arb1.equals(arb2)
                && Objects.equals(arb1.getArbeidsgiverNavn(), arb2.getArbeidsgiverNavn())
                && Objects.equals(arb1.getStillingsprosent(), arb2.getStillingsprosent())
                && Objects.equals(arb1.getProsentArbeid(), arb2.getProsentArbeid())
                && Objects.equals(arb1.getUtbetalingsgrad(), arb2.getUtbetalingsgrad());
    }
}
