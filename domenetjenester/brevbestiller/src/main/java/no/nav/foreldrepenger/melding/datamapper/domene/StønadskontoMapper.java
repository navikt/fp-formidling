package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.MØDREKVOTE;

import java.math.BigInteger;
import java.util.Optional;

import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

public final class StønadskontoMapper {

    private StønadskontoMapper() {
    }

    public static BigInteger finnDisponibleDager(Saldoer saldoer, RelasjonsRolleType rolleType) {
        Optional<BigInteger> saldoForeldrepenger = finnSaldo(saldoer, FORELDREPENGER);
        if (saldoForeldrepenger.isPresent()) {
            return saldoForeldrepenger.get();
        }

        boolean søkerErMor = RelasjonsRolleType.MORA.equals(rolleType);
        Optional<BigInteger> saldoKvote = søkerErMor ? finnSaldo(saldoer, MØDREKVOTE) : finnSaldo(saldoer, FEDREKVOTE);
        return saldoKvote.orElse(BigInteger.ZERO);
    }

    private static Optional<BigInteger> finnSaldo(Saldoer saldoer, StønadskontoType stønadskontoType) {
        Optional<Stønadskonto> stønadskonto = PeriodeBeregner.finnStønadsKontoMedType(saldoer.getStønadskontoer(), stønadskontoType);
        if (stønadskonto.isPresent()) {
            int saldo = stønadskonto.get().getSaldo();
            return Optional.of(saldo > 0 ? BigInteger.valueOf(saldo) : BigInteger.valueOf(0));
        }
        return Optional.empty();
    }

    public static BigInteger finnTapteDagerFørTermin(UttakResultatPerioder uttakResultatPerioder, Saldoer saldoer, FamilieHendelse familieHendelse) {
        if (fødtFørTermin(familieHendelse)) {
            Optional<Stønadskonto> foreldrepengerFørFødselKonto = saldoer.getStønadskontoer().stream()
                    .filter(konto -> StønadskontoType.FORELDREPENGER_FØR_FØDSEL.equals(konto.getStønadskontoType()))
                    .findFirst();
            return BigInteger.valueOf(PeriodeBeregner.beregnTapteDagerFørTermin(uttakResultatPerioder.getPerioder(), foreldrepengerFørFødselKonto));
        }
        return BigInteger.valueOf(0);
    }

    private static boolean fødtFørTermin(FamilieHendelse familieHendelse) {
        if (!FamilieHendelseType.FØDSEL.equals(familieHendelse.getFamilieHendelseType())) {
            return false;
        }
        if (familieHendelse.getFødselsdato().isPresent() && familieHendelse.getTermindato().isPresent()) {
            return familieHendelse.getFødselsdato().get().isBefore(familieHendelse.getTermindato().get());
        }
        return false;
    }

    public static BigInteger finnDisponibleFellesDager(Saldoer saldoer) {
        return finnSaldo(saldoer, StønadskontoType.FELLESPERIODE).orElse(BigInteger.ZERO);
    }

    public static Optional<BigInteger> finnForeldrepengeperiodenUtvidetUkerHvisFinnes(Saldoer saldoer) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer.getStønadskontoer(), StønadskontoType.FLERBARNSDAGER)
                .map(Stønadskonto::getMaxDager)
                .map(BigInteger::valueOf)
                .map(dager -> dager.divide(BigInteger.valueOf(5)));
    }

    public static Optional<Integer> finnPrematurDagerHvisFinnes(Saldoer saldoer) {
        return saldoer.getStønadskontoer()
                .stream()
                .map(Stønadskonto::getPrematurDager)
                .max(Integer::compareTo);
    }
}
