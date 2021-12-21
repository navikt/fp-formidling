package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.MØDREKVOTE;

import java.math.BigInteger;

import no.nav.foreldrepenger.melding.brevmapper.brev.felles.PeriodeBeregner;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;

public final class StønadskontoMapper {
    public static int finnDisponibleDager(Saldoer saldoer, RelasjonsRolleType rolleType) {
        int saldoForeldrepenger = finnSaldo(saldoer, FORELDREPENGER);

        if (saldoForeldrepenger != 0) {
            return saldoForeldrepenger;
        }

        return RelasjonsRolleType.MORA.equals(rolleType) ? finnSaldo(saldoer, MØDREKVOTE) : finnSaldo(saldoer, FEDREKVOTE);
    }

    public static int finnSaldo(Saldoer saldoer, StønadskontoType stønadskontoType) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer.stønadskontoer(), stønadskontoType).map(sk -> Math.max(sk.saldo(), 0)).orElse(0);
    }

    public static int finnDisponibleFellesDager(Saldoer saldoer) {
        return finnSaldo(saldoer, StønadskontoType.FELLESPERIODE);
    }

    public static int finnForeldrepengeperiodenUtvidetUkerHvisFinnes(Saldoer saldoer) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer.stønadskontoer(), StønadskontoType.FLERBARNSDAGER)
                .map(Stønadskonto::maxDager)
                .map(BigInteger::valueOf)
                .map(dager -> dager.divide(BigInteger.valueOf(5)))
                .map(BigInteger::intValue).orElse(0);
    }

    public static Integer finnPrematurDagerHvisFinnes(Saldoer saldoer) {
        return saldoer.stønadskontoer()
                .stream()
                .map(Stønadskonto::prematurDager)
                .max(Integer::compareTo).orElse(null);
    }
}
