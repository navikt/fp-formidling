package no.nav.vedtak.felles.prosesstask.api;

/**
 * Utvalgte read-only attributter for en prosesstasktype
 */
public class ProsessTaskTypeInfo {

    private String kode;
    private int maksForsøk;

    public ProsessTaskTypeInfo(String kode, int maksForsøk) {
        this.kode = kode;
        this.maksForsøk = maksForsøk;
    }

    public String getKode() {
        return kode;
    }

    public int getMaksForsøk() {
        return maksForsøk;
    }
}
