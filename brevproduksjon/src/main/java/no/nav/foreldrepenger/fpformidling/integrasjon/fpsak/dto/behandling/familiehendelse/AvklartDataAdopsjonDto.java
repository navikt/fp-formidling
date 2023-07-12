package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse;

import com.fasterxml.jackson.annotation.JsonTypeName;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Map;

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

    public void setAdopsjonFodelsedatoer(Map<Integer, LocalDate> adopsjonFodelsedatoer) {
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
