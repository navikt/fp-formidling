package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;

public class KlageHjemsendtBrevMapperTest {
    private static final long ID = 123L;
    private static final String KA = "NAV Klageinstans Midt-Norge";

    @Mock
    private DokumentFelles dokumentFelles;
    @Mock
    private FellesType fellesType;
    @Mock
    private BrevParametere brevParametere;
    @Mock
    private DomeneobjektProvider domeneobjektProvider;
    @Mock
    private BrevMapperUtil brevMapperUtil;

    @InjectMocks
    private KlageHjemsendtBrevMapper mapper;

    @BeforeEach
    public void before() {
        mapper = new KlageHjemsendtBrevMapper(brevParametere, domeneobjektProvider, brevMapperUtil);
        MockitoAnnotations.openMocks(this);

        when(brevMapperUtil.getSvarFrist()).thenReturn(LocalDate.of(2020, 1, 1).plusDays(21));
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(KA);
        when(dokumentFelles.getKontaktTlf()).thenReturn("21 07 17 30");
    }

    @Test
    public void skal_mappe_brev_om_hjemsendt_klage_på_førstegangsbehandling_uten_oppheving() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD, KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_ikke_opphevet"));
    }

    @Test
    public void skal_mappe_brev_om_hjemsendt_klage_på_tilbakekreving_uten_oppheving() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, BehandlingType.TILBAKEKREVING, KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_TILBAKEBETALING"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_ikke_opphevet_TILBAKEBETALING"));
    }

    @Test
    public void skal_mappe_brev_om_hjemsendt_klage_på_førstegangsbehandling_med_oppheving() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD, KlageVurdering.OPPHEVE_YTELSESVEDTAK);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_opphevet"));
    }

    @Test
    public void skal_mappe_brev_om_hjemsendt_klage_på_tilbakekreving_med_oppheving() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, BehandlingType.TILBAKEKREVING, KlageVurdering.OPPHEVE_YTELSESVEDTAK);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_TILBAKEBETALING"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_opphevet_TILBAKEBETALING"));
    }

    private Behandling opprettBehandling() {
        BehandlingType behandlingType = BehandlingType.KLAGE;
        return Behandling.builder().medId(ID)
                .medBehandlingType(behandlingType)
                .medSpråkkode(Språkkode.nb)
                .build();
    }

    private DokumentHendelse opprettDokumentHendelse() {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medBehandlendeEnhetNavn(KA)
                .build();
    }

    private void mockKlage(Behandling behandling, BehandlingType behandlingType, KlageVurdering klageVurdering) {
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny()
                .medFritekstTilbrev("FRITEKST")
                .medKlageVurdering(klageVurdering)
                .build();
        Klage klage = Klage.ny()
                .medPåklagdBehandlingType(behandlingType)
                .medKlageVurderingResultatNK(klageVurderingResultat)
                .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}