package no.nav.vedtak.felles.prosesstask.rest.dto;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class ProsessTaskEndreStatusInputDto implements AbacDto {

    private ProsessTaskStatusDto nyStatus;
    private ProsessTaskStatusDto nåværendeStatus;
    private String tasktype;
    private Long prosessTaskId;

    public Long getProsessTaskId() {
        return prosessTaskId;
    }

    public void setProsessTaskId(Long prosessTaskId) {
        this.prosessTaskId = prosessTaskId;
    }

    public ProsessTaskStatusDto getNyStatus() {
        return nyStatus;
    }

    public void setNyStatus(ProsessTaskStatusDto nyStatus) {
        this.nyStatus = nyStatus;
    }

    public ProsessTaskStatusDto getNåværendeStatus() {
        return nåværendeStatus;
    }

    public void setNåværendeStatus(ProsessTaskStatusDto nåværendeStatus) {
        this.nåværendeStatus = nåværendeStatus;
    }

    public String getTasktype() {
        return tasktype;
    }

    public void setTasktype(String tasktype) {
        this.tasktype = tasktype;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
