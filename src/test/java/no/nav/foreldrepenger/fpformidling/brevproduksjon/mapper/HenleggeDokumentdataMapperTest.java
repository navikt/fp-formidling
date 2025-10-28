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
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

class HenleggeDokumentdataMapperTest {

    private HenleggeDokumentdataMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new HenleggeDokumentdataMapper();
    }

    @Test
    void henlegg_mapper_vanligBehandling() {
        //Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD, BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER);
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse();

        //Act
        var henleggelseDokumentdata = mapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(henleggelseDokumentdata.getVanligBehandling()).isTrue();
        assertThat(henleggelseDokumentdata.getAnke()).isFalse();
        assertThat(henleggelseDokumentdata.getInnsyn()).isFalse();
        assertThat(henleggelseDokumentdata.getKlage()).isFalse();
        assertThat(henleggelseDokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.FP);
    }

    @Test
    void henlegg_mapper_anke_med_opphav_klage() {
        //Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.BehandlingType.ANKE, BrevGrunnlagDto.FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentHendelse = lagDokumentHendelse();

        //Act
        var henleggelseDokumentdata = mapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(henleggelseDokumentdata.getVanligBehandling()).isFalse();
        assertThat(henleggelseDokumentdata.getAnke()).isTrue();
        assertThat(henleggelseDokumentdata.getInnsyn()).isFalse();
        assertThat(henleggelseDokumentdata.getKlage()).isFalse();
        assertThat(henleggelseDokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.SVP);
    }

    private BrevGrunnlagDto opprettBehandling(BrevGrunnlagDto.BehandlingType behType, BrevGrunnlagDto.FagsakYtelseType ytelseType) {
        var behandlingsres = behandlingsresultat()
            .behandlingResultatType(BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType.INNVILGET)
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
