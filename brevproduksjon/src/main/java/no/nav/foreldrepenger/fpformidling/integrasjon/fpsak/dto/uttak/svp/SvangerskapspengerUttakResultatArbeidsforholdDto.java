package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.svp;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;

public record SvangerskapspengerUttakResultatArbeidsforholdDto(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak,
                                                               String arbeidsgiverReferanse, UttakArbeidType arbeidType,
                                                               List<SvangerskapspengerUttakResultatPeriodeDto> perioder) {

}

