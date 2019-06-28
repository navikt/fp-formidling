package no.nav.foreldrepenger.fpsak.dto.uttak.svp;

import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.uttak.ArbeidsgiverDto;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;

public class SvangerskapspengerUttakResultatArbeidsforholdDto {
    private ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak;
    private ArbeidsgiverDto arbeidsgiver;
    private UttakArbeidType uttakArbeidType;

    private List<SvangerskapspengerUttakResultatPeriodeDto> perioder;

    public SvangerskapspengerUttakResultatArbeidsforholdDto() {
    }

    public ArbeidsforholdIkkeOppfyltÅrsak getArbeidsforholdIkkeOppfyltÅrsak() {
        return arbeidsforholdIkkeOppfyltÅrsak;
    }

    public ArbeidsgiverDto getArbeidsgiver() {
        return arbeidsgiver;
    }

    public UttakArbeidType getUttakArbeidType() {
        return uttakArbeidType;
    }

    public List<SvangerskapspengerUttakResultatPeriodeDto> getPerioder() {
        return perioder;
    }

}

