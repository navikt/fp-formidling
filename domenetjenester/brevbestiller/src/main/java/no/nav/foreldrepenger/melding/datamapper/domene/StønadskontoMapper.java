package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.melding.uttak.StønadskontoType.MØDREKVOTE;

import java.math.BigInteger;
import java.util.Optional;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

public class StønadskontoMapper {

    public static BigInteger finnDisponibleDager(Behandling behandling, Boolean aleneOmsorg, Boolean annenForelderHarRett, Saldoer saldoer) {
        if (aleneOmsorg || (!annenForelderHarRett)) {
            return finnSaldo(saldoer, FORELDREPENGER);
        }
        boolean forMor = RelasjonsRolleType.MORA.equals(behandling.getRelasjonsRolleType());
        return forMor ? finnSaldo(saldoer, MØDREKVOTE) : finnSaldo(saldoer, FEDREKVOTE);
    }

    private static BigInteger finnSaldo(Saldoer saldoer, StønadskontoType stønadskontoType) {
        return BigInteger.valueOf(PeriodeBeregner.finnStønadsKontoMedType(saldoer.getStønadskontoer(), stønadskontoType).map(Stønadskonto::getSaldo).orElse(0));
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
        return finnSaldo(saldoer, StønadskontoType.FELLESPERIODE);
    }

    public static Optional<BigInteger> finnForeldrepengeperiodenUtvidetUkerHvisFinnes(Saldoer saldoer) {
        return PeriodeBeregner.finnStønadsKontoMedType(saldoer.getStønadskontoer(), StønadskontoType.FLERBARNSDAGER)
                .map(Stønadskonto::getMaxDager)
                .map(BigInteger::valueOf)
                .map(dager -> dager.divide(BigInteger.valueOf(5)));

    }
}
