package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import no.nav.foreldrepenger.melding.klage.KlageVurdering;
import no.nav.foreldrepenger.melding.klage.KlageVurderingResultat;
import no.nav.vedtak.felles.testutilities.StillTid;
import no.nav.vedtak.felles.testutilities.Whitebox;

public class KlageHjemsendtBrevMapperTest {
    private static final long ID = 123L;
    private static final String KA = "NAV Klageinstans Midt-Norge";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule().silent();

    @Rule
    public StillTid stillTid = new StillTid().medTid(LocalDateTime.of(2020, 1, 1, 12, 0));

    @Mock
    private DokumentFelles dokumentFelles;
    @Mock
    private FellesType fellesType;
    @Mock
    private BrevParametere brevParametere;
    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    @InjectMocks
    private KlageHjemsendtBrevMapper mapper;

    @Before
    public void before() {
        mapper = new KlageHjemsendtBrevMapper(brevParametere, domeneobjektProvider);
        MockitoAnnotations.initMocks(this);

        when(brevParametere.getSvarfristDager()).thenReturn(21); // Skal gi 22.01.2020 når klokken er stilt
        when(dokumentFelles.getNavnAvsenderEnhet()).thenReturn(KA);
        when(dokumentFelles.getKontaktTlf()).thenReturn("21 07 17 30");
    }

    @Test
    public void skal_mappe_brev_om_hjemsendt_vedtak_uten_oppheving() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, KlageVurdering.HJEMSENDE_UTEN_Å_OPPHEVE);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_ikke_opphevet"));
    }

    @Test
    public void skal_mappe_brev_om_hjemsendt_vedtak_med_oppheving() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentHendelse dokumentHendelse = opprettDokumentHendelse();
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));
        mockKlage(behandling, KlageVurdering.OPPHEVE_YTELSESVEDTAK);

        // Act
        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);

        // Assert
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst_opphevet"));
    }

    private Behandling opprettBehandling() {
        BehandlingType behandlingType = BehandlingType.KLAGE;
        Whitebox.setInternalState(behandlingType, "ekstraData", "{ \"behandlingstidFristUker\" : 12, \"behandlingstidVarselbrev\" : \"N\" }");
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

    private void mockKlage(Behandling behandling, KlageVurdering klageVurdering) {
        KlageVurderingResultat klageVurderingResultat = KlageVurderingResultat.ny()
                .medFritekstTilbrev("FRITEKST")
                .medKlageVurdering(klageVurdering)
                .build();
        Klage klage = Klage.ny()
                .medKlageVurderingResultatNK(klageVurderingResultat)
                .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}