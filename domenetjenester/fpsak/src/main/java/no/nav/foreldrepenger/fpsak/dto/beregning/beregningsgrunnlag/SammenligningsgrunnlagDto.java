package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SammenligningsgrunnlagDto {
    private LocalDate sammenligningsgrunnlagFom;
    private LocalDate sammenligningsgrunnlagTom;
    private BigDecimal rapportertPrAar;
    private Long avvikPromille;

    public SammenligningsgrunnlagDto() {
        // trengs for deserialisering av JSON
    }

    public LocalDate getSammenligningsgrunnlagFom() {
        return sammenligningsgrunnlagFom;
    }

    public LocalDate getSammenligningsgrunnlagTom() {
        return sammenligningsgrunnlagTom;
    }

    public BigDecimal getRapportertPrAar() {
        return rapportertPrAar;
    }

    public Long getAvvikPromille() {
        return avvikPromille;
    }

    public void setSammenligningsgrunnlagFom(LocalDate sammenligningsgrunnlagFom) {
        this.sammenligningsgrunnlagFom = sammenligningsgrunnlagFom;
    }

    public void setSammenligningsgrunnlagTom(LocalDate sammenligningsgrunnlagTom) {
        this.sammenligningsgrunnlagTom = sammenligningsgrunnlagTom;
    }

    public void setRapportertPrAar(BigDecimal rapportertPrAar) {
        this.rapportertPrAar = rapportertPrAar;
    }

    public void setAvvikPromille(Long avvikPromille) {
        this.avvikPromille = avvikPromille;
    }
}
