package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.svp;

import java.util.List;

public class SvangerskapspengerUttakResultatDto {
    private List<SvangerskapspengerUttakResultatArbeidsforholdDto> uttaksResultatArbeidsforhold;

    public SvangerskapspengerUttakResultatDto() {
    }

    public SvangerskapspengerUttakResultatDto(List<SvangerskapspengerUttakResultatArbeidsforholdDto> uttaksResultatArbeidsforhold) {
        this.uttaksResultatArbeidsforhold = uttaksResultatArbeidsforhold;
    }

    public List<SvangerskapspengerUttakResultatArbeidsforholdDto> getUttaksResultatArbeidsforhold() {
        return uttaksResultatArbeidsforhold;
    }
}
