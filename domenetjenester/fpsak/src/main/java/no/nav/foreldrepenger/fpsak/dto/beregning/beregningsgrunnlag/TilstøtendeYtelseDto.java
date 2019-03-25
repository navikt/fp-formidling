
package no.nav.foreldrepenger.fpsak.dto.beregning.beregningsgrunnlag;

import java.math.BigDecimal;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class TilstøtendeYtelseDto {

    private List<TilstøtendeYtelseAndelDto> tilstøtendeYtelseAndeler;
    private Long dekningsgrad;
    private KodeDto arbeidskategori;
    private KodeDto ytelseType;
    private BigDecimal bruttoBG;
    private boolean skalReduseres;
    private boolean erBesteberegning;

    public TilstøtendeYtelseDto() {
        // Hibernate
    }

    public boolean isSkalReduseres() {
        return skalReduseres;
    }

    public void setSkalReduseres(boolean skalReduseres) {
        this.skalReduseres = skalReduseres;
    }

    public Long getDekningsgrad() {
        return dekningsgrad;
    }

    public void setDekningsgrad(Long dekningsgrad) {
        this.dekningsgrad = dekningsgrad;
    }

    public KodeDto getYtelseType() {
        return ytelseType;
    }

    public void setYtelseType(KodeDto ytelseType) {
        this.ytelseType = ytelseType;
    }

    public BigDecimal getBruttoBG() {
        return bruttoBG;
    }

    public void setBruttoBG(BigDecimal bruttoBG) {
        this.bruttoBG = bruttoBG;
    }

    public List<TilstøtendeYtelseAndelDto> getTilstøtendeYtelseAndeler() {
        return tilstøtendeYtelseAndeler;
    }

    public void setTilstøtendeYtelseAndeler(List<TilstøtendeYtelseAndelDto> tilstøtendeYtelseAndeler) {
        this.tilstøtendeYtelseAndeler = tilstøtendeYtelseAndeler;
    }

    public KodeDto getArbeidskategori() {
        return arbeidskategori;
    }

    public void setArbeidskategori(KodeDto arbeidskategori) {
        this.arbeidskategori = arbeidskategori;
    }

    public boolean getErBesteberegning() {
        return erBesteberegning;
    }

    public void setErBesteberegning(boolean erBesteberegning) {
        this.erBesteberegning = erBesteberegning;
    }
}
