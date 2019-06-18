package no.nav.vedtak.felles.prosesstask.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

public class ProsessTaskDataTest {

    private static final String ORIGINALTYPE = "ORIGINALTYPE";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private ProsessTaskData original;

    @Before
    public void oppsett() {
        original = new ProsessTaskData(ORIGINALTYPE);
    }

    @Test
    public void testVenterPåHendelse() {
        // Arrange
        // Act
        original.venterPåHendelse(ProsessTaskHendelse.UKJENT_HENDELSE);
        // Assert
        assertThat(original.getStatus()).isEqualTo(ProsessTaskStatus.VENTER_SVAR);
        Optional<ProsessTaskHendelse> venterPå = original.getHendelse();
        assertThat(venterPå.isPresent()).isEqualTo(true);
        assertThat(venterPå.get()).isEqualTo(ProsessTaskHendelse.UKJENT_HENDELSE);
    }

    @Test
    public void testVenterIkkePåHendelse() {
        // Arrange
        // Act
        // Assert
        Optional<ProsessTaskHendelse> venterPå = original.getHendelse();
        assertThat(venterPå.isPresent()).isEqualTo(false);
    }

}
