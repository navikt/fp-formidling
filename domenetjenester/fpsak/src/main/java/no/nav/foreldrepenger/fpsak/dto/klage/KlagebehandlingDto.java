package no.nav.foreldrepenger.fpsak.dto.klage;

public class KlagebehandlingDto {

    private KlageVurderingResultatDto klageVurderingResultatNFP;
    private KlageVurderingResultatDto klageVurderingResultatNK;
    private KlageFormkravResultatDto klageFormkravResultatNFP;
    private KlageFormkravResultatDto klageFormkravResultatKA;


    public KlagebehandlingDto() {
        // trengs for deserialisering av JSON
    }

    public KlageVurderingResultatDto getKlageVurderingResultatNFP() {
        return klageVurderingResultatNFP;
    }

    void setKlageVurderingResultatNFP(KlageVurderingResultatDto klageVurderingResultatNFP) {
        this.klageVurderingResultatNFP = klageVurderingResultatNFP;
    }

    public KlageVurderingResultatDto getKlageVurderingResultatNK() {
        return klageVurderingResultatNK;
    }

    void setKlageVurderingResultatNK(KlageVurderingResultatDto klageVurderingResultatNK) {
        this.klageVurderingResultatNK = klageVurderingResultatNK;
    }

    public KlageFormkravResultatDto getKlageFormkravResultatNFP() {
        return klageFormkravResultatNFP;
    }

    void setKlageFormkravResultatNFP(KlageFormkravResultatDto klageFormkravResultatNFP) {
        this.klageFormkravResultatNFP = klageFormkravResultatNFP;
    }

    public KlageFormkravResultatDto getKlageFormkravResultatKA() {
        return klageFormkravResultatKA;
    }

    void setKlageFormkravResultatKA(KlageFormkravResultatDto klageFormkravResultatKA) {
        this.klageFormkravResultatKA = klageFormkravResultatKA;
    }
}
