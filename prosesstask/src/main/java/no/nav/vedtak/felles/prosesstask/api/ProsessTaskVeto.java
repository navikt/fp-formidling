package no.nav.vedtak.felles.prosesstask.api;

import java.util.Objects;

public class ProsessTaskVeto {

    private boolean veto;
    private Long blokkertAvProsessTaskId;
    private Long prosessTaskId;
    private String begrunnelse;

    public ProsessTaskVeto(boolean veto, Long prosessTaskId) {
        this(veto, prosessTaskId, null, null);
    }

    public ProsessTaskVeto(boolean veto, Long prosessTaskId, Long blokkertAvProsessTaskId) {
        this(veto, prosessTaskId, blokkertAvProsessTaskId, null);
    }

    public ProsessTaskVeto(boolean veto, Long prosessTaskId, Long blokkertAvProsessTaskId, String begrunnelse) {
        this.begrunnelse = begrunnelse;
        Objects.requireNonNull(prosessTaskId, "prosessTaskId");
        this.veto = veto;
        this.prosessTaskId = prosessTaskId;
        this.blokkertAvProsessTaskId = blokkertAvProsessTaskId;
    }

    public Long getBlokkertAvProsessTaskId() {
        return blokkertAvProsessTaskId;
    }

    public Long getProsessTaskId() {
        return prosessTaskId;
    }

    public boolean isVeto() {
        return veto;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }
}
