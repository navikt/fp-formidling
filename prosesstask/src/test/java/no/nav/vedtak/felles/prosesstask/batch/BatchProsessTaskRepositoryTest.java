package no.nav.vedtak.felles.prosesstask.batch;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;


public class BatchProsessTaskRepositoryTest {

    @Test
    public void utledPartisjonsNr() {
        LocalDate date = LocalDate.of(2018, 1, 1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("02");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("03");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("04");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("05");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("06");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("07");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("08");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("09");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("10");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("11");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("12");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("01");
        date = date.plusMonths(1);
        assertThat(BatchProsessTaskRepository.utledPartisjonsNr(date)).isEqualTo("02");
    }
}
