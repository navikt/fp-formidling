package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsesvp;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Svangerskapspenger.UttakArbeidsforhold;

import java.util.List;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp.AvslåttAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Fpsak;

public final class AvslåttAktivitetMapper {

    private static final List<String> RELEVANTE_ARBEIDSFORHOLD_ÅRSAKER = List.of(
        ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode(),
        ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN.getKode(),
        ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO.getKode());

    private AvslåttAktivitetMapper() {
    }

    public static List<AvslåttAktivitet> mapAvslåtteAktiviteter(List<UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                                                UnaryOperator<String> hentNavn) {
        return uttakResultatArbeidsforhold.stream()
            .filter(ura -> ura.arbeidsforholdIkkeOppfyltÅrsak() != null && !ura.arbeidsforholdIkkeOppfyltÅrsak().equals(Fpsak.STANDARDKODE_UDEFINERT))
            .filter(ura -> RELEVANTE_ARBEIDSFORHOLD_ÅRSAKER.contains(ura.arbeidsforholdIkkeOppfyltÅrsak()))
            .map(uttakArbeidsforhold -> opprettSvpAvslagArbeidsforhold(uttakArbeidsforhold, hentNavn))
            .toList();
    }

    private static AvslåttAktivitet opprettSvpAvslagArbeidsforhold(UttakArbeidsforhold ura, UnaryOperator<String> hentNavn) {
        var arbeidsforholdIkkeOppfyltÅrsak = ura.arbeidsforholdIkkeOppfyltÅrsak();
        var builder = AvslåttAktivitet.ny().medÅrsak(Årsak.of(arbeidsforholdIkkeOppfyltÅrsak));

        if (kanArbeidsgiverTilrettelegge(arbeidsforholdIkkeOppfyltÅrsak)) {
            utledAtFlSn(ura, builder, hentNavn);
        }

        return builder.build();
    }

    private static boolean kanArbeidsgiverTilrettelegge(String arbeidsforholdIkkeOppfyltÅrsak) {
        return ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.getKode().equals(arbeidsforholdIkkeOppfyltÅrsak)
            || ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN.getKode()
            .equals(arbeidsforholdIkkeOppfyltÅrsak);
    }

    private static void utledAtFlSn(UttakArbeidsforhold ura, AvslåttAktivitet.Builder builder, UnaryOperator<String> hentNavn) {
        if (ura.arbeidsgiverReferanse() != null) {
            builder.medArbeidsgiverNavn(hentNavn.apply(ura.arbeidsgiverReferanse()));
        } else if (BrevGrunnlagDto.UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE.equals(ura.arbeidType())) {
            builder.medErSN(true);
        } else if (BrevGrunnlagDto.UttakArbeidType.FRILANS.equals(ura.arbeidType())) {
            builder.medErFL(true);
        } else {
            throw new IllegalStateException("Kan ikke opprette avslått aktivitet for ukjent arbeidsgiver og/eller UttakArbeidType");
        }
    }
}
