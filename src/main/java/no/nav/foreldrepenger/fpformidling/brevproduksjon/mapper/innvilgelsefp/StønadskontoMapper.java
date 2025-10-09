package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag.ForeldrepengerUttak;

import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.PeriodeBeregner;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;

public final class StønadskontoMapper {

    private StønadskontoMapper() {
    }

    public static int finnDisponibleDager(List<ForeldrepengerUttak.Stønadskonto> saldoer, BrevGrunnlag.RelasjonsRolleType rolleType) {
        var saldoForeldrepenger = finnSaldo(saldoer, ForeldrepengerUttak.Stønadskonto.Type.FORELDREPENGER);

        if (saldoForeldrepenger != 0) {
            return saldoForeldrepenger;
        }

        return BrevGrunnlag.RelasjonsRolleType.MORA.equals(rolleType) ? finnSaldo(saldoer, ForeldrepengerUttak.Stønadskonto.Type.MØDREKVOTE) : finnSaldo(saldoer,
            ForeldrepengerUttak.Stønadskonto.Type.FEDREKVOTE);
    }

    public static int finnSaldo(List<ForeldrepengerUttak.Stønadskonto> saldoer, ForeldrepengerUttak.Stønadskonto.Type stønadskontoType) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer, stønadskontoType).map(sk -> Math.max(sk.saldo(), 0)).orElse(0);
    }

    public static int finnMaksdager(List<ForeldrepengerUttak.Stønadskonto> saldoer, ForeldrepengerUttak.Stønadskonto.Type stønadskontoType) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer, stønadskontoType).map(ForeldrepengerUttak.Stønadskonto::maxDager).orElse(0);
    }

    public static int finnDisponibleFellesDager(List<ForeldrepengerUttak.Stønadskonto> saldoer) {
        return finnSaldo(saldoer, ForeldrepengerUttak.Stønadskonto.Type.FELLESPERIODE);
    }

    public static boolean kontoEksisterer(List<ForeldrepengerUttak.Stønadskonto> saldoer, ForeldrepengerUttak.Stønadskonto.Type stønadskontoType) {
        return saldoer.stream().anyMatch(stønadskonto -> Objects.equals(stønadskonto.stønadskontotype(), stønadskontoType));
    }

    public static int finnFlerbarnsdagerUtvidetUkerHvisFinnes(List<ForeldrepengerUttak.Stønadskonto> saldoer) {
        return dagerFlerbarnsdager(saldoer) / 5;
    }

    public static int finnFlerbarnsdagerUtvidetDagerHvisFinnes(List<ForeldrepengerUttak.Stønadskonto> saldoer) {
        return dagerFlerbarnsdager(saldoer) % 5;
    }

    private static int dagerFlerbarnsdager(List<ForeldrepengerUttak.Stønadskonto> saldoer) {
        return saldoer.stream().map(k -> k.kontoUtvidelser().flerbarnsdager()).filter(flerbarnsdager -> flerbarnsdager > 0).findFirst().orElse(0);
    }

    public static Integer finnPrematurDagerHvisFinnes(List<ForeldrepengerUttak.Stønadskonto> saldoer) {
        return saldoer.stream().map(k -> k.kontoUtvidelser().prematurdager()).max(Integer::compareTo).orElse(null);
    }
}
