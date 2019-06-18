package no.nav.vedtak.felles.prosesstask.rest.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

@ApiModel
public class ProsessTaskIdDto implements AbacDto {

    @NotNull
    @Min(0)
    @Max(Long.MAX_VALUE)
    private Long prosessTaskId;

    public ProsessTaskIdDto() { // NOSONAR Input-dto, ingen behov for initialisering
    }

    public ProsessTaskIdDto(Long prosessTaskId) { // NOSONAR Input-dto, ingen behov for initialisering
        this.prosessTaskId = prosessTaskId;
    }

    @ApiModelProperty(required = true, value = "Prosesstask-id for en eksisterende prosesstask")
    public Long getProsessTaskId() {
        return prosessTaskId;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett(); //denne er tom, ProsessTask-API har i praksis rollebasert tilgangskontroll
    }
}
