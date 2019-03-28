package no.nav.foreldrepenger.melding.datamapper.domene.hjelperdto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class BeregningsgrunnlagRegelDto implements Serializable {
    private static final long serialVersionUID = -6444717542495046944L;
    private String status;
    private int antallArbeidsgivereIBeregning;
    private boolean besteBeregning;
    private List<BeregningsgrunnlagAndelDto> beregningsgrunnlagAndelDto = new ArrayList<>();
    private String selvstendingNæringsdrivendeNyoppstartet;

    public boolean getBesteBeregning() {
        return besteBeregning;
    }

    public void setBesteBeregning(boolean besteBeregning) {
        this.besteBeregning = besteBeregning;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<BeregningsgrunnlagAndelDto> getBeregningsgrunnlagAndelDto() {
        return beregningsgrunnlagAndelDto;
    }

    public void addBeregningsgrunnlagAndelDto(BeregningsgrunnlagAndelDto beregningsgrunnlagAndelDto) {
        this.beregningsgrunnlagAndelDto.add(beregningsgrunnlagAndelDto);
    }

    public int getAntallArbeidsgivereIBeregning() {
        return antallArbeidsgivereIBeregning;
    }

    public void setAntallArbeidsgivereIBeregning(int antallArbeidsgivereIBeregning) {
        this.antallArbeidsgivereIBeregning = antallArbeidsgivereIBeregning;
    }

    public void setSNnyoppstartet(String selvstendingNæringsdrivendeNyoppstartet) {
        this.selvstendingNæringsdrivendeNyoppstartet = selvstendingNæringsdrivendeNyoppstartet;
    }

    public String getSNnyoppstartet() {
        return selvstendingNæringsdrivendeNyoppstartet;
    }

    @Override
    public String toString() {
        return "BeregningsgrunnlagRegelDto{" +
                "status='" + status + '\'' +
                ", antallArbeidsgivereIBeregning=" + antallArbeidsgivereIBeregning +
                ", besteBeregning=" + besteBeregning +
                ", beregningsgrunnlagAndelDto=" + beregningsgrunnlagAndelDto +
                ", selvstendingNæringsdrivendeNyoppstartet=" + selvstendingNæringsdrivendeNyoppstartet +
                '}';
    }
}
