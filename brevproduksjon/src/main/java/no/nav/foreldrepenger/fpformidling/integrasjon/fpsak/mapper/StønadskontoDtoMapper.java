package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.mapper;

import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.saldo.SaldoerDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.uttak.saldo.StønadskontoDto;
import no.nav.foreldrepenger.fpformidling.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.uttak.fp.Stønadskonto;

public class StønadskontoDtoMapper {

    private StønadskontoDtoMapper() {
    }

    public static Saldoer mapSaldoerFraDto(SaldoerDto dto) {
        return new Saldoer(dto.stonadskontoer().values().stream().map(StønadskontoDtoMapper::mapStønadskontoFradto).collect(Collectors.toSet()),
            dto.tapteDagerFpff());
    }

    private static Stønadskonto mapStønadskontoFradto(StønadskontoDto dto) {
        return new Stønadskonto(dto.maxDager(), dto.stonadskontotype(), dto.saldo(), mapPrematurDager(dto), mapFlerbarnsdager(dto));
    }

    private static int mapPrematurDager(StønadskontoDto dto) {
        return dto.kontoUtvidelser() == null ? 0 : dto.kontoUtvidelser().prematurdager();
    }

    private static int mapFlerbarnsdager(StønadskontoDto dto) {
        return dto.kontoUtvidelser() == null ? 0 : dto.kontoUtvidelser().flerbarnsdager();
    }

}
