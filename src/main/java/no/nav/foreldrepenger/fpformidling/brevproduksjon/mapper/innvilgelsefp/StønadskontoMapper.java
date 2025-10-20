package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

public final class StønadskontoMapper {

    private StønadskontoMapper() {
    }

    public static int finnDisponibleDager(List<Foreldrepenger.Stønadskonto> saldoer, BrevGrunnlagDto.RelasjonsRolleType rolleType) {
        var saldoForeldrepenger = finnSaldo(saldoer, Foreldrepenger.Stønadskonto.Type.FORELDREPENGER);

        if (saldoForeldrepenger != 0) {
            return saldoForeldrepenger;
        }

        return BrevGrunnlagDto.RelasjonsRolleType.MORA.equals(rolleType) ? finnSaldo(saldoer,
            Foreldrepenger.Stønadskonto.Type.MØDREKVOTE) : finnSaldo(saldoer, Foreldrepenger.Stønadskonto.Type.FEDREKVOTE);
    }

    public static int finnSaldo(List<Foreldrepenger.Stønadskonto> saldoer, Foreldrepenger.Stønadskonto.Type stønadskontoType) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer, stønadskontoType).map(sk -> Math.max(sk.saldo(), 0)).orElse(0);
    }

    public static int finnMaksdager(List<Foreldrepenger.Stønadskonto> saldoer, Foreldrepenger.Stønadskonto.Type stønadskontoType) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer, stønadskontoType).map(Foreldrepenger.Stønadskonto::maxDager).orElse(0);
    }

    public static int finnDisponibleFellesDager(List<Foreldrepenger.Stønadskonto> saldoer) {
        return finnSaldo(saldoer, Foreldrepenger.Stønadskonto.Type.FELLESPERIODE);
    }

    public static boolean kontoEksisterer(List<Foreldrepenger.Stønadskonto> saldoer, Foreldrepenger.Stønadskonto.Type stønadskontoType) {
        return saldoer.stream().anyMatch(stønadskonto -> Objects.equals(stønadskonto.stønadskontotype(), stønadskontoType));
    }

    public static int finnFlerbarnsdagerUtvidetUkerHvisFinnes(List<Foreldrepenger.Stønadskonto> saldoer) {
        return dagerFlerbarnsdager(saldoer) / 5;
    }

    public static int finnFlerbarnsdagerUtvidetDagerHvisFinnes(List<Foreldrepenger.Stønadskonto> saldoer) {
        return dagerFlerbarnsdager(saldoer) % 5;
    }

    private static int dagerFlerbarnsdager(List<Foreldrepenger.Stønadskonto> saldoer) {
        return saldoer.stream()
            .flatMap(k -> Optional.ofNullable(k.kontoUtvidelser()).stream())
            .map(Foreldrepenger.KontoUtvidelser::flerbarnsdager)
            .max(Integer::compareTo)
            .orElse(0);
    }

    public static int finnPrematurDagerHvisFinnes(List<Foreldrepenger.Stønadskonto> saldoer) {
        return saldoer.stream()
            .flatMap(k -> Optional.ofNullable(k.kontoUtvidelser()).stream())
            .map(Foreldrepenger.KontoUtvidelser::prematurdager)
            .max(Integer::compareTo)
            .orElse(0);
    }
}
