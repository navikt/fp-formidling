package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoEngelsk;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Period;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FellesDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;

@ExtendWith(MockitoExtension.class)
class InnhenteOpplysningerDokumentdataMapperTest {

    private static final String FRITEKST = "Tekst1\n- Vedlegg1\n- Vedlegg2\nTekst2\nTekst3\n- Vedlegg3\nTekst4";
    private static final LocalDate SØKNAD_DATO = LocalDate.now().minusDays(1);

    private BrevMapperUtil brevMapperUtil;
    private InnhenteOpplysningerDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        brevMapperUtil = new BrevMapperUtil(brevParametere);
        dokumentdataMapper = new InnhenteOpplysningerDokumentdataMapper(brevMapperUtil);
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.Språkkode.BOKMÅL);
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getHarVerge()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErKopi()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getYtelseType()).isEqualTo(FellesDokumentdata.YtelseType.FP);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(innhenteOpplysningerDokumentdata.getFørstegangsbehandling()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getRevurdering()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getEndringssøknad()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getDød()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getKlage()).isFalse();
        assertThat(innhenteOpplysningerDokumentdata.getSøknadDato()).isEqualTo(formaterDatoNorsk(SØKNAD_DATO));
        assertThat(innhenteOpplysningerDokumentdata.getFristDato()).isEqualTo(formaterDatoNorsk(brevMapperUtil.getSvarFrist()));
    }

    @Test
    void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.Språkkode.BOKMÅL);
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.NEI, true, FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getErKopi()).isFalse();
    }

    @Test
    void skal_mappe_felter_for_brev_med_engelsk_språk() {
        // Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.Språkkode.BOKMÅL);
        var dokumentFelles = lagStandardDokumentFellesBuilder(FagsakYtelseType.FORELDREPENGER).medSpråkkode(Språkkode.EN).build();
        var dokumentHendelse = lagDokumentHendelse();

        // Act
        var innhenteOpplysningerDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(innhenteOpplysningerDokumentdata.getFelles()).isNotNull();
        assertThat(innhenteOpplysningerDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoEngelsk(LocalDate.now()));
        assertThat(innhenteOpplysningerDokumentdata.getSøknadDato()).isEqualTo(formaterDatoEngelsk(SØKNAD_DATO));
        assertThat(innhenteOpplysningerDokumentdata.getFristDato()).isEqualTo(formaterDatoEngelsk(brevMapperUtil.getSvarFrist()));
    }

    private BrevGrunnlagDto opprettBehandling(BrevGrunnlagDto.Språkkode språkkode) {
        return defaultBuilder()
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER)
            .behandlingType(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD)
            .språkkode(språkkode)
            .sisteSøknadMottattDato(SØKNAD_DATO)
            .build();
    }

    private DokumentHendelse lagDokumentHendelse() {
        return lagStandardHendelseBuilder().medFritekst(FRITEKST).build();
    }
}
