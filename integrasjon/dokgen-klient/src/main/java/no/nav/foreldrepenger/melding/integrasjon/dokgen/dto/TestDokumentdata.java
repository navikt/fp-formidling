package no.nav.foreldrepenger.melding.integrasjon.dokgen.dto;

public class TestDokumentdata extends Dokumentdata {
    private String ytelse;
    private String hjemler;
    private boolean enBoolean1;
    private boolean enBoolean2;

    public TestDokumentdata(FellesDokumentdata felles, String ytelse, String hjemler, boolean enBoolean1, boolean enBoolean2) {
        this.felles = felles;
        this.ytelse = ytelse;
        this.hjemler = hjemler;
        this.enBoolean1 = enBoolean1;
        this.enBoolean2 = enBoolean2;
    }

    public String getYtelse() {
        return ytelse;
    }

    public String getHjemler() {
        return hjemler;
    }

    public boolean isEnBoolean1() {
        return enBoolean1;
    }

    public boolean isEnBoolean2() {
        return enBoolean2;
    }
}
