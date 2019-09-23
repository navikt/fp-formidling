package no.nav.foreldrepenger.melding.datamapper.domene;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;

public class StønadskontoMapperTest {

    @Test
    public void skal_finne_disponible_dager_mødrekvote() {
        Set<Stønadskonto> stønadskontoer = Set.of(new Stønadskonto(10, StønadskontoType.MØDREKVOTE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer), RelasjonsRolleType.MORA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    public void skal_finne_disponible_dager_fellesperiode() {
        Set<Stønadskonto> stønadskontoer = Set.of(new Stønadskonto(10, StønadskontoType.FELLESPERIODE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleFellesDager(new Saldoer(stønadskontoer));
        assertThat(dager).isEqualTo(5);
    }

    @Test
    public void skal_finne_disponible_dager_foreldrepenger() {
        Set<Stønadskonto> stønadskontoer = Set.of(new Stønadskonto(10, StønadskontoType.FORELDREPENGER, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer), RelasjonsRolleType.MORA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    public void skal_finne_disponible_dager_fedrekvote() {
        Set<Stønadskonto> stønadskontoer = Set.of(new Stønadskonto(10, StønadskontoType.FEDREKVOTE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer), RelasjonsRolleType.FARA);
        assertThat(dager).isEqualTo(5);
    }
}
