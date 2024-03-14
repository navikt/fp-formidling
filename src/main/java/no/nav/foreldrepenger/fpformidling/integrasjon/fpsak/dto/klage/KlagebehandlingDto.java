package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
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

    public void setKlageVurderingResultatNFP(KlageVurderingResultatDto klageVurderingResultatNFP) {
        this.klageVurderingResultatNFP = klageVurderingResultatNFP;
    }

    public KlageVurderingResultatDto getKlageVurderingResultatNK() {
        return klageVurderingResultatNK;
    }

    public void setKlageVurderingResultatNK(KlageVurderingResultatDto klageVurderingResultatNK) {
        this.klageVurderingResultatNK = klageVurderingResultatNK;
    }

    public KlageFormkravResultatDto getKlageFormkravResultatNFP() {
        return klageFormkravResultatNFP;
    }

    public void setKlageFormkravResultatNFP(KlageFormkravResultatDto klageFormkravResultatNFP) {
        this.klageFormkravResultatNFP = klageFormkravResultatNFP;
    }

    public KlageFormkravResultatDto getKlageFormkravResultatKA() {
        return klageFormkravResultatKA;
    }

    public void setKlageFormkravResultatKA(KlageFormkravResultatDto klageFormkravResultatKA) {
        this.klageFormkravResultatKA = klageFormkravResultatKA;
    }
}
