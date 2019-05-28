package no.nav.foreldrepenger.melding.datamapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.søknad.Søknad;

public class InnvilgelseForeldrepengerMapperTest {

    InnvilgelseForeldrepengerMapper mapper;

    private DomeneobjektProvider domeneobjektProvider = Mockito.mock(DomeneobjektProvider.class);
    private BrevParametere brevParametere;

    private Behandling behandling = Mockito.mock(Behandling.class);
    private Behandling annenBehandling = Mockito.mock(Behandling.class);
    private Behandling tredjeBehandling = Mockito.mock(Behandling.class);
    private Behandling fjerdeBehandling = Mockito.mock(Behandling.class);
    private Behandling femteBehandling = Mockito.mock(Behandling.class);

    private Søknad søknad = Mockito.mock(Søknad.class);


    @Before
    public void setup() {
        when(domeneobjektProvider.hentSøknad(Mockito.any())).thenReturn(Optional.empty());
        mapper = new InnvilgelseForeldrepengerMapper(domeneobjektProvider, brevParametere);
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
