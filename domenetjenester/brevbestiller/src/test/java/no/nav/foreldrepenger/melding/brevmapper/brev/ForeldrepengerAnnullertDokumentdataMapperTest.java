package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.standardBehandling;
import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.standardDokumenthendelse;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.Period;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.ForeldrepengerAnnullertDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
public class ForeldrepengerAnnullertDokumentdataMapperTest {

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private DokumentData dokumentData;

    private ForeldrepengerAnnullertDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        dokumentData = lagStandardDokumentData(DokumentMalType.FORELDREPENGER_ANNULLERT);
        dokumentdataMapper = new ForeldrepengerAnnullertDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    public void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        Behandling behandling = standardBehandling();
        DokumentFelles dokumentFelles = lagStandardDokumentFelles(dokumentData, DokumentFelles.Kopi.JA, false);
        DokumentHendelse dokumentHendelse = standardDokumenthendelse();

        // Act
        ForeldrepengerAnnullertDokumentdata dokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

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
        assertThat(dokumentdata.getFelles().getBehandlesAvKA()).isFalse();
        assertThat(dokumentdata.getFelles().getErUtkast()).isEqualTo(false);

        assertThat(dokumentdata.getKlagefristUker()).isEqualTo(6);
    }
}