package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.util.ArrayList;
import java.util.List;


public class EndringBeregningsgrunnlagArbeidsforholdDto extends BeregningsgrunnlagArbeidsforholdDto {

    private List<GraderingEllerRefusjonDto> perioderMedGraderingEllerRefusjon = new ArrayList<>();

    public void leggTilPeriodeMedGraderingEllerRefusjon(GraderingEllerRefusjonDto periodeMedGraderingEllerRefusjon) {
        this.perioderMedGraderingEllerRefusjon.add(periodeMedGraderingEllerRefusjon);
    }

    public List<GraderingEllerRefusjonDto> getPerioderMedGraderingEllerRefusjon() {
        return perioderMedGraderingEllerRefusjon;
    }

    public void setPerioderMedGraderingEllerRefusjon(List<GraderingEllerRefusjonDto> perioderMedGraderingEllerRefusjon) {
        this.perioderMedGraderingEllerRefusjon = perioderMedGraderingEllerRefusjon;
    }
}
