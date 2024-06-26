package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnFlerbarnsdagerUtvidetDagerHvisFinnes;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnFlerbarnsdagerUtvidetUkerHvisFinnes;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.SaldoVisningStønadskontoType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Stønadskonto;

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

    @Test
    void flerbarnsdager_ingen() {
        var stønadskontoer = Set.of(new Stønadskonto(100, SaldoVisningStønadskontoType.FELLESPERIODE, 5, 0, 0));
        var saldoer = new Saldoer(stønadskontoer, 0);
        assertThat(finnFlerbarnsdagerUtvidetUkerHvisFinnes(saldoer)).isZero();
        assertThat(finnFlerbarnsdagerUtvidetDagerHvisFinnes(saldoer)).isZero();
    }

    @Test
    void flerbarnsdager_hele_uker() {
        var stønadskontoer = Set.of(new Stønadskonto(100, SaldoVisningStønadskontoType.FELLESPERIODE, 5, 0, 20));
        var saldoer = new Saldoer(stønadskontoer, 0);
        assertThat(finnFlerbarnsdagerUtvidetUkerHvisFinnes(saldoer)).isEqualTo(4);
        assertThat(finnFlerbarnsdagerUtvidetDagerHvisFinnes(saldoer)).isZero();
    }

    @Test
    void flerbarnsdager_uker_og_dager() {
        var stønadskontoer = Set.of(new Stønadskonto(100, SaldoVisningStønadskontoType.FELLESPERIODE, 5, 0, 32));
        var saldoer = new Saldoer(stønadskontoer, 0);
        assertThat(finnFlerbarnsdagerUtvidetUkerHvisFinnes(saldoer)).isEqualTo(6);
        assertThat(finnFlerbarnsdagerUtvidetDagerHvisFinnes(saldoer)).isEqualTo(2);
    }
}
