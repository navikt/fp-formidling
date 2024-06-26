package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.SaldoVisningStønadskontoType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Stønadskonto;

public final class StønadskontoMapper {

    private StønadskontoMapper() {
    }

    public static int finnDisponibleDager(Saldoer saldoer, RelasjonsRolleType rolleType) {
        var saldoForeldrepenger = finnSaldo(saldoer, SaldoVisningStønadskontoType.FORELDREPENGER);

        if (saldoForeldrepenger != 0) {
            return saldoForeldrepenger;
        }

        return RelasjonsRolleType.MORA.equals(rolleType) ? finnSaldo(saldoer, SaldoVisningStønadskontoType.MØDREKVOTE) : finnSaldo(saldoer,
            SaldoVisningStønadskontoType.FEDREKVOTE);
    }

    public static int finnSaldo(Saldoer saldoer, SaldoVisningStønadskontoType stønadskontoType) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer.stønadskontoer(), stønadskontoType).map(sk -> Math.max(sk.saldo(), 0)).orElse(0);
    }

    public static int finnDisponibleFellesDager(Saldoer saldoer) {
        return finnSaldo(saldoer, SaldoVisningStønadskontoType.FELLESPERIODE);
    }

    public static boolean kontoEksisterer(Saldoer saldoer, SaldoVisningStønadskontoType stønadskontoType) {
        return saldoer.stønadskontoer().stream().anyMatch(stønadskonto -> Objects.equals(stønadskonto.stønadskontoType(), stønadskontoType));
    }

    public static int finnFlerbarnsdagerUtvidetUkerHvisFinnes(Saldoer saldoer) {
        return dagerFlerbarnsdager(saldoer) / 5;
    }

    public static int finnFlerbarnsdagerUtvidetDagerHvisFinnes(Saldoer saldoer) {
        return dagerFlerbarnsdager(saldoer) % 5;
    }

    private static int dagerFlerbarnsdager(Saldoer saldoer) {
        return saldoer.stønadskontoer().stream().map(Stønadskonto::flerbarnsDager).filter(flerbarnsdager -> flerbarnsdager > 0).findFirst().orElse(0);
    }

    public static Integer finnPrematurDagerHvisFinnes(Saldoer saldoer) {
        return saldoer.stønadskontoer().stream().map(Stønadskonto::prematurDager).max(Integer::compareTo).orElse(null);
    }
}
