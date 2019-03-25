package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        EndringBeregningsgrunnlagArbeidsforholdDto that = (EndringBeregningsgrunnlagArbeidsforholdDto) o;
        return Objects.equals(perioderMedGraderingEllerRefusjon, that.perioderMedGraderingEllerRefusjon);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), perioderMedGraderingEllerRefusjon);
    }
}
