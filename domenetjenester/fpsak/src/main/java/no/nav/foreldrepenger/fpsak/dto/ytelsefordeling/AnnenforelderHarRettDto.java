package no.nav.foreldrepenger.fpsak.dto.ytelsefordeling;

import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.PeriodeDto;

public class AnnenforelderHarRettDto {

    private Boolean annenforelderHarRett;
    private String begrunnelse;
    private List<PeriodeDto> annenforelderHarRettPerioder;

    public AnnenforelderHarRettDto() {
    }

    public Boolean getAnnenforelderHarRett() {
        return annenforelderHarRett;
    }

    public void setAnnenforelderHarRett(Boolean annenforelderHarRett) {
        this.annenforelderHarRett = annenforelderHarRett;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public List<PeriodeDto> getAnnenforelderHarRettPerioder() {
        return annenforelderHarRettPerioder;
    }

    public void setAnnenforelderHarRettPerioder(List<PeriodeDto> annenforelderHarRettPerioder) {
        this.annenforelderHarRettPerioder = annenforelderHarRettPerioder;
    }
}
