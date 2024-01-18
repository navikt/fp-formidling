package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.SaldoVisningStønadskontoType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Stønadskonto;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StønadskontoMapperTest {

    @Test
    void skal_finne_disponible_dager_mødrekvote() {
        var stønadskontoer = Set.of(new Stønadskonto(10, SaldoVisningStønadskontoType.MØDREKVOTE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer, 0), RelasjonsRolleType.MORA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    void skal_finne_disponible_dager_fellesperiode() {
        var stønadskontoer = Set.of(new Stønadskonto(10, SaldoVisningStønadskontoType.FELLESPERIODE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleFellesDager(new Saldoer(stønadskontoer, 0));
        assertThat(dager).isEqualTo(5);
    }

    @Test
    void skal_finne_disponible_dager_foreldrepenger() {
        var stønadskontoer = Set.of(new Stønadskonto(10, SaldoVisningStønadskontoType.FORELDREPENGER, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer, 0), RelasjonsRolleType.MORA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    void skal_finne_disponible_dager_fedrekvote() {
        var stønadskontoer = Set.of(new Stønadskonto(10, SaldoVisningStønadskontoType.FEDREKVOTE, 5, 0, 0));
        var dager = StønadskontoMapper.finnDisponibleDager(new Saldoer(stønadskontoer, 0), RelasjonsRolleType.FARA);
        assertThat(dager).isEqualTo(5);
    }
}
