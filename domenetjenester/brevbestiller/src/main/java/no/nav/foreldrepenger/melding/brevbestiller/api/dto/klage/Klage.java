package no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage;

import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;

public class Klage {

    private KlageVurderingResultat klageVurderingResultatNFP;
    private KlageVurderingResultat klageVurderingResultatNK;
    private KlageFormkravResultat formkravKA;
    private KlageFormkravResultat formkravNFP;

    private Klage() {
    }

    public static Klage fraDto(KlagebehandlingDto dto) {
        Klage klage = new Klage();
        if (dto.getKlageFormkravResultatNFP() != null) {
            klage.formkravNFP = KlageFormkravResultat.fraDto(dto.getKlageFormkravResultatNFP());
        }
        if (dto.getKlageFormkravResultatKA() != null) {
            klage.formkravKA = KlageFormkravResultat.fraDto(dto.getKlageFormkravResultatKA());
        }
        if (dto.getKlageVurderingResultatNFP() != null) {
            klage.klageVurderingResultatNFP = KlageVurderingResultat.fraDto(dto.getKlageVurderingResultatNFP());
        }
        if (dto.getKlageVurderingResultatNK() != null) {
            klage.klageVurderingResultatNK = KlageVurderingResultat.fraDto(dto.getKlageVurderingResultatNK());
        }
        return klage;
    }

    public KlageFormkravResultat getFormkravKA() {
        return formkravKA;
    }

    public KlageFormkravResultat getFormkravNFP() {
        return formkravNFP;
    }

    public KlageVurderingResultat getKlageVurderingResultatNFP() {
        return klageVurderingResultatNFP;
    }

    public KlageVurderingResultat getKlageVurderingResultatNK() {
        return klageVurderingResultatNK;
    }
}
