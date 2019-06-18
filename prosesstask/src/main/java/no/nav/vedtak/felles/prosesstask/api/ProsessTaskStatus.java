package no.nav.vedtak.felles.prosesstask.api;

public enum ProsessTaskStatus {

    KLAR("KLAR"), //$NON-NLS-1$
    FERDIG("FERDIG"), //$NON-NLS-1$
    VENTER_SVAR("VENTER_SVAR"), //$NON-NLS-1$
    VETO("VETO"), //$NON-NLS-1$
    SUSPENDERT("SUSPENDERT"), //$NON-NLS-1$
    FEILET("FEILET") //$NON-NLS-1$
    ;
    private String dbKode;

    ProsessTaskStatus(String dbKode) {
        this.dbKode = dbKode;
    }

    public String getDbKode() {
        return dbKode;
    }

    @Override
    public String toString() {
        return getDbKode();
    }
}
