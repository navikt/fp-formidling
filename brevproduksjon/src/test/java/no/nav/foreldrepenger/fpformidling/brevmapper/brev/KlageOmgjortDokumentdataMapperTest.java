package no.nav.foreldrepenger.fpformidling.brevmapper.brev;

import static no.nav.foreldrepenger.fpformidling.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.datamapper.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.fpformidling.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.KlageOmgjortDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.fpformidling.klage.Klage;
import no.nav.foreldrepenger.fpformidling.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class KlageOmgjortDokumentdataMapperTest {

    private static final String FRITEKST_TIL_BREV = "FRITEKST";

    @Mock
    private BrevParametere brevParametere;

    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    private DokumentData dokumentData;

    private KlageOmgjortDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.KLAGE_OMGJORT);
        when(brevParametere.getKlagefristUker()).thenReturn(6);
        dokumentdataMapper = new KlageOmgjortDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    public void skal_mappe_felter_for_brevet() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().medErOpphevetKlage(true).build();
        mockKlage(behandling);

        // Act
        KlageOmgjortDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(Fritekst.fra(FRITEKST_TIL_BREV));

        assertThat(dokumentdata.getGjelderTilbakekreving()).isFalse();
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }

    private void mockKlage(Behandling behandling) {
        Klage klage = Klage.ny()
                .medPåklagdBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medKlageVurderingResultatNK(new KlageVurderingResultat(null, FRITEKST_TIL_BREV))
                .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}