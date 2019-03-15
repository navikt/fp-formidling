package no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse;

import java.time.LocalDate;

import javax.validation.Valid;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;


public class AvklartDataOmsorgDto extends FamiliehendelseDto {

    private LocalDate omsorgsovertakelseDato;

    @Valid
    private KodeDto vilkarType;

    private Integer antallBarnTilBeregning;
    private LocalDate foreldreansvarDato;

    public AvklartDataOmsorgDto() {
        // trengs for deserialisering av JSON
        super();
    }

    public LocalDate getForeldreansvarDato() {
        return foreldreansvarDato;
    }

    public void setForeldreansvarDato(LocalDate foreldreansvarDato) {
        this.foreldreansvarDato = foreldreansvarDato;
    }

    public LocalDate getOmsorgsovertakelseDato() {
        return omsorgsovertakelseDato;
    }

    void setOmsorgsovertakelseDato(LocalDate omsorgsovertakelseDato) {
        this.omsorgsovertakelseDato = omsorgsovertakelseDato;
    }

    public KodeDto getVilkarType() {
        return vilkarType;
    }

    void setVilkarType(KodeDto vilkarType) {
        this.vilkarType = vilkarType;
    }

    public Integer getAntallBarnTilBeregning() {
        return antallBarnTilBeregning;
    }

    void setAntallBarnTilBeregning(Integer antallBarnTilBeregning) {
        this.antallBarnTilBeregning = antallBarnTilBeregning;
    }
}
