package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.uttak.Saldoer;
import no.nav.foreldrepenger.fpformidling.uttak.Stønadskonto;
import no.nav.foreldrepenger.fpformidling.uttak.StønadskontoType;

public class StønadskontoMapperTest {

    @Test
    public void skal_finne_disponible_dager_mødrekvote() {
        Set<Stønadskonto> stønadskontoer = Set.of(new Stønadskonto(10, StønadskontoType.MØDREKVOTE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer, 0), RelasjonsRolleType.MORA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    public void skal_finne_disponible_dager_fellesperiode() {
        Set<Stønadskonto> stønadskontoer = Set.of(new Stønadskonto(10, StønadskontoType.FELLESPERIODE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleFellesDager(new Saldoer(stønadskontoer, 0));
        assertThat(dager).isEqualTo(5);
    }

    @Test
    public void skal_finne_disponible_dager_foreldrepenger() {
        Set<Stønadskonto> stønadskontoer = Set.of(new Stønadskonto(10, StønadskontoType.FORELDREPENGER, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer, 0), RelasjonsRolleType.MORA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    public void skal_finne_disponible_dager_fedrekvote() {
        Set<Stønadskonto> stønadskontoer = Set.of(new Stønadskonto(10, StønadskontoType.FEDREKVOTE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer, 0), RelasjonsRolleType.FARA);
        assertThat(dager).isEqualTo(5);
    }
}