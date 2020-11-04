package no.nav.foreldrepenger.melding.brevmapper.brev;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
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
import no.nav.foreldrepenger.melding.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeId;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.InnhenteOpplysningerDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.typer.Dato;
import no.nav.foreldrepenger.melding.typer.Saksnummer;

@ExtendWith(MockitoExtension.class)
public class InnhenteOpplysningerDokumentdataMapperTest {

    private static final String SØKERS_NAVN = "Bruker Brukersen";
    private static final String SAKSNUMMER = "123456";
    private static final String FRITEKST_INN = "Element1\nElement2";
    private static final List<String> FRITEKST_UT = List.of("Element1", "Element2");
    private static final LocalDate SØKNAD_DATO = LocalDate.now().minusDays(1);

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private BrevMapperUtil brevMapperUtil;

    private InnhenteOpplysningerDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    public void before() {
        BrevParametere brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        brevMapperUtil = new BrevMapperUtil(brevParametere);
        dokumentdataMapper = new InnhenteOpplysningerDokumentdataMapper(brevMapperUtil, domeneobjektProvider);

        MottattDokument mottattDokument = new MottattDokument(SØKNAD_DATO, DokumentTypeId.FORELDREPENGER_ENDRING_SØKNAD, DokumentKategori.SØKNAD);
        when(domeneobjektProvider.hentMottatteDokumenter(any(Behandling.class))).thenReturn(of(mottattDokument));
    }

    @Test
    public void skal_mappe_felter() {
        // Arrange
        Behandling behandling = opprettBehandling();
        DokumentFelles dokumentFelles = lagDokumentFelles();
        DokumentHendelse dokumentHendelse = lagDokumentHendelse();

        // Act
        InnhenteOpplysningerDokumentdata innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo("111111 11111");
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBrevDato()).isEqualTo(Dato.formaterDato(LocalDate.now()));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getHarVerge()).isEqualTo(true);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErKopi()).isEqualTo(true);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");

        assertThat(innhenteOpplysningerDokumentdata.getFørstegangsbehandling()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getRevurdering()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getEndringssøknad()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getDød()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getSøknadDato()).isEqualTo(formaterDato(SØKNAD_DATO));
        assertThat(innhenteOpplysningerDokumentdata.getFristDato()).isEqualTo(formaterDato(brevMapperUtil.getSvarFrist()));
        assertThat(innhenteOpplysningerDokumentdata.getDokumentListe()).containsAll(FRITEKST_UT);
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
                .medId(1L)
                .medBehandlingType(BehandlingType.REVURDERING)
                .medBehandlingÅrsaker(of(BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER).build()))
                .medBehandlingsresultat(Behandlingsresultat.builder()
                        .medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
                .build();
    }

    private DokumentFelles lagDokumentFelles() {
        DokumentAdresse dokumentAdresse = new DokumentAdresse.Builder()
                .medAdresselinje1("Adresse 1")
                .medPostNummer("0491")
                .medPoststed("OSLO")
                .medMottakerNavn(SØKERS_NAVN)
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
                .medMottakerId("11111111111")
                .medMottakerNavn(SØKERS_NAVN)
                .medSaksnummer(new Saksnummer(SAKSNUMMER))
                .medSakspartId("99999999999")
                .medSakspartNavn(SØKERS_NAVN)
                .medErKopi(Optional.of(DokumentFelles.Kopi.JA))
                .medMottakerType(DokumentFelles.MottakerType.PERSON)
                .medSpråkkode(Språkkode.nb)
                .medSakspartPersonStatus("ANNET")
                .build();
    }

    private DokumentHendelse lagDokumentHendelse() {
        return DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medFritekst(FRITEKST_INN)
                .build();
    }
}