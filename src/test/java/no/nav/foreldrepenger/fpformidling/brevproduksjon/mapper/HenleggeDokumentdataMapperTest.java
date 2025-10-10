package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlag;

class HenleggeDokumentdataMapperTest {

    private HenleggeDokumentdataMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HenleggeDokumentdataMapper();
    }

    @Test
    void henlegg_mapper_vanligBehandling() {
        //Arrange
        var behandling = opprettBehandling(BrevGrunnlag.BehandlingType.FØRSTEGANGSSØKNAD, BrevGrunnlag.FagsakYtelseType.FORELDREPENGER);
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse();

        //Act
        var henleggelseDokumentdata = mapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(henleggelseDokumentdata.getVanligBehandling()).isTrue();
        assertThat(henleggelseDokumentdata.getAnke()).isFalse();
        assertThat(henleggelseDokumentdata.getInnsyn()).isFalse();
        assertThat(henleggelseDokumentdata.getKlage()).isFalse();
        assertThat(henleggelseDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
    }

    @Test
    void henlegg_mapper_anke_med_opphav_klage() {
        //Arrange
        var behandling = opprettBehandling(BrevGrunnlag.BehandlingType.ANKE, BrevGrunnlag.FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentHendelse = lagDokumentHendelse();

        //Act
        var henleggelseDokumentdata = mapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(henleggelseDokumentdata.getVanligBehandling()).isFalse();
        assertThat(henleggelseDokumentdata.getAnke()).isTrue();
        assertThat(henleggelseDokumentdata.getInnsyn()).isFalse();
        assertThat(henleggelseDokumentdata.getKlage()).isFalse();
        assertThat(henleggelseDokumentdata.getFelles().getYtelseType()).isEqualTo(FagsakYtelseType.SVANGERSKAPSPENGER.getKode());
    }

    private BrevGrunnlag opprettBehandling(BrevGrunnlag.BehandlingType behType, BrevGrunnlag.FagsakYtelseType ytelseType) {
        var behandlingsres = behandlingsresultat()
            .behandlingResultatType(BrevGrunnlag.Behandlingsresultat.BehandlingResultatType.INNVILGET)
            .build();

        return defaultBuilder()
            .uuid(UUID.randomUUID())
            .behandlingType(behType)
            .behandlingsresultat(behandlingsres)
            .fagsakYtelseType(ytelseType)
            .build();
    }

    private DokumentHendelse lagDokumentHendelse() {
        return DatamapperTestUtil.lagStandardHendelseBuilder().build();
    }
}
