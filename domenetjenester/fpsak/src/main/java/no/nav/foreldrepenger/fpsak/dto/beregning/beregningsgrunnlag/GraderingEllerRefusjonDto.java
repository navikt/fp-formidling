package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GraderingEllerRefusjonDto {

    @JsonProperty("erRefusjon")
    private boolean erRefusjon;
    @JsonProperty("erGradering")
    private boolean erGradering;
    @JsonProperty("fom")
    private LocalDate fom;
    @JsonProperty("tom")
    private LocalDate tom;


    public GraderingEllerRefusjonDto(boolean erRefusjon, boolean erGradering) {
        if ((erRefusjon && erGradering) || (!erRefusjon && !erGradering)) {
            throw new IllegalArgumentException("Må gjelde enten gradering eller refusjon");
        }
        this.erGradering = erGradering;
        this.erRefusjon = erRefusjon;
    }

    public boolean isErRefusjon() {
        return erRefusjon;
    }

    public void setErRefusjon(boolean erRefusjon) {
        this.erRefusjon = erRefusjon;
    }

    public boolean isErGradering() {
        return erGradering;
    }

    public void setErGradering(boolean erGradering) {
        this.erGradering = erGradering;
    }

    public LocalDate getFom() {
        return fom;
    }

    public void setFom(LocalDate fom) {
        this.fom = fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public void setTom(LocalDate tom) {
        this.tom = tom;
    }
}
