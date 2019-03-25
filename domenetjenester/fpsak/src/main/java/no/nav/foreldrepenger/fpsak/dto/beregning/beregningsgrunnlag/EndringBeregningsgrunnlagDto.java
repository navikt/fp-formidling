package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.util.ArrayList;
import java.util.List;


public class EndringBeregningsgrunnlagDto {

    private List<EndringBeregningsgrunnlagPeriodeDto> endringBeregningsgrunnlagPerioder = new ArrayList<>();
    private List<EndringBeregningsgrunnlagArbeidsforholdDto> endredeArbeidsforhold = new ArrayList<>();


    public List<EndringBeregningsgrunnlagArbeidsforholdDto> getEndredeArbeidsforhold() {
        return endredeArbeidsforhold;
    }

    public void setEndredeArbeidsforhold(List<EndringBeregningsgrunnlagArbeidsforholdDto> endredeArbeidsforhold) {
        this.endredeArbeidsforhold = endredeArbeidsforhold;
    }

    public void leggTilEndretArbeidsforhold(EndringBeregningsgrunnlagArbeidsforholdDto endretArbeidsforhold) {
        this.endredeArbeidsforhold.add(endretArbeidsforhold);
    }

    public List<EndringBeregningsgrunnlagPeriodeDto> getEndringBeregningsgrunnlagPerioder() {
        return endringBeregningsgrunnlagPerioder;
    }

    public void setEndringBeregningsgrunnlagPerioder(List<EndringBeregningsgrunnlagPeriodeDto> endringBeregningsgrunnlagPerioder) {
        this.endringBeregningsgrunnlagPerioder = endringBeregningsgrunnlagPerioder;
    }
}
