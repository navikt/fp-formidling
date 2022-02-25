package no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "AvklartDataOmsorgDto")
public class AvklartDataOmsorgDto extends FamiliehendelseDto {

    private LocalDate omsorgsovertakelseDato;

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

    public Integer getAntallBarnTilBeregning() {
        return antallBarnTilBeregning;
    }

    public void setAntallBarnTilBeregning(Integer antallBarnTilBeregning) {
        this.antallBarnTilBeregning = antallBarnTilBeregning;
    }
}
