package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.svp;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;

public record SvangerskapspengerUttakResultatArbeidsforholdDto(ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak,
                                                               String arbeidsgiverReferanse, UttakArbeidType arbeidType,
                                                               List<SvangerskapspengerUttakResultatPeriodeDto> perioder) {

}

