package no.nav.foreldrepenger.fpsak.dto.klage;

public class KlageVurderingResultatDto {
    private String klageVurdering;
    private String begrunnelse;
    private String fritekstTilBrev;
    private String klageMedholdArsak;
    private String klageMedholdArsakNavn;
    private String klageVurderingOmgjoer;
    private String klageVurdertAv;

    public KlageVurderingResultatDto() {
    }

    public String getKlageVurdering() {
        return klageVurdering;
    }

    public void setKlageVurdering(String klageVurdering) {
        this.klageVurdering = klageVurdering;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public void setFritekstTilBrev(String fritekstTilBrev) {
        this.fritekstTilBrev = fritekstTilBrev;
    }

    public String getKlageMedholdArsak() {
        return klageMedholdArsak;
    }

    public void setKlageMedholdArsak(String klageMedholdArsak) {
        this.klageMedholdArsak = klageMedholdArsak;
    }

    public String getKlageVurdertAv() {
        return klageVurdertAv;
    }

    public void setKlageVurdertAv(String klageVurdertAv) {
        this.klageVurdertAv = klageVurdertAv;
    }

    public String getKlageMedholdArsakNavn() {
        return klageMedholdArsakNavn;
    }

    public void setKlageMedholdArsakNavn(String klageMedholdArsakNavn) {
        this.klageMedholdArsakNavn = klageMedholdArsakNavn;
    }

    public String getKlageVurderingOmgjoer() {
        return klageVurderingOmgjoer;
    }

    public void setKlageVurderingOmgjoer(String klageVurderingOmgjoer) {
        this.klageVurderingOmgjoer = klageVurderingOmgjoer;
    }
}
