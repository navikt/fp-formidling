package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.HenleggelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

class HenleggeDokumentdataMapperTest {

    private DokumentData dokumentData;

    private HenleggeDokumentdataMapper mapper;

    @BeforeEach
    void setUp() {
        dokumentData = lagStandardDokumentData(DokumentMalType.INFO_OM_HENLEGGELSE);
        mapper = new HenleggeDokumentdataMapper();
    }

    @Test
    public void henlegg_mapper_vanligBehandling() {
        //Arrange
        Behandling behandling = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, "NAV Familie- og pensjonsytelser");
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, null, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse(FagsakYtelseType.FORELDREPENGER);

        //Act
        HenleggelseDokumentdata henleggelseDokumentdata = mapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(henleggelseDokumentdata.getVanligBehandling()).isTrue();
        assertThat(henleggelseDokumentdata.getAnke()).isFalse();
        assertThat(henleggelseDokumentdata.getInnsyn()).isFalse();
        assertThat(henleggelseDokumentdata.getKlage()).isFalse();
        assertThat(henleggelseDokumentdata.getOpphavType()).isEqualTo("FAMPEN");
        assertThat(henleggelseDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
    }

    @Test
    public void henlegg_mapper_anke_med_opphav_klage() {
        //Arrange
        Behandling behandling = opprettBehandling(BehandlingType.ANKE, "NAV Klageinstans");
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, null, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse(FagsakYtelseType.SVANGERSKAPSPENGER);

        //Act
        HenleggelseDokumentdata henleggelseDokumentdata = mapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        //Assert
        assertThat(henleggelseDokumentdata.getVanligBehandling()).isFalse();
        assertThat(henleggelseDokumentdata.getAnke()).isTrue();
        assertThat(henleggelseDokumentdata.getInnsyn()).isFalse();
        assertThat(henleggelseDokumentdata.getKlage()).isFalse();
        assertThat(henleggelseDokumentdata.getOpphavType()).isEqualTo("KLAGE");
        assertThat(henleggelseDokumentdata.getFelles().getYtelseType()).isEqualTo("SVP");
    }

    private Behandling opprettBehandling(BehandlingType behType, String behNavn) {
        return Behandling.builder()
                .medUuid(UUID.randomUUID())
                .medBehandlingType(behType)
                .medBehandlendeEnhetNavn(behNavn)
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
    }

    private DokumentHendelse lagDokumentHendelse(FagsakYtelseType ytelseType) {
        return lagStandardHendelseBuilder()
                .medYtelseType(ytelseType)
                .build();
    }
}
