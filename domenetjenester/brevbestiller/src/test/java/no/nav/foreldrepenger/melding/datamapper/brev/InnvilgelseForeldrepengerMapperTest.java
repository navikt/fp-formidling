package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import no.nav.foreldrepenger.PersonAdapter;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.søknad.Søknad;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class InnvilgelseForeldrepengerMapperTest {

    InnvilgelseForeldrepengerMapper mapper;

    @Mock
    private DomeneobjektProvider domeneobjektProvider;
    @Mock
    private Behandling behandling;
    @Mock
    private Behandling annenBehandling;
    @Mock
    private Behandling tredjeBehandling;
    @Mock
    private Behandling fjerdeBehandling;
    @Mock
    private Behandling femteBehandling;
    private static final Søknad SØKNAD = new Søknad(null, null, null);

    @BeforeEach
    public void setup() {
        when(behandling.getUuid()).thenReturn(UUID.fromString("EACD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(annenBehandling.getUuid()).thenReturn(UUID.fromString("EBCD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(tredjeBehandling.getUuid()).thenReturn(UUID.fromString("ECCD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(fjerdeBehandling.getUuid()).thenReturn(UUID.fromString("EDCD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(femteBehandling.getUuid()).thenReturn(UUID.fromString("EECD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(domeneobjektProvider.hentSøknad(Mockito.any())).thenReturn(Optional.empty());
        mapper = new InnvilgelseForeldrepengerMapper(domeneobjektProvider, null, mock(PersonAdapter.class));
    }

    @Test
    public void skalIkkeEvigLoopMenKasteException() {
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(behandling))).thenReturn(Optional.of(annenBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(annenBehandling))).thenReturn(Optional.of(behandling));
        assertThatThrownBy(() -> mapper.hentSøknadUansett(behandling)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void skalHenteSøknadDypt() {
        when(domeneobjektProvider.hentSøknad(eq(femteBehandling))).thenReturn(Optional.of(SØKNAD));

        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(behandling))).thenReturn(Optional.of(annenBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(annenBehandling))).thenReturn(Optional.of(tredjeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(tredjeBehandling))).thenReturn(Optional.of(fjerdeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(fjerdeBehandling))).thenReturn(Optional.of(femteBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(femteBehandling))).thenReturn(Optional.empty());
        assertThat(mapper.hentSøknadUansett(behandling)).isNotNull();
    }

    @Test
    public void skalHenteSøknadIkkeDyptDypt() {
        when(domeneobjektProvider.hentSøknad(behandling)).thenReturn(Optional.of(SØKNAD));

        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(behandling))).thenReturn(Optional.of(annenBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(annenBehandling))).thenReturn(Optional.of(tredjeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(tredjeBehandling))).thenReturn(Optional.of(fjerdeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(femteBehandling))).thenReturn(Optional.empty());
        assertThat(mapper.hentSøknadUansett(behandling)).isNotNull();
    }

    @Test
    public void skalFeileNårTomtForBehandlinger() {
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(behandling))).thenReturn(Optional.of(annenBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(annenBehandling))).thenReturn(Optional.of(tredjeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(tredjeBehandling))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mapper.hentSøknadUansett(behandling)).isInstanceOf(IllegalStateException.class);
    }

}
