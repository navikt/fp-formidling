package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.defaultBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.VarselOmRevurderingDokumentdata.RevurderingVarslingÅrsak;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.barn;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.familieHendelse;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak;

@ExtendWith(MockitoExtension.class)
class VarselOmRevurderingDokumentdataMapperTest {

    private static final LocalDate TERMINDATO = LocalDate.now();
    private static final int ANTALL_BARN = 1;

    private BrevMapperUtil brevMapperUtil;
    private VarselOmRevurderingDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        brevMapperUtil = new BrevMapperUtil(brevParametere);
        dokumentdataMapper = new VarselOmRevurderingDokumentdataMapper(brevMapperUtil);
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER);
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.JA, false, FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse(RevurderingÅrsak.ARBEIDS_I_STØNADSPERIODEN);

        // Act
        var varselOmRevurderingDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(varselOmRevurderingDokumentdata.getFelles()).isNotNull();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(varselOmRevurderingDokumentdata.getFelles().getMottakerNavn()).isNull();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getBrevDato()).isEqualTo(formaterDatoNorsk(LocalDate.now()));
        assertThat(varselOmRevurderingDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getErKopi()).isTrue();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSaksnummer()).isEqualTo(SAKSNUMMER);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getYtelseType()).isEqualTo("FP");
        assertThat(varselOmRevurderingDokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(FRITEKST));
        assertThat(varselOmRevurderingDokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(varselOmRevurderingDokumentdata.getTerminDato()).isEqualTo(formaterDatoNorsk(TERMINDATO));
        assertThat(varselOmRevurderingDokumentdata.getFristDato()).isEqualTo(formaterDatoNorsk(brevMapperUtil.getSvarFrist()));
        assertThat(varselOmRevurderingDokumentdata.getAntallBarn()).isEqualTo(ANTALL_BARN);
        assertThat(varselOmRevurderingDokumentdata.getAdvarselKode()).isEqualTo(RevurderingVarslingÅrsak.JOBBFULLTID);
        assertThat(varselOmRevurderingDokumentdata.getFlereOpplysninger()).isFalse();
    }

    @Test
    void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER);
        var dokumentFelles = lagStandardDokumentFelles(DokumentFelles.Kopi.NEI, true, FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse(RevurderingÅrsak.ARBEIDS_I_STØNADSPERIODEN);

        // Act
        var varselOmRevurderingDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(varselOmRevurderingDokumentdata.getFelles()).isNotNull();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSøkerNavn()).isEqualTo(SØKERS_NAVN);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getSøkerPersonnummer()).isEqualTo(formaterPersonnummer(SØKERS_FNR));
        assertThat(varselOmRevurderingDokumentdata.getFelles().getMottakerNavn()).isEqualTo(VERGES_NAVN);
        assertThat(varselOmRevurderingDokumentdata.getFelles().getHarVerge()).isTrue();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getErKopi()).isFalse();
    }

    @Test
    void skal_gi_flere_opplysninger_når_ikke_JOBBFULLTID_er_årasak() {
        // Arrange
        var behandling = opprettBehandling(BrevGrunnlagDto.FagsakYtelseType.FORELDREPENGER);
        var dokumentFelles = lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        var dokumentHendelse = lagDokumentHendelse(RevurderingÅrsak.ARBEID_I_UTLANDET);

        // Act
        var varselOmRevurderingDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(varselOmRevurderingDokumentdata.getFlereOpplysninger()).isTrue();
    }

    private BrevGrunnlagDto opprettBehandling(BrevGrunnlagDto.FagsakYtelseType ytelseType) {
        var fhBarn = barn().fødselsdato(LocalDate.now()).build();

        var fh = familieHendelse().barn(List.of(fhBarn))
            .termindato(TERMINDATO)
            .antallBarn(ANTALL_BARN)
            .build();

        var behandlingsres = behandlingsresultat()
            .behandlingResultatType(BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType.INNVILGET)
            .build();

        return defaultBuilder()
            .uuid(UUID.randomUUID())
            .behandlingType(BrevGrunnlagDto.BehandlingType.REVURDERING)
            .behandlingsresultat(behandlingsres)
            .fagsakYtelseType(ytelseType)
            .familieHendelse(fh)
            .build();
    }

    private DokumentHendelse lagDokumentHendelse(RevurderingÅrsak varslingÅrsak) {
        return lagStandardHendelseBuilder().medRevurderingÅrsak(varslingÅrsak).build();
    }
}
