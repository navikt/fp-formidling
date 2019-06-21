package no.nav.vedtak.felles.prosesstask.rest.dto;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class ProsessTaskEndreStatusInputDto implements AbacDto {

    private ProsessTaskStatusDto prosessTaskStatusDto;
    private Long prosessTaskId;

    public Long getProsessTaskId() {
        return prosessTaskId;
    }

    public void setProsessTaskId(Long prosessTaskId) {
        this.prosessTaskId = prosessTaskId;
    }

    public ProsessTaskStatusDto getProsessTaskStatusDto() {
        return prosessTaskStatusDto;
    }

    public void setProsessTaskStatusDto(ProsessTaskStatusDto prosessTaskStatusDto) {
        this.prosessTaskStatusDto = prosessTaskStatusDto;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
