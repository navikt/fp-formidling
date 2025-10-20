package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnFlerbarnsdagerUtvidetDagerHvisFinnes;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnFlerbarnsdagerUtvidetUkerHvisFinnes;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.kontoUtvidelser;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.stønadskonto;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

class StønadskontoMapperTest {

    @Test
    void skal_finne_disponible_dager_mødrekvote() {
        var stønadskontoer = List.of(stønadskonto().stønadskontotype(BrevGrunnlagDto.Foreldrepenger.Stønadskonto.Type.MØDREKVOTE)
            .maxDager(10)
            .saldo(5)
            .kontoUtvidelser(kontoUtvidelser().build())
            .build());
        var dager = StønadskontoMapper.finnDisponibleDager(stønadskontoer, BrevGrunnlagDto.RelasjonsRolleType.MORA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    void skal_finne_disponible_dager_fellesperiode() {
        var stønadskontoer = List.of(stønadskonto().stønadskontotype(BrevGrunnlagDto.Foreldrepenger.Stønadskonto.Type.FELLESPERIODE)
            .maxDager(10)
            .saldo(5)
            .kontoUtvidelser(kontoUtvidelser().build())
            .build());
        var dager = StønadskontoMapper.finnDisponibleFellesDager(stønadskontoer);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    void skal_finne_disponible_dager_foreldrepenger() {
        var stønadskontoer = List.of(stønadskonto().stønadskontotype(BrevGrunnlagDto.Foreldrepenger.Stønadskonto.Type.FORELDREPENGER)
            .maxDager(10)
            .saldo(5)
            .kontoUtvidelser(kontoUtvidelser().build())
            .build());
        var dager = StønadskontoMapper.finnDisponibleDager(stønadskontoer, BrevGrunnlagDto.RelasjonsRolleType.MORA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    void skal_finne_disponible_dager_fedrekvote() {
        var stønadskontoer = List.of(stønadskonto().stønadskontotype(BrevGrunnlagDto.Foreldrepenger.Stønadskonto.Type.FEDREKVOTE)
            .maxDager(10)
            .saldo(5)
            .kontoUtvidelser(kontoUtvidelser().build())
            .build());
        var dager = StønadskontoMapper.finnDisponibleDager(stønadskontoer, BrevGrunnlagDto.RelasjonsRolleType.FARA);
        assertThat(dager).isEqualTo(5);
    }

    @Test
    void flerbarnsdager_ingen() {
        var stønadskontoer = List.of(stønadskonto().stønadskontotype(BrevGrunnlagDto.Foreldrepenger.Stønadskonto.Type.FELLESPERIODE)
            .maxDager(100)
            .saldo(5)
            .kontoUtvidelser(kontoUtvidelser().flerbarnsdager(0).build())
            .build());
        assertThat(finnFlerbarnsdagerUtvidetUkerHvisFinnes(stønadskontoer)).isZero();
        assertThat(finnFlerbarnsdagerUtvidetDagerHvisFinnes(stønadskontoer)).isZero();
    }

    @Test
    void flerbarnsdager_hele_uker() {
        var stønadskontoer = List.of(stønadskonto().stønadskontotype(BrevGrunnlagDto.Foreldrepenger.Stønadskonto.Type.FELLESPERIODE)
            .maxDager(100)
            .saldo(5)
            .kontoUtvidelser(kontoUtvidelser().flerbarnsdager(20).build())
            .build());
        assertThat(finnFlerbarnsdagerUtvidetUkerHvisFinnes(stønadskontoer)).isEqualTo(4);
        assertThat(finnFlerbarnsdagerUtvidetDagerHvisFinnes(stønadskontoer)).isZero();
    }

    @Test
    void flerbarnsdager_uker_og_dager() {
        var stønadskontoer = List.of(stønadskonto().stønadskontotype(BrevGrunnlagDto.Foreldrepenger.Stønadskonto.Type.FELLESPERIODE)
            .maxDager(100)
            .saldo(5)
            .kontoUtvidelser(kontoUtvidelser().flerbarnsdager(32).build())
            .build());
        assertThat(finnFlerbarnsdagerUtvidetUkerHvisFinnes(stønadskontoer)).isEqualTo(6);
        assertThat(finnFlerbarnsdagerUtvidetDagerHvisFinnes(stønadskontoer)).isEqualTo(2);
    }
}
