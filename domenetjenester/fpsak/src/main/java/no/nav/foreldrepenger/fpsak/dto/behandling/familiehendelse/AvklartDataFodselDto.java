package no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "AvklartDataFodselDto")
public class AvklartDataFodselDto extends FamiliehendelseDto {
    private LocalDate fodselsdato;
    private Integer antallBarnFødt;
    private Boolean brukAntallBarnFraTps;
    private Boolean erOverstyrt;
    private LocalDate termindato;
    private Integer antallBarnTermin;
    private LocalDate utstedtdato;
    private Boolean morForSykVedFodsel;
    private Long vedtaksDatoSomSvangerskapsuke;

    @JsonProperty("fodselsdato")
    public LocalDate getFodselsdato() {
        return fodselsdato;
    }

    void setFodselsdato(LocalDate fodselsdato) {
        this.fodselsdato = fodselsdato;
    }

    @JsonProperty("antallBarnFodsel")
    public Integer getAntallBarnFødt() {
        return antallBarnFødt;
    }

    public void setAntallBarnFødt(Integer antallBarnFødt) {
        this.antallBarnFødt = antallBarnFødt;
    }

    @JsonProperty("brukAntallBarnFraTps")
    public Boolean getBrukAntallBarnFraTps() {
        return brukAntallBarnFraTps;
    }

    void setBrukAntallBarnFraTps(Boolean brukAntallBarnFraTps) {
        this.brukAntallBarnFraTps = brukAntallBarnFraTps;
    }

    //TODO(OJR) burde fjerne enten denne eller setAntallBarnFødt

    @JsonProperty("termindato")
    public LocalDate getTermindato() {
        return termindato;
    }

    public void setTermindato(LocalDate termindato) {
        this.termindato = termindato;
    }

    @JsonProperty("antallBarnTermin")
    public Integer getAntallBarnTermin() {
        return antallBarnTermin;
    }

    public void setAntallBarnTermin(Integer antallBarnTermin) {
        this.antallBarnTermin = antallBarnTermin;
    }

    @JsonProperty("utstedtdato")
    public LocalDate getUtstedtdato() {
        return utstedtdato;
    }

    void setUtstedtdato(LocalDate utstedtdato) {
        this.utstedtdato = utstedtdato;
    }

    @JsonProperty("dokumentasjonForeligger")
    public Boolean getErOverstyrt() {
        return erOverstyrt;
    }

    void setErOverstyrt(Boolean erOverstyrt) {
        this.erOverstyrt = erOverstyrt;
    }

    @JsonProperty("morForSykVedFodsel")
    public Boolean getMorForSykVedFodsel() {
        return morForSykVedFodsel;
    }

    public void setMorForSykVedFodsel(Boolean morForSykVedFodsel) {
        this.morForSykVedFodsel = morForSykVedFodsel;
    }

    @JsonProperty("vedtaksDatoSomSvangerskapsuke")
    public Long getVedtaksDatoSomSvangerskapsuke() {
        return vedtaksDatoSomSvangerskapsuke;
    }

    void setVedtaksDatoSomSvangerskapsuke(Long vedtaksDatoSomSvangerskapsuke) {
        this.vedtaksDatoSomSvangerskapsuke = vedtaksDatoSomSvangerskapsuke;
    }
}
