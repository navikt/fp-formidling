package no.nav.vedtak.felles.prosesstask.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.Test;

import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskFeil;

public class ProsessTaskFeilTest {
    private static TaskManagerFeil feilFactory = FeilFactory.create(TaskManagerFeil.class);

    @Test
    public void skal_skrive_feil_til_json() throws Exception {
        // Arrange
        Exception exception = lagEnFeilFraEnMetode();
        Feil feil = feilFactory.kunneIkkeProsessereTaskVilPrøveIgjenEnkelFeilmelding(1L, "hello.world", 1, LocalDateTime.now(), exception);
        ProsessTaskData prosessTaskData = new ProsessTaskData("hello.world");
        prosessTaskData.setId(1L);

        // Act
        ProsessTaskFeil prosessTaskFeil = new ProsessTaskFeil(prosessTaskData, feil);

        assertThat(prosessTaskFeil.getStackTrace()).isNotNull().contains("IllegalArgumentException").contains("lagEnFeilFraEnMetode");
        assertThat(prosessTaskFeil.getFeilkode()).isEqualTo("FP-415564");
        assertThat(prosessTaskFeil.getFeilmelding()).contains("Kunne ikke prosessere task, id=1");

        String json = prosessTaskFeil.writeValueAsString();

        System.out.println(json);  // NOSONAR
    }

    private Exception lagEnFeilFraEnMetode() {
        return new IllegalArgumentException("dette er en feil");
    }
}
