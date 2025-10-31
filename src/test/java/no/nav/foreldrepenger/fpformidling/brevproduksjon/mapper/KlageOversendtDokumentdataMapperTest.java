package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.klageBehandling;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.klageVurderingResultat;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

@ExtendWith(MockitoExtension.class)
class KlageOversendtDokumentdataMapperTest {

    private static final String FRITEKST_TIL_BREV = "FRITEKST";
    private static final LocalDate MOTTATT_DATO = LocalDate.now();

    private KlageOversendtDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        dokumentdataMapper = new KlageOversendtDokumentdataMapper();
    }

    @Test
    void skal_mappe_felter_for_brevet() {
        // Arrange
        var klageVurderingResultatNK = klageVurderingResultat().fritekstTilBrev(FRITEKST_TIL_BREV).build();
        var klageBehandlingData = klageBehandling().klageVurderingResultatNK(klageVurderingResultatNK)
            .mottattDato(MOTTATT_DATO)
            .build();
        var behandling = defaultBuilder().fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER).klageBehandling(klageBehandlingData).build();
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        var dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(dokumentdata.getFelles()).isNotNull();
        assertThat(dokumentdata.getFelles().getSøkerNavn()).isEqualTo(DatamapperTestUtil.SØKERS_NAVN);
        assertThat(dokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(DatamapperTestUtil.SØKERS_FNR));
        assertThat(dokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(dokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(dokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(dokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(dokumentdata.getFelles().getSaksnummer()).isEqualTo(DatamapperTestUtil.SAKSNUMMER);
        assertThat(dokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.FP);
        assertThat(dokumentdata.getFelles().getErUtkast()).isFalse();
        assertThat(dokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST_TIL_BREV));

        assertThat(dokumentdata.getGjelderTilbakekreving()).isFalse();
        assertThat(dokumentdata.getMottattDato()).isEqualTo(formaterDatoNorsk(MOTTATT_DATO));
    }
}
