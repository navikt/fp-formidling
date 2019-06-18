package no.nav.vedtak.felles.prosesstask.api;

import java.math.BigDecimal;

public class TaskStatus {
    private ProsessTaskStatus status;
    private BigDecimal antall;

    public TaskStatus(ProsessTaskStatus status, BigDecimal antall) {
        this.status = status;
        this.antall = antall;
    }

    public BigDecimal getAntall() {
        return antall;
    }

    public ProsessTaskStatus getStatus() {
        return status;
    }
}
