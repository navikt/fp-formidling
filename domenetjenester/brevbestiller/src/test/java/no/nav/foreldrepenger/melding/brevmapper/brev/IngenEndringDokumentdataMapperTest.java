package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.IngenEndringDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class IngenEndringDokumentdataMapperTest {

    private DokumentData dokumentData;

    private IngenEndringDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        dokumentData = lagStandardDokumentData(DokumentMalType.INGEN_ENDRING);
        dokumentdataMapper = new IngenEndringDokumentdataMapper();
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        IngenEndringDokumentdata ingenEndringDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ingenEndringDokumentdata.getFelles()).isNotNull();
        assertThat(ingenEndringDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ingenEndringDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ingenEndringDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(ingenEndringDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(ingenEndringDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(ingenEndringDokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(ingenEndringDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(ingenEndringDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(ingenEndringDokumentdata.getFelles().getBehandlesAvKA()).isEqualTo(false);
        assertThat(ingenEndringDokumentdata.getFelles().getErUtkast()).isEqualTo(false);
    }

    @Test
    public void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.NEI, true);
        DokumentHendelse dokumentHendelse = lagStandardHendelseBuilder().build();

        // Act
        IngenEndringDokumentdata ingenEndringDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(ingenEndringDokumentdata.getFelles()).isNotNull();
        assertThat(ingenEndringDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(ingenEndringDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(ingenEndringDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(ingenEndringDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(ingenEndringDokumentdata.getFelles().getErKopi()).isEqualTo(false);
    }
}