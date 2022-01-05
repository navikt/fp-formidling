package no.nav.foreldrepenger.fpsak.mapper;

import java.util.stream.Collectors;

import no.nav.foreldrepenger.fpformidling.uttak.Saldoer;
import no.nav.foreldrepenger.fpformidling.uttak.Stønadskonto;
import no.nav.foreldrepenger.fpformidling.uttak.StønadskontoType;
import no.nav.foreldrepenger.fpsak.dto.uttak.saldo.SaldoerDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.saldo.StønadskontoDto;

public class StønadskontoDtoMapper {

    public static Saldoer mapSaldoerFraDto(SaldoerDto dto) {
        return new Saldoer(dto.getStonadskontoer().values()
                .stream()
                .map(StønadskontoDtoMapper::mapStønadskontoFradto)
                .collect(Collectors.toSet()), dto.getTapteDagerFpff());
    }

    static Stønadskonto mapStønadskontoFradto(StønadskontoDto dto) {
        return new Stønadskonto(dto.getMaxDager(), StønadskontoType.fraKode(dto.getStonadskontotype()), dto.getSaldo(), mapPrematurDager(dto), mapFlerbarnsdager(dto));
    }

    private static int mapPrematurDager(StønadskontoDto dto) {
        return dto.getKontoUtvidelser() == null ? 0 : dto.getKontoUtvidelser().getPrematurdager();
    }

    private static int mapFlerbarnsdager(StønadskontoDto dto) {
        return dto.getKontoUtvidelser() == null ? 0 : dto.getKontoUtvidelser().getFlerbarnsdager();
    }

}
