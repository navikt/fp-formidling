package no.nav.foreldrepenger.melding.datamapper.domene.svp;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;

final class SvpUtledAvslagArbeidsforhold {

    private static final List<ArbeidsforholdIkkeOppfyltÅrsak> RELEVANTE_ARBEIDSFORHOLD_ÅRSAKER = List.of(
            ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE,
            ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN,
            ArbeidsforholdIkkeOppfyltÅrsak.HELE_UTTAKET_ER_ETTER_3_UKER_FØR_TERMINDATO
    );

    private SvpUtledAvslagArbeidsforhold() {
        // Skjul default constructor
    }

    static Set<SvpAvslagArbeidsforhold> utled(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold) {
        return uttakResultatArbeidsforhold.stream()
                .filter(ura -> RELEVANTE_ARBEIDSFORHOLD_ÅRSAKER.contains(ura.getArbeidsforholdIkkeOppfyltÅrsak()))
                .map(SvpUtledAvslagArbeidsforhold::opprettSvpAvslagArbeidsforhold)
                .collect(Collectors.toSet());
    }

    private static SvpAvslagArbeidsforhold opprettSvpAvslagArbeidsforhold(SvpUttakResultatArbeidsforhold ura) {
        final var arbeidsforholdIkkeOppfyltÅrsak = ura.getArbeidsforholdIkkeOppfyltÅrsak();
        final var builder = SvpAvslagArbeidsforhold.Builder.ny().medAarsakskode(arbeidsforholdIkkeOppfyltÅrsak);
        if (kanArbeidsgiverTilrettelegge(arbeidsforholdIkkeOppfyltÅrsak)) {
            return utled_AT_FL_SN(ura, builder);
        }
        return builder.build();
    }

    private static boolean kanArbeidsgiverTilrettelegge(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak) {
        return ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE.equals(arbeidsforholdIkkeOppfyltÅrsak) ||
                ArbeidsforholdIkkeOppfyltÅrsak.ARBEIDSGIVER_KAN_TILRETTELEGGE_FREM_TIL_3_UKER_FØR_TERMIN.equals(arbeidsforholdIkkeOppfyltÅrsak);
    }

    private static SvpAvslagArbeidsforhold utled_AT_FL_SN(SvpUttakResultatArbeidsforhold ura, SvpAvslagArbeidsforhold.Builder builder) {
        if (ura.getArbeidsgiver() != null) {
            return builder.medArbeidsgiverNavn(ura.getArbeidsgiver().navn()).build();
        }
        if (UttakArbeidType.SELVSTENDIG_NÆRINGSDRIVENDE.equals(ura.getUttakArbeidType())) {
            return builder.medErSN(true).build();
        }
        if (UttakArbeidType.FRILANS.equals(ura.getUttakArbeidType())) {
            return builder.medErFL(true).build();
        }
        throw new IllegalStateException("Kan ikke opprette avslått arbeidsforhold for ukjent arbeidsgiver og/eller UttakArbeidType");
    }

}
