package no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage;

import no.nav.foreldrepenger.fpsak.dto.klage.KlageFormkravResultatDto;

public class KlageFormkravResultat {

    private String begrunnelse;

    private KlageFormkravResultat() {

    }

    public static KlageFormkravResultat fraDto(KlageFormkravResultatDto dto) {
        KlageFormkravResultat klageFormkravResultat = new KlageFormkravResultat();
        klageFormkravResultat.begrunnelse = dto.getBegrunnelse();
        return klageFormkravResultat;
    }
}
