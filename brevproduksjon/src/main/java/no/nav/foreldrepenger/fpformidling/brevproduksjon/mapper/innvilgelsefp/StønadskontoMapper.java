package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import java.math.BigInteger;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.uttak.SaldoVisningStønadskontoType;
import no.nav.foreldrepenger.fpformidling.uttak.Saldoer;
import no.nav.foreldrepenger.fpformidling.uttak.Stønadskonto;

public final class StønadskontoMapper {
    public static int finnDisponibleDager(Saldoer saldoer, RelasjonsRolleType rolleType) {
        int saldoForeldrepenger = finnSaldo(saldoer, SaldoVisningStønadskontoType.FORELDREPENGER);

        if (saldoForeldrepenger != 0) {
            return saldoForeldrepenger;
        }

        return RelasjonsRolleType.MORA.equals(rolleType) ? finnSaldo(saldoer, SaldoVisningStønadskontoType.MØDREKVOTE)
                : finnSaldo(saldoer, SaldoVisningStønadskontoType.FEDREKVOTE);
    }

    public static int finnSaldo(Saldoer saldoer, SaldoVisningStønadskontoType stønadskontoType) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer.stønadskontoer(), stønadskontoType).map(sk -> Math.max(sk.saldo(), 0)).orElse(0);
    }

    public static int finnDisponibleFellesDager(Saldoer saldoer) {
        return finnSaldo(saldoer, SaldoVisningStønadskontoType.FELLESPERIODE);
    }

    public static boolean kontoEksisterer(Saldoer saldoer, SaldoVisningStønadskontoType stønadskontoType) {
        return saldoer.stønadskontoer().stream()
                .anyMatch(stønadskonto -> Objects.equals(stønadskonto.stønadskontoType(), stønadskontoType));
    }

    public static int finnForeldrepengeperiodenUtvidetUkerHvisFinnes(Saldoer saldoer) {
        return saldoer.stønadskontoer().stream()
                .map(s -> s.flerbarnsDager())
                .filter(flerbarnsdager -> flerbarnsdager > 0)
                .map(BigInteger::valueOf)
                .map(dager -> dager.divide(BigInteger.valueOf(5)))
                .map(BigInteger::intValue)
                .findFirst().orElse(0);
    }

    public static Integer finnPrematurDagerHvisFinnes(Saldoer saldoer) {
        return saldoer.stønadskontoer()
                .stream()
                .map(Stønadskonto::prematurDager)
                .max(Integer::compareTo).orElse(null);
    }
}
