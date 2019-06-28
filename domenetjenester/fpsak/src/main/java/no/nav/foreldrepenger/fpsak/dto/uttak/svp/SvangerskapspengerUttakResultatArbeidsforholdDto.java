package no.nav.foreldrepenger.fpsak.dto.uttak.svp;

import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.ArbeidsgiverDto;

public class SvangerskapspengerUttakResultatArbeidsforholdDto {
    private KodeDto arbeidsforholdIkkeOppfyltÅrsak;
    private ArbeidsgiverDto arbeidsgiver;
    private KodeDto  arbeidType;

    private List<SvangerskapspengerUttakResultatPeriodeDto> perioder;

    public SvangerskapspengerUttakResultatArbeidsforholdDto() {
    }

    public KodeDto getArbeidsforholdIkkeOppfyltÅrsak() {
        return arbeidsforholdIkkeOppfyltÅrsak;
    }

    public ArbeidsgiverDto getArbeidsgiver() {
        return arbeidsgiver;
    }

    public KodeDto getArbeidType() {
        return arbeidType;
    }

    public List<SvangerskapspengerUttakResultatPeriodeDto> getPerioder() {
        return perioder;
    }

}

