package no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.fpsak.dto.klage.KlageFormkravResultatDto;
import no.nav.foreldrepenger.melding.behandling.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;

public class KlageFormkravResultat {

    private String begrunnelse;
    private List<KlageAvvistÅrsak> avvistÅrsaker = new ArrayList<>();

    private KlageFormkravResultat() {

    }

    public static KlageFormkravResultat fraDto(KlageFormkravResultatDto dto, KodeverkRepository kodeverkRepository) {
        KlageFormkravResultat klageFormkravResultat = new KlageFormkravResultat();
        klageFormkravResultat.begrunnelse = dto.getBegrunnelse();
        dto.getAvvistArsaker().forEach(årsak -> klageFormkravResultat.avvistÅrsaker.add(kodeverkRepository.finn(KlageAvvistÅrsak.class, årsak.kode)));
        return klageFormkravResultat;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public List<KlageAvvistÅrsak> getAvvistÅrsaker() {
        return avvistÅrsaker;
    }
}
