package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static java.util.List.of;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.FRITEKST;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SAKSNUMMER;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_FNR;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.SØKERS_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.VERGES_NAVN;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentData;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardDokumentFelles;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDatoNorsk;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentData;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles.Kopi;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ExtendWith(MockitoExtension.class)
class VarselOmRevurderingDokumentdataMapperTest {

    private static final LocalDate TERMINDATO = LocalDate.now();
    private static final int ANTALL_BARN = 1;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);

    private BrevMapperUtil brevMapperUtil;

    private DokumentData dokumentData;

    private VarselOmRevurderingDokumentdataMapper dokumentdataMapper;

    @BeforeEach
    void before() {
        var brevParametere = new BrevParametere(6, 2, Period.ZERO, Period.ZERO);
        brevMapperUtil = new BrevMapperUtil(brevParametere);
        dokumentData = lagStandardDokumentData(DokumentMalType.VARSEL_OM_REVURDERING);
        dokumentdataMapper = new VarselOmRevurderingDokumentdataMapper(brevMapperUtil, domeneobjektProvider);

        var familieHendelse = opprettFamiliehendelse();
        when(domeneobjektProvider.hentFamiliehendelse(any(Behandling.class))).thenReturn(familieHendelse);
    }

    @Test
    void skal_mappe_felter_for_brev_til_bruker() {
        // Arrange
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.JA, false);
        var dokumentHendelse = lagDokumentHendelse(RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN);

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
        assertThat(varselOmRevurderingDokumentdata.getFelles().getBehandlesAvKA()).isFalse();
        assertThat(varselOmRevurderingDokumentdata.getFelles().getErUtkast()).isFalse();

        assertThat(varselOmRevurderingDokumentdata.getTerminDato()).isEqualTo(formaterDatoNorsk(TERMINDATO));
        assertThat(varselOmRevurderingDokumentdata.getFristDato()).isEqualTo(formaterDatoNorsk(brevMapperUtil.getSvarFrist()));
        assertThat(varselOmRevurderingDokumentdata.getAntallBarn()).isEqualTo(ANTALL_BARN);
        assertThat(varselOmRevurderingDokumentdata.getAdvarselKode()).isEqualTo(RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN.getKode());
        assertThat(varselOmRevurderingDokumentdata.getFlereOpplysninger()).isFalse();
        assertThat(varselOmRevurderingDokumentdata.getKreverSammenhengendeUttak()).isTrue();
    }

    @Test
    void skal_mappe_felter_for_brev_til_verge() {
        // Arrange
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData, Kopi.NEI, true);
        var dokumentHendelse = lagDokumentHendelse(RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN);

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
        var behandling = opprettBehandling();
        var dokumentFelles = lagStandardDokumentFelles(dokumentData);
        var dokumentHendelse = lagDokumentHendelse(RevurderingVarslingÅrsak.ARBEID_I_UTLANDET);

        // Act
        var varselOmRevurderingDokumentdata = dokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, behandling, false);

        // Assert
        assertThat(varselOmRevurderingDokumentdata.getFlereOpplysninger()).isTrue();
    }

    private FamilieHendelse opprettFamiliehendelse() {
        return new FamilieHendelse(FamilieHendelseType.TERMIN, ANTALL_BARN, 0, null, TERMINDATO, null, null, false, false);
    }

    private Behandling opprettBehandling() {
        return Behandling.builder()
            .medUuid(UUID.randomUUID())
            .medBehandlingType(BehandlingType.REVURDERING)
            .medBehandlingÅrsaker(of(BehandlingÅrsak.builder().medBehandlingÅrsakType(BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER).build()))
            .medBehandlingsresultat(Behandlingsresultat.builder().medBehandlingResultatType(BehandlingResultatType.INNVILGET).build())
            .medKreverSammenhengendeUttak(true)
            .build();
    }

    private DokumentHendelse lagDokumentHendelse(RevurderingVarslingÅrsak varslingÅrsak) {
        return lagStandardHendelseBuilder().medRevurderingVarslingÅrsak(varslingÅrsak).build();
    }
}
