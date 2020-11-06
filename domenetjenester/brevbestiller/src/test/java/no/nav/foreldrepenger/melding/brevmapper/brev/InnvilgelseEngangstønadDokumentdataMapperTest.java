package no.nav.foreldrepenger.melding.brevmapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatES;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

@ExtendWith(MockitoExtension.class)
class InnvilgelseEngangstønadDokumentdataMapperTest {
    private InnvilgelseEngangstønadDokumentdataMapper dokumentdataMapperTest;
    private BrevParametere brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
    private static final long ID = 123L;
    private static final long ID_REV = 124L;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);
    @Mock
    private DokumentHendelse dokumentHendelse = mock(DokumentHendelse.class);

    private DokumentFelles dokumentFelles;

    @BeforeEach
    public void setup() {
        dokumentdataMapperTest = new InnvilgelseEngangstønadDokumentdataMapper(brevParametere, domeneobjektProvider);
    }

    @Test
    public void case_med_endret_sats_blir_satt_riktig() {
       //Arrange
        Behandling orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID);
        Behandling innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV);
        dokumentFelles = lagDokumentFelles();

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(innvilgetES))).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentBeregningsresultatESHvisFinnes(eq(orgBehES))).thenReturn(Optional.of(new BeregningsresultatES(86000L)));

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES);

        //Assert
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isTrue();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp().equals("1 000"));
        assertThat(innvilgelseDokumentdata.getDød()).isFalse();
        assertThat(innvilgelseDokumentdata.getFbEllerMedhold()).isFalse();
        assertThat(innvilgelseDokumentdata.getMedhold()).isFalse();
        assertThat(innvilgelseDokumentdata.getRevurdering()).isTrue();
        assertThat(innvilgelseDokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(innvilgelseDokumentdata.getFelles().getHarVerge()).isTrue();
    }

    @Test
    public void skal_ikke_flagge_endret_sats_hvis_forrige_behandling_manglet_beregningsresultat() {
       //Arrange
        Behandling orgBehES = opprettBehandling(BehandlingType.FØRSTEGANGSSØKNAD, ID);
        Behandling innvilgetES = opprettBehandling(BehandlingType.REVURDERING, ID_REV);
        dokumentFelles = lagDokumentFelles();

        when(domeneobjektProvider.hentBeregningsresultatES(eq(innvilgetES))).thenReturn(new BeregningsresultatES(85000L));
        when(domeneobjektProvider.hentOriginalBehandlingHvisFinnes(eq(innvilgetES))).thenReturn(Optional.of(orgBehES));
        when(domeneobjektProvider.hentBeregningsresultatESHvisFinnes(eq(orgBehES))).thenReturn(Optional.empty());

        //Act
        EngangsstønadInnvilgelseDokumentdata innvilgelseDokumentdata = dokumentdataMapperTest.mapTilDokumentdata(dokumentFelles, dokumentHendelse, innvilgetES);

        //Assert
        assertThat(innvilgelseDokumentdata.getErEndretSats()).isFalse();
        assertThat(innvilgelseDokumentdata.getInnvilgetBeløp().equals("85 000"));
    }

    private Behandling opprettBehandling(BehandlingType behType, long id) {
        return Behandling.builder().medId(id)
                .medBehandlingType(behType)
                .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
    }

    private DokumentFelles lagDokumentFelles() {
        DokumentAdresse dokumentAdresse = new DokumentAdresse.Builder()
                .medAdresselinje1("Adresse 1")
                .medPostNummer("0491")
                .medPoststed("OSLO")
                .medMottakerNavn("Guri Malla")
                .build();

        DokumentData dokumentData = DokumentData.builder()
                .medDokumentMalType(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD)
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingType("B")
                .medBestiltTid(LocalDateTime.now())
                .build();

        return DokumentFelles.builder(dokumentData)
                .medAutomatiskBehandlet(Boolean.TRUE)
                .medDokumentDato(LocalDate.now())
                .medKontaktTelefonNummer("22222222")
                .medMottakerAdresse(dokumentAdresse)
                .medNavnAvsenderEnhet("NAV Familie og pensjonsytelser")
                .medPostadresse(dokumentAdresse)
                .medReturadresse(dokumentAdresse)
                .medMottakerId("123456789")
                .medMottakerNavn("Guri Malla")
                .medSaksnummer(new Saksnummer("123456"))
                .medSakspartId("99999999999")
                .medSakspartNavn("Guri Malla")
                .medErKopi(Optional.of(DokumentFelles.Kopi.NEI))
                .medMottakerType(DokumentFelles.MottakerType.PERSON)
                .medSpråkkode(Språkkode.nb)
                .medSakspartPersonStatus("ANNET")
                .build();
    }
}
