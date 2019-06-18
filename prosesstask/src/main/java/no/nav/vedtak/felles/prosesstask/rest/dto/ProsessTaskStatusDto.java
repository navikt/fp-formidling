package no.nav.vedtak.felles.prosesstask.rest.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import no.nav.vedtak.util.InputValideringRegex;

@ApiModel
public class ProsessTaskStatusDto {
    @NotNull
    @Size(min = 1, max = 100)
    @Pattern(regexp = InputValideringRegex.KODEVERK)
    private String prosessTaskStatusName;

    public ProsessTaskStatusDto() {
    }

    public ProsessTaskStatusDto(String prosessTaskStatusName) {
        this.prosessTaskStatusName = prosessTaskStatusName;
    }

    @ApiModelProperty(required = true, value = "Navn på prosesstask-status")
    public String getProsessTaskStatusName() {
        return prosessTaskStatusName;
    }
}
