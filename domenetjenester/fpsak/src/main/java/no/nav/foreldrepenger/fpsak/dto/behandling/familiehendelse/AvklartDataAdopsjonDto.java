package no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse;

import java.time.LocalDate;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(value = "AvklartDataAdopsjonDto")
public class AvklartDataAdopsjonDto extends FamiliehendelseDto {

    private Boolean mannAdoptererAlene;
    private Boolean ektefellesBarn;
    private LocalDate omsorgsovertakelseDato;
    private LocalDate ankomstNorge;

    @Valid
    @Size(max = 9)
    private Map<Integer, LocalDate> adopsjonFodelsedatoer;

    public LocalDate getOmsorgsovertakelseDato() {
        return omsorgsovertakelseDato;
    }

    void setOmsorgsovertakelseDato(LocalDate omsorgsovertakelseDato) {
        this.omsorgsovertakelseDato = omsorgsovertakelseDato;
    }

    public Map<Integer, LocalDate> getAdopsjonFodelsedatoer() {
        return adopsjonFodelsedatoer;
    }

    void setAdopsjonFodelsedatoer(Map<Integer, LocalDate> adopsjonFodelsedatoer) {
        this.adopsjonFodelsedatoer = adopsjonFodelsedatoer;
    }

    public Boolean getEktefellesBarn() {
        return ektefellesBarn;
    }

    void setEktefellesBarn(Boolean ektefellesBarn) {
        this.ektefellesBarn = ektefellesBarn;
    }

    public Boolean getMannAdoptererAlene() {
        return mannAdoptererAlene;
    }

    void setMannAdoptererAlene(Boolean mannAdoptererAlene) {
        this.mannAdoptererAlene = mannAdoptererAlene;
    }

    public LocalDate getAnkomstNorge() {
        return ankomstNorge;
    }

    void setAnkomstNorge(LocalDate ankomstNorge) {
        this.ankomstNorge = ankomstNorge;
    }
}
