package no.nav.vedtak.felles.prosesstask.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHendelse;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;

public class ProsessTaskHendelseMottakImplTest {

    private static final Long UKJENT_TASK_ID = 999L;
    private static final Long TASK_ID = 111L;
    private static final Long TASK_ID_SOM_IKKE_VENTER = 666L;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();
    private ProsessTaskHendelseMottakImpl prosessTaskHendelseMottak;

    @Mock
    private ProsessTaskRepositoryImpl repo;

    @Mock
    private ProsessTaskData taskSomVenterØkonomiKvittering;

    @Mock
    private ProsessTaskData taskSomIkkeVenter;

    @Before
    public void setUp() throws Exception {
        prosessTaskHendelseMottak = new ProsessTaskHendelseMottakImpl(repo);
        when(taskSomVenterØkonomiKvittering.getHendelse()).thenReturn(Optional.of(ProsessTaskHendelse.UKJENT_HENDELSE));
        when(taskSomIkkeVenter.getHendelse()).thenReturn(Optional.empty());
    }

    @Test
    public void testMottaHendelseHappyDay() {
        // Arrange
        when(taskSomVenterØkonomiKvittering.getStatus()).thenReturn(ProsessTaskStatus.VENTER_SVAR);
        when(repo.finn(TASK_ID)).thenReturn(taskSomVenterØkonomiKvittering);
        // Act
        prosessTaskHendelseMottak.mottaHendelse(TASK_ID, ProsessTaskHendelse.UKJENT_HENDELSE);
        // Assert
        verify(taskSomVenterØkonomiKvittering).setStatus(ProsessTaskStatus.KLAR);
        verify(repo).lagre(taskSomVenterØkonomiKvittering);
    }

    @Test(expected = IllegalStateException.class)
    public void testMottaHendelseUkjentTask() {
        // Arrange
        when(repo.finn(UKJENT_TASK_ID)).thenReturn(null);
        // Act
        prosessTaskHendelseMottak.mottaHendelse(UKJENT_TASK_ID, ProsessTaskHendelse.UKJENT_HENDELSE);
        // Assert
    }

    @Test(expected = IllegalStateException.class)
    public void testMottaUventetHendelse() {
        // Arrange
        when(repo.finn(TASK_ID)).thenReturn(taskSomVenterØkonomiKvittering);
        // Act
        prosessTaskHendelseMottak.mottaHendelse(TASK_ID, ProsessTaskHendelse.UKJENT_HENDELSE);
        // Assert
    }

    @Test(expected = IllegalStateException.class)
    public void testMottaHendelseITaskSomIkkeVenter() {
        // Arrange
        when(repo.finn(TASK_ID_SOM_IKKE_VENTER)).thenReturn(taskSomIkkeVenter);
        // Act
        prosessTaskHendelseMottak.mottaHendelse(TASK_ID_SOM_IKKE_VENTER, ProsessTaskHendelse.UKJENT_HENDELSE);
        // Assert
    }
}
