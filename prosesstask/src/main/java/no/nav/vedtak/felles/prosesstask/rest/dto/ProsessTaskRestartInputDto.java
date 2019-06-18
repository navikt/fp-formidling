package no.nav.vedtak.felles.prosesstask.rest.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

@ApiModel(value = "Informasjon for restart av en eksisterende prosesstask")
public class ProsessTaskRestartInputDto implements AbacDto {

    @Min(0)
    @Max(Long.MAX_VALUE)
    @NotNull
    private Long prosessTaskId;

    @ApiModelProperty(
            value = "Nåværende status. Angis hviss prosessen som skal restartes har en annen status enn KLAR.",
            allowableValues = "VENTER_SVAR, SUSPENDERT, FEILET"
    )
    @Size(max = 15)
    @Pattern(regexp = "VENTER_SVAR|FEILET|SUSPENDERT")
    private String naaVaaerendeStatus;

    public ProsessTaskRestartInputDto() { // NOSONAR Input-dto, ingen behov for initialisering
    }

    public Long getProsessTaskId() {
        return prosessTaskId;
    }

    public void setProsessTaskId(Long prosessTaskId) {
        this.prosessTaskId = prosessTaskId;
    }

    public String getNaaVaaerendeStatus() {
        return naaVaaerendeStatus;
    }

    public void setNaaVaaerendeStatus(String naaVaaerendeStatus) {
        this.naaVaaerendeStatus = naaVaaerendeStatus;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett(); //denne er tom, ProsessTask-API har i praksis rollebasert tilgangskontroll
    }
}
