package no.nav.foreldrepenger.melding.beregningsgrunnlag;

import java.util.Optional;

import no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag.BeregningsgrunnlagArbeidsforholdDto;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;

public class BGAndelArbeidsforhold {
    private Arbeidsgiver arbeidsgiver;
    private ArbeidsforholdRef arbeidsforholdRef;
    private String arbeidsforholdType;

    public BGAndelArbeidsforhold(Arbeidsgiver arbeidsgiver, ArbeidsforholdRef arbeidsforholdRef, String arbeidsforholdType) {
        this.arbeidsgiver = arbeidsgiver;
        this.arbeidsforholdRef = arbeidsforholdRef;
        this.arbeidsforholdType = arbeidsforholdType;
    }

    public static BGAndelArbeidsforhold fraDto(BeregningsgrunnlagArbeidsforholdDto dto) {
        return new BGAndelArbeidsforhold(Arbeidsgiver.fraDto(dto), ArbeidsforholdRef.ref(dto.getArbeidsforholdId()), dto.getArbeidsforholdType().kode);
    }

    public String getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public Optional<Arbeidsgiver> getArbeidsgiver() {
        return Optional.ofNullable(arbeidsgiver);
    }

    public Optional<ArbeidsforholdRef> getArbeidsforholdRef() {
        return Optional.ofNullable(arbeidsforholdRef);
    }

    public Optional<Virksomhet> getVirksomhet() {
        return Optional.ofNullable(arbeidsgiver).map(Arbeidsgiver::getVirksomhet);
    }
}
