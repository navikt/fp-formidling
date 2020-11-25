package no.nav.foreldrepenger.fpsak.dto.uttak.svp;

import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class SvangerskapspengerUttakResultatArbeidsforholdDto {
    private KodeDto arbeidsforholdIkkeOppfyltÅrsak;
    private String arbeidsgiverReferanse;
    private KodeDto  arbeidType;

    private List<SvangerskapspengerUttakResultatPeriodeDto> perioder;

    public SvangerskapspengerUttakResultatArbeidsforholdDto() {
    }

    public KodeDto getArbeidsforholdIkkeOppfyltÅrsak() {
        return arbeidsforholdIkkeOppfyltÅrsak;
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public KodeDto getArbeidType() {
        return arbeidType;
    }

    public List<SvangerskapspengerUttakResultatPeriodeDto> getPerioder() {
        return perioder;
    }

}

