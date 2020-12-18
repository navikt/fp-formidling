package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.HenleggelseDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

class HenleggeDokumentDataMapperTest {

    private DokumentData dokumentData;

    private HenleggeDokumentDataMapper mapper;

    @BeforeEach
    void setUp() {
        dokumentData = lagStandardDokumentData(DokumentMalType.INFO_OM_HENLEGGELSE);
        mapper = new HenleggeDokumentDataMapper();
    }

    @Test
    public void henlegg_mapper_vanligBehandling() {
        //Arrange
        Behandling behandling = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, "NAV Familie- og pensjonsytelser");
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, null, false);
        DokumentHendelse dokumentHendelse = lagDokumentHendelse(FagsakYtelseType.FORELDREPENGER);

        //Act
        HenleggelseDokumentdata henleggelseDokumentdata = mapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling);

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
        HenleggelseDokumentdata henleggelseDokumentdata = mapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling);

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
                .medId(1L)
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