package no.nav.foreldrepenger.melding.dtomapper;

import static no.nav.foreldrepenger.melding.behandling.Behandlingsresultat.builder;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingsresultatDto;
import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.vedtak.Vedtaksbrev;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;

@ApplicationScoped
public class BehandlingsresultatDtoMapper {

    private KodeverkRepository kodeverkRepository;

    public BehandlingsresultatDtoMapper() {
        //CDI
    }

    @Inject
    public BehandlingsresultatDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public Behandlingsresultat mapBehandlingsresultatFraDto(BehandlingsresultatDto dto) {
        Behandlingsresultat.Builder builder = builder();
        if (dto.getAvslagsarsak() != null) {
            builder.medAvslagsårsak(kodeverkRepository.finn(Avslagsårsak.class, dto.getAvslagsarsak().getKode()));
        }
        if (dto.getType() != null) {
            //Enum i stedet for å hente fra kodeliste
            builder.medBehandlingResultatType(BehandlingResultatType.fraKode(dto.getType().getKode()));
        }
        builder.medFritekstbrev(dto.getFritekstbrev())
                .medOverskrift(dto.getOverskrift())
                .medVedtaksbrev(kodeverkRepository.finn(Vedtaksbrev.class, dto.getVedtaksbrev().getKode()))
                .medAvslagarsakFritekst(dto.getAvslagsarsakFritekst());
        List<KonsekvensForYtelsen> konsekvenserForYtelsen = new ArrayList<>();
        for (KodeDto kodeDto : dto.getKonsekvenserForYtelsen()) {
            konsekvenserForYtelsen.add(kodeverkRepository.finn(KonsekvensForYtelsen.class, kodeDto.getKode()));
        }
        builder.medKonsekvenserForYtelsen(konsekvenserForYtelsen);
        builder.medErRevurderingMedUendretUtfall(dto.getErRevurderingMedUendretUtfall());
        return builder.build();
    }
}
