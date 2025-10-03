package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.standardForeldrepengerBehandling;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.klage.Klage;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageVurderingResultat;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;

@ExtendWith(MockitoExtension.class)
class KlageOmgjortDokumentdataMapperTest {

    private static final String FRITEKST_TIL_BREV = "FRITEKST";

    @Mock
    private BrevParametere brevParametere;

    @Mock
    private DomeneobjektProvider domeneobjektProvider;

    private KlageOmgjortDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        when(brevParametere.getKlagefristUker()).thenReturn(6);
        dokumentdataMapper = new KlageOmgjortDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    void skal_mappe_felter_for_brevet() {
        // Arrange
        var behandling = standardForeldrepengerBehandling();
        var dokumentFelles = lagStandardDokumentFelles();
        var dokumentHendelse = lagStandardHendelseBuilder().build();
        mockKlage(behandling);

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST_TIL_BREV));

        assertThat(dokumentdata.getGjelderTilbakekreving()).isFalse();
        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }

    private void mockKlage(Behandling behandling) {
        var klage = Klage.ny()
            .medPåklagdBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medKlageVurderingResultatNK(new KlageVurderingResultat(null, FRITEKST_TIL_BREV))
            .build();
        when(domeneobjektProvider.hentKlagebehandling(behandling)).thenReturn(klage);
    }
}
