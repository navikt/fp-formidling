package no.nav.foreldrepenger.melding.datamapper.domene;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.Klage;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class KlageMapper {

    private KodeverkRepository kodeverkRepository;

    public KlageMapper() {
        //CDO
    }

    @Inject
    public KlageMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public boolean erOpphevet(Klage klage) {
        String klageVurdering = null;
        if (klage.getKlageVurderingResultatNFP() != null) {
            klageVurdering = klage.getKlageVurderingResultatNFP().getKlageVurdering();
        } else if (klage.getKlageVurderingResultatNK() != null) {
            klageVurdering = klage.getKlageVurderingResultatNK().getKlageVurdering();
        }
        if (StringUtils.nullOrEmpty(klageVurdering)) {
            throw new IllegalStateException();
        }
        return kodeverkRepository.finn(KlageVurdering.class, klageVurdering).equals(KlageVurdering.OPPHEVE_YTELSESVEDTAK);
    }

}