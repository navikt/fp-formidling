package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.KlageRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.melding.behandling.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.klage.Klage;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.vedtak.util.StringUtils;

@ApplicationScoped
public class KlageMapper {

    private KodeverkRepository kodeverkRepository;
    private KlageRestKlient klageRestKlient;

    public KlageMapper() {
        //CDO
    }

    @Inject
    public KlageMapper(KodeverkRepository kodeverkRepository,
                       KlageRestKlient klageRestKlient) {
        this.kodeverkRepository = kodeverkRepository;
        this.klageRestKlient = klageRestKlient;
    }


    public Klage hentKlagebehandling(Behandling behandling) {
        Optional<KlagebehandlingDto> klagebehandlingDto = klageRestKlient.hentKlagebehandling(new BehandlingIdDto(behandling.getId()));
        if (!klagebehandlingDto.isPresent()) {
            throw new IllegalStateException("Finner ikke klagebehandling");
        }
        return Klage.fraDto(klagebehandlingDto.get());
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
