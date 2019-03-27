package no.nav.foreldrepenger.fpsak.dto.soknad;

import java.time.LocalDate;
import java.util.Map;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class SoknadFodselDto extends SoknadDto {
    private LocalDate utstedtdato;
    private LocalDate termindato;
    private KodeDto farSokerType;
    private Map<Integer, LocalDate> fodselsdatoer;

    public SoknadFodselDto() {
        super();
    }

    public LocalDate getUtstedtdato() {
        return utstedtdato;
    }

    public LocalDate getTermindato() {
        return termindato;
    }

    public KodeDto getFarSokerType() {
        return farSokerType;
    }

    public Map<Integer, LocalDate> getFodselsdatoer() {
        return fodselsdatoer;
    }

    public void setUtstedtdato(LocalDate utstedtdato) {
        this.utstedtdato = utstedtdato;
    }

    public void setTermindato(LocalDate termindato) {
        this.termindato = termindato;
    }

    public void setFarSokerType(KodeDto farSokerType) {
        this.farSokerType = farSokerType;
    }

    public void setFodselsdatoer(Map<Integer, LocalDate> fodselsdatoer) {
        this.fodselsdatoer = fodselsdatoer;
    }

}
