package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.AvslåttAktivitet;
import no.nav.foreldrepenger.fpformidling.uttak.fp.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.svp.SvpUttakResultatArbeidsforhold;

public final class AvslåttAktivitetMapper {

    private static final List<ArbeidsforholdIkkeOppfyltÅrsak> RELEVANTE_ARBEIDSFORHOLD_ÅRSAKER = List.of(
        ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE,
        ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN,
        ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO);

    private AvslåttAktivitetMapper() {
    }

    public static List<AvslåttAktivitet> mapAvslåtteAktiviteter(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold) {
        return uttakResultatArbeidsforhold.stream()
            .filter(ura -> RELEVANTE_ARBEIDSFORHOLD_ÅRSAKER.contains(ura.getArbeidsforholdIkkeOppfyltÅrsak()))
            .map(AvslåttAktivitetMapper::opprettSvpAvslagArbeidsforhold)
            .toList();
    }

    private static AvslåttAktivitet opprettSvpAvslagArbeidsforhold(SvpUttakResultatArbeidsforhold ura) {
        var arbeidsforholdIkkeOppfyltÅrsak = ura.getArbeidsforholdIkkeOppfyltÅrsak();
        var builder = AvslåttAktivitet.ny().medÅrsak(Årsak.of(arbeidsforholdIkkeOppfyltÅrsak.getKode()));

        if (kanArbeidsgiverTilrettelegge(arbeidsforholdIkkeOppfyltÅrsak)) {
            utledAtFlSn(ura, builder);
        }

        return builder.build();
    }

    private static boolean kanArbeidsgiverTilrettelegge(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak) {
        return ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.equals(arbeidsforholdIkkeOppfyltÅrsak)
            || ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN.equals(arbeidsforholdIkkeOppfyltÅrsak);
    }

    private static void utledAtFlSn(SvpUttakResultatArbeidsforhold ura, AvslåttAktivitet.Builder builder) {
        if (ura.getArbeidsgiver() != null) {
            builder.medArbeidsgiverNavn(ura.getArbeidsgiver().navn());
        } else if (UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE.equals(ura.getUttakArbeidType())) {
            builder.medErSN(true);
        } else if (UttakArbeidType.FRILANS.equals(ura.getUttakArbeidType())) {
            builder.medErFL(true);
        } else {
            throw new IllegalStateException("Kan ikke opprette avslått aktivitet for ukjent arbeidsgiver og/eller UttakArbeidType");
        }
    }
}
