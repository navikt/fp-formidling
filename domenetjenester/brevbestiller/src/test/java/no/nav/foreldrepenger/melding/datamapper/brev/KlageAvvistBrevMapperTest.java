package no.nav.foreldrepenger.melding.datamapper.brev;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
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
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.melding.klage.KlageFormkravResultat;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;

public class KlageAvvistBrevMapperTest {
    private static final long ID = 123L;
    private static final String NFP = "NAV Familie- og pensjonsytelser Drammen";
    private static final String NFP_TLF = "55 55 33 33";
    private static final String KA = "NAV Klageinstans Midt-Norge";
    private static final String KA_TLF = "21 07 17 30";

    @Mock
    private DokumentFelles dokumentFelles;
    @Mock
    private FellesType fellesType;
    @Mock
    private BrevParametere brevParametere;
    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    @InjectMocks
    private KlageAvvistBrevMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new KlageAvvistBrevMapper(brevParametere, domeneobjektProvider);
        MockitoAnnotations.openMocks(this);
        when(brevParametere.getKlagefristUker()).thenReturn(6);
    }

    @Test
    public void skal_mappe_brev_om_avvist_klage_fra_NFP_med_en_årsak_på_førstegangsbehandling() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(NFP);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(NFP);
        when(dokumentFelles.getKontaktTlf()).thenReturn(NFP_TLF);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        KlageAvvistÅrsak klageAvvistÅrsak = KlageAvvistÅrsak.KLAGET_FOR_SENT;
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD, of(klageAvvistÅrsak));

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_NFP"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_NFP"));
    }

    @Test
    public void skal_mappe_brev_om_avvist_klage_fra_KA_med_en_årsak_på_førstegangsbehandling() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(KA);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(KA);
        when(dokumentFelles.getKontaktTlf()).thenReturn(KA_TLF);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        KlageAvvistÅrsak klageAvvistÅrsak = KlageAvvistÅrsak.KLAGET_FOR_SENT;
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD, of(klageAvvistÅrsak));

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_KA"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_KA"));
    }

    @Test
    public void skal_mappe_brev_om_avvist_klage_fra_NFP_med_flere_årsaker_i_punktliste_på_førstegangsbehandling() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(NFP);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(NFP);
        when(dokumentFelles.getKontaktTlf()).thenReturn(NFP_TLF);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        KlageAvvistÅrsak klageAvvistÅrsak1 = KlageAvvistÅrsak.KLAGET_FOR_SENT;
        KlageAvvistÅrsak klageAvvistÅrsak2 = KlageAvvistÅrsak.KLAGER_IKKE_PART;
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD, of(klageAvvistÅrsak1, klageAvvistÅrsak2));

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_NFP"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_NFP_to_aarsaker"));
    }

    @Test
    public void skal_mappe_brev_om_avvist_klage_fra_KA_med_flere_årsaker_i_punktliste_på_førstegangsbehandling() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(KA);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(KA);
        when(dokumentFelles.getKontaktTlf()).thenReturn(KA_TLF);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        KlageAvvistÅrsak klageAvvistÅrsak1 = KlageAvvistÅrsak.KLAGET_FOR_SENT;
        KlageAvvistÅrsak klageAvvistÅrsak2 = KlageAvvistÅrsak.KLAGER_IKKE_PART;
        mockKlage(behandling, BehandlingType.FØRSTEGANGSSØKNAD, of(klageAvvistÅrsak1, klageAvvistÅrsak2));

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_KA"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_KA_to_aarsaker"));
    }

    @Test
    public void skal_mappe_brev_om_avvist_klage_fra_NFP_med_en_årsak_på_tilbakekreving() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(NFP);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(NFP);
        when(dokumentFelles.getKontaktTlf()).thenReturn(NFP_TLF);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        KlageAvvistÅrsak klageAvvistÅrsak = KlageAvvistÅrsak.KLAGET_FOR_SENT;
        mockKlage(behandling, BehandlingType.TILBAKEKREVING, of(klageAvvistÅrsak));

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_NFP_TILBAKEBETALING"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_NFP"));
    }

    @Test
    public void skal_mappe_brev_om_avvist_klage_fra_KA_med_en_årsak_på_tilbakekreving() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse(KA);
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(KA);
        when(dokumentFelles.getKontaktTlf()).thenReturn(KA_TLF);
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        KlageAvvistÅrsak klageAvvistÅrsak = KlageAvvistÅrsak.KLAGET_FOR_SENT;
        mockKlage(behandling, BehandlingType.TILBAKEKREVING, of(klageAvvistÅrsak));

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift_KA_TILBAKEBETALING"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_KA"));
    }

    private Behandling opprettBehandling() {
        return Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.KLAGE)
                .medSpråkkode(Språkkode.NB)
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

    private void mockKlage(Behandling behandling, BehandlingType behandlingType, List<KlageAvvistÅrsak> avvistÅrsaker) {
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny()
                .medFritekstTilbrev("FRITEKST")
                .build();
        KlageFormkravResultat klageFormkravResultat = new KlageFormkravResultat(avvistÅrsaker);
        Klage klage = Klage.ny()
                .medPåklagdBehandlingType(behandlingType)
                .medKlageVurderingResultatNK(klageVurderingResultat)
                .medFormkravNFP(klageFormkravResultat)
                .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}
