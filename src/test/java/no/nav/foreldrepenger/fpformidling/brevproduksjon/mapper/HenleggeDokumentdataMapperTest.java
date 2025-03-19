package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

class HenleggeDokumentdataMapperTest {

    private DokumentData dokumentData;

    private HenleggeDokumentdataMapper mapper;

    @BeforeEach
    void setUp() {
        dokumentData = DatamapperTestUtil.lagStandardDokumentData(DokumentMalType.INFO_OM_HENLEGGELSE);
        mapper = new HenleggeDokumentdataMapper();
    }

    @Test
    void henlegg_mapper_vanligBehandling() {
        //Arrange
        var behandling = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, FagsakYtelseType.FORELDREPENGER);
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, null, false);
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
        var behandling = opprettBehandling(BehandlingType.ANKE, FagsakYtelseType.SVANGERSKAPSPENGER);
        var dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(dokumentData, null, false);
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

    private Behandling opprettBehandling(BehandlingType behType, FagsakYtelseType ytelseType) {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(behType)
            .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
            .medFagsak(FagsakBackend.ny().medFagsakYtelseType(ytelseType).build())
            .build();
    }

    private DokumentHendelse lagDokumentHendelse() {
        return DatamapperTestUtil.lagStandardHendelseBuilder().build();
    }
}
