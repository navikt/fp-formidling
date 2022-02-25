package no.nav.foreldrepenger.fpsak.dto.uttak.svp;

import java.util.List;

import no.nav.foreldrepenger.fpformidling.uttak.UttakArbeidType;
import no.nav.foreldrepenger.fpformidling.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;

public class SvangerskapspengerUttakResultatArbeidsforholdDto {
    private ArbeidsforholdIkkeOppfyltÅrsak arbeidsforholdIkkeOppfyltÅrsak;
    private String arbeidsgiverReferanse;
    private UttakArbeidType arbeidType;

    private List<SvangerskapspengerUttakResultatPeriodeDto> perioder;

    public SvangerskapspengerUttakResultatArbeidsforholdDto() {
    }

    public ArbeidsforholdIkkeOppfyltÅrsak getArbeidsforholdIkkeOppfyltÅrsak() {
        return arbeidsforholdIkkeOppfyltÅrsak;
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public UttakArbeidType getArbeidType() {
        return arbeidType;
    }

    public List<SvangerskapspengerUttakResultatPeriodeDto> getPerioder() {
        return perioder;
    }

}

