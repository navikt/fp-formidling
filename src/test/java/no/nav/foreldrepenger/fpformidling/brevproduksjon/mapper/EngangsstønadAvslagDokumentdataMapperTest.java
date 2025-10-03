package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Vilkår;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.pdl.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;
import no.nav.foreldrepenger.fpformidling.typer.PersonIdent;

@ExtendWith(MockitoExtension.class)
class EngangsstønadAvslagDokumentdataMapperTest {
    private static final UUID ID = UUID.randomUUID();
    private static final AktørId AKTØR_ID = new AktørId("2222222222222");

    private DokumentFelles dokumentFelles;
    private DokumentHendelse dokumentHendelse;

    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);
    @Mock
    private PersonAdapter personAdapter = mock(PersonAdapter.class);

    private EngangsstønadAvslagDokumentdataMapper engangsstønadAvslagDokumentdataMapper;

    @BeforeEach
    void setUp() {
        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles();
        dokumentHendelse = lagStandardHendelseBuilder().medFritekst(null).medDokumentMal(DokumentMal.ENGANGSSTØNAD_AVSLAG).build();

        var personinfo = Personinfo.getbuilder(AKTØR_ID)
            .medPersonIdent(new PersonIdent("9999999999"))
            .medNavn("Nav Navesen")
            .medNavBrukerKjønn(NavBrukerKjønn.KVINNE)
            .build();
        lenient().when(personAdapter.hentBrukerForAktør(any(), any())).thenReturn(Optional.of(personinfo));

        engangsstønadAvslagDokumentdataMapper = new EngangsstønadAvslagDokumentdataMapper(DatamapperTestUtil.getBrevParametere(),
            domeneobjektProvider, personAdapter);
    }

    @Test
    void mapTilDokumentdata_avslag_ESFB_søknadsfrist_med_Fritekst_mappes_ok() {
        //Arrange
        var fagsak = opprettFagsak(RelasjonsRolleType.MORA);
        var familieHendelse = new FamilieHendelse(List.of(), LocalDate.now(), 1, null);
        var vilkårFraBehandling = List.of(new Vilkår(VilkårType.SØKNADSFRISTVILKÅRET));

        var avslagsfritekst = "Vi har ikke motatt informasjon som begrunner at du ikke har kunnet søke i tide. Derfor avslås saken.";

        var avslagESFB = opprettBehandlingBuilder(Avslagsårsak.SØKT_FOR_SENT, avslagsfritekst, fagsak).medFamilieHendelse(familieHendelse)
            .medVilkår(vilkårFraBehandling)
            .build();


        //Act
        var avslagDokumentdata = engangsstønadAvslagDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, avslagESFB, false);

        //Verify
        assertThat(avslagDokumentdata.getAvslagÅrsak()).isEqualTo(Avslagsårsak.SØKT_FOR_SENT.name());
        assertThat(avslagDokumentdata.getAntallBarn()).isEqualTo(1);
        assertThat(avslagDokumentdata.getFørstegangsbehandling()).isTrue();
        assertThat(avslagDokumentdata.getGjelderFødsel()).isTrue();
        assertThat(avslagDokumentdata.getRelasjonsRolle()).isEqualTo(RelasjonsRolleType.MORA.getKode());
        assertThat(avslagDokumentdata.getVilkårTyper()).hasSize(1);
        assertThat(avslagDokumentdata.getVilkårTyper()).containsExactly("FP_VK_3");
        assertThat(avslagDokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(avslagsfritekst));
    }

    @Test
    void mapAvslagsårsakerBrev_mapper_riktig() {
        var avslagsårsak = engangsstønadAvslagDokumentdataMapper.mapAvslagsårsakerBrev(Avslagsårsak.SØKER_ER_MEDMOR);

        assertThat(avslagsårsak).isEqualTo("IKKE_ALENEOMSORG");
    }

    @Test
    void mapVilkårTIlBrev_skal_mappe_riktig_vilkår_string() {
        var vilkårFraBehandling = List.of(new Vilkår(VilkårType.FØDSELSVILKÅRET_MOR));
        var fbbehandling = opprettBehandling(Avslagsårsak.SØKER_ER_MEDMOR, null, null);

        var vilkårTilBrev = engangsstønadAvslagDokumentdataMapper.utledVilkårTilBrev(vilkårFraBehandling, Avslagsårsak.SØKER_ER_MEDMOR, fbbehandling);

        assertThat(vilkårTilBrev).hasSize(1);
        assertThat(vilkårTilBrev.get(0)).isEqualTo("FPVK1_4");
    }

    @Test
    void utledRelasjonsRolle_skal_utlede_riktig_rolle() {
        var fagsak = opprettFagsak(RelasjonsRolleType.SAMBOER);
        var rolle = engangsstønadAvslagDokumentdataMapper.utledRelasjonsRolle(fagsak);

        assertThat(rolle).isEqualTo(RelasjonsRolleType.MEDMOR.getKode());
    }

    private Behandling.Builder opprettBehandlingBuilder(Avslagsårsak avslagsårsak, String avslagsfritekst, FagsakBackend fagsak) {
        var behandlingresultat = Behandlingsresultat.builder()
            .medAvslagsårsak(avslagsårsak)
            .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT);

        if (avslagsfritekst != null) {
            behandlingresultat.medAvslagarsakFritekst(avslagsfritekst);
        }
        var behandlingBuilder = Behandling.builder()
            .medUuid(EngangsstønadAvslagDokumentdataMapperTest.ID)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medBehandlingsresultat(behandlingresultat.build())
            .medFagsakBackend(fagsak);

        if (fagsak != null) {
            behandlingBuilder.medFagsakBackend(fagsak);
        }
        return behandlingBuilder;
    }

    private Behandling opprettBehandling(Avslagsårsak avslagsårsak, String avslagsfritekst, FagsakBackend fagsak) {
        return opprettBehandlingBuilder(avslagsårsak, avslagsfritekst, fagsak).build();
    }

    private FagsakBackend opprettFagsak(RelasjonsRolleType relasjonsRolleType) {
        return FagsakBackend.ny()
            .medAktørId(AKTØR_ID)
            .medSaksnummer("123456")
            .medBrukerRolle(relasjonsRolleType)
            .medFagsakYtelseType(FagsakYtelseType.ENGANGSTØNAD)
            .build();
    }
}
