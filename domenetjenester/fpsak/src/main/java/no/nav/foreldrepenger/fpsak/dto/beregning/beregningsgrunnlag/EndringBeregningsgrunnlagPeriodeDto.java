package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import static no.nav.vedtak.konfig.Tid.TIDENES_ENDE;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EndringBeregningsgrunnlagPeriodeDto {

    private LocalDate fom;
    private LocalDate tom;
    private List<EndringBeregningsgrunnlagAndelDto> endringBeregningsgrunnlagAndeler = new ArrayList<>();
    private boolean harPeriodeAarsakGraderingEllerRefusjon = false;
    private boolean skalKunneEndreRefusjon = false;


    public boolean isHarPeriodeAarsakGraderingEllerRefusjon() {
        return harPeriodeAarsakGraderingEllerRefusjon;
    }

    public void setHarPeriodeAarsakGraderingEllerRefusjon(boolean harPeriodeAarsakGraderingEllerRefusjon) {
        this.harPeriodeAarsakGraderingEllerRefusjon = harPeriodeAarsakGraderingEllerRefusjon;
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
        if (!TIDENES_ENDE.equals(tom)) {
            this.tom = tom;
        }
    }

    public List<EndringBeregningsgrunnlagAndelDto> getEndringBeregningsgrunnlagAndeler() {
        return endringBeregningsgrunnlagAndeler;
    }

    public void setEndringBeregningsgrunnlagAndeler(List<EndringBeregningsgrunnlagAndelDto> endringBeregningsgrunnlagAndeler) {
        this.endringBeregningsgrunnlagAndeler = endringBeregningsgrunnlagAndeler;
    }

    public void leggTilEndringBeregningsgrunnlagAndel(EndringBeregningsgrunnlagAndelDto endringBeregningsgrunnlagAndel) {
        this.endringBeregningsgrunnlagAndeler.add(endringBeregningsgrunnlagAndel);
    }

    public boolean isSkalKunneEndreRefusjon() {
        return skalKunneEndreRefusjon;
    }

    public void setSkalKunneEndreRefusjon(boolean skalKunneEndreRefusjon) {
        this.skalKunneEndreRefusjon = skalKunneEndreRefusjon;
    }
}
