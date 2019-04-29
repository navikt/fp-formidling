package no.nav.foreldrepenger.melding.dtomapper;

import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.dto.uttak.saldo.SaldoerDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.saldo.StønadskontoDto;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;

@ApplicationScoped
public class StønadskontoDtoMapper {

    private KodeverkRepository kodeverkRepository;

    @Inject
    public StønadskontoDtoMapper(KodeverkRepository kodeverkRepository) {
        this.kodeverkRepository = kodeverkRepository;
    }

    public StønadskontoDtoMapper() {
        //CDI
    }

    public Saldoer mapSaldoerFraDto(SaldoerDto dto) {
        return new Saldoer(dto.getStonadskontoer().values()
                .stream()
                .map(this::mapStønadskontoFradto)
                .collect(Collectors.toSet()));
    }

    Stønadskonto mapStønadskontoFradto(StønadskontoDto dto) {
        return new Stønadskonto(dto.getMaxDager(), kodeverkRepository.finn(StønadskontoType.class, dto.getStonadskontotype()), dto.getSaldo());
    }

}
