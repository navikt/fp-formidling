package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.PersonTjeneste;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.søknad.Søknad;

public class InnvilgelseForeldrepengerMapperTest {

    InnvilgelseForeldrepengerMapper mapper;

    private final DomeneobjektProvider domeneobjektProvider = Mockito.mock(DomeneobjektProvider.class);
    private final Behandling behandling = Mockito.mock(Behandling.class);
    private final Behandling annenBehandling = Mockito.mock(Behandling.class);
    private final Behandling tredjeBehandling = Mockito.mock(Behandling.class);
    private final Behandling fjerdeBehandling = Mockito.mock(Behandling.class);
    private final Behandling femteBehandling = Mockito.mock(Behandling.class);
    private final Søknad søknad = Mockito.mock(Søknad.class);

    @BeforeEach
    public void setup() {
        when(behandling.getUuid()).thenReturn(UUID.fromString("EACD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(annenBehandling.getUuid()).thenReturn(UUID.fromString("EBCD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(tredjeBehandling.getUuid()).thenReturn(UUID.fromString("ECCD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(fjerdeBehandling.getUuid()).thenReturn(UUID.fromString("EDCD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(femteBehandling.getUuid()).thenReturn(UUID.fromString("EECD223A-B0F9-4CB4-A9FE-39EFB52A0C50"));
        when(domeneobjektProvider.hentSøknad(Mockito.any())).thenReturn(Optional.empty());
        mapper = new InnvilgelseForeldrepengerMapper(domeneobjektProvider, null, mock(PersonTjeneste.class));
    }

    @Test
    public void skalIkkeEvigLoopMenKasteException() {
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)).thenReturn(Optional.of(annenBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(annenBehandling)).thenReturn(Optional.of(behandling));
        assertThatThrownBy(() -> mapper.hentSøknadUansett(behandling)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void skalHenteSøknadDypt() {
        when(domeneobjektProvider.hentSøknad(femteBehandling)).thenReturn(Optional.of(søknad));

        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)).thenReturn(Optional.of(annenBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(annenBehandling)).thenReturn(Optional.of(tredjeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(tredjeBehandling)).thenReturn(Optional.of(fjerdeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(fjerdeBehandling)).thenReturn(Optional.of(femteBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(femteBehandling)).thenReturn(Optional.empty());
        assertThat(mapper.hentSøknadUansett(behandling)).isNotNull();
    }

    @Test
    public void skalHenteSøknadIkkeDyptDypt() {
        when(domeneobjektProvider.hentSøknad(behandling)).thenReturn(Optional.of(søknad));

        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)).thenReturn(Optional.of(annenBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(annenBehandling)).thenReturn(Optional.of(tredjeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(tredjeBehandling)).thenReturn(Optional.of(fjerdeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(femteBehandling)).thenReturn(Optional.empty());
        assertThat(mapper.hentSøknadUansett(behandling)).isNotNull();
    }

    @Test
    public void skalFeileNårTomtForBehandlinger() {
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)).thenReturn(Optional.of(annenBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(annenBehandling)).thenReturn(Optional.of(tredjeBehandling));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(tredjeBehandling)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> mapper.hentSøknadUansett(behandling)).isInstanceOf(IllegalStateException.class);
    }

}
