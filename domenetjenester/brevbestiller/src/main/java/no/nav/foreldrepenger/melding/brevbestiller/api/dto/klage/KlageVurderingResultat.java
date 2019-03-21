package no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage;

import no.nav.foreldrepenger.fpsak.dto.klage.KlageVurderingResultatDto;

public class KlageVurderingResultat {

    private String KlageVurdering;

    private KlageVurderingResultat() {
    }

    public static KlageVurderingResultat fraDto(KlageVurderingResultatDto dto) {
        KlageVurderingResultat klageVurderingResultat = new KlageVurderingResultat();
        klageVurderingResultat.KlageVurdering = dto.getKlageVurdering();
        return klageVurderingResultat;
    }

    public String getKlageVurdering() {
        return KlageVurdering;
    }
}
