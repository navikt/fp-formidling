package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;

public class KlageOmgjøringBrevMapperTest {
    private static final long ID = 123L;
    private static final String NFP = "NAV Familie- og pensjonsytelser Drammen";
    private static final String KA = "NAV Klageinstans Midt-Norge";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Mock
    private DokumentFelles dokumentFelles;
    @Mock
    private FellesType fellesType;
    @Mock
    private BrevParametere brevParametere;
    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    @InjectMocks
    private KlageOmgjøringBrevMapper mapper;

    @Before
    public void before() {
        mapper = new KlageOmgjøringBrevMapper(brevParametere, domeneobjektProvider);
        MockitoAnnotations.initMocks(this);
        when(brevParametere.getKlagefristUker()).thenReturn(6);
    }

    @Test
    public void skal_mappe_brev_om_omgjøring_av_førstegangsbehandling_fra_NFP() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(NFP);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(NFP);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_NFP"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_NFP"));
    }

    @Test
    public void skal_mappe_brev_om_omgjøring_av_førstegangsbehandling_fra_KA() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(KA);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(KA);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_KA"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_KA"));
    }

    @Test
    public void skal_mappe_brev_om_omgjøring_av_tilbakekreving_fra_NFP() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(NFP);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(NFP);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, BehandlingType.TILBAKEKREVING);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_NFP_TILBAKEBETALING"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_NFP_TILBAKEBETALING"));
    }

    @Test
    public void skal_mappe_brev_om_omgjøring_av_tilbakekreving_fra_KA() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(KA);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(KA);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, BehandlingType.TILBAKEKREVING);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_KA_TILBAKEBETALING"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_KA_TILBAKEBETALING"));
    }

    private Behandling opprettBehandling() {
        return Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.KLAGE)
                .medSpråkkode(Språkkode.nb)
                .build();
    }

    private DokumentHendelse opprettDokumentHendelse(String behandlendeEnhetNavn) {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medBehandlendeEnhetNavn(behandlendeEnhetNavn)
                .build();
    }

    private void mockKlage(Behandling behandling, BehandlingType behandlingType) {
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny()
                .medFritekstTilbrev("FRITEKST")
                .build();
        Klage klage = Klage.ny()
                .medPåklagdBehandlingType(behandlingType)
                .medKlageVurderingResultatNK(klageVurderingResultat)
                .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}