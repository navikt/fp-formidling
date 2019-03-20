package no.nav.foreldrepenger.melding.brevbestiller.api.dto;

import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;

public class Klage {

    KlageFormkravResultat formkravKA;
    KlageFormkravResultat formkravNFP;

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
        return klage;
    }

    public KlageFormkravResultat getFormkravKA() {
        return formkravKA;
    }

    public KlageFormkravResultat getFormkravNFP() {
        return formkravNFP;
    }
}
