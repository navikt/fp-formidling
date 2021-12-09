package no.nav.foreldrepenger.melding.brevmapper.brev;

import no.nav.foreldrepenger.PersonAdapter;
import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadAvslagDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static no.nav.foreldrepenger.melding.datamapper.DatamapperTestUtil.lagStandardHendelseBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(DatamapperTestUtil.lagStandardDokumentData(DokumentMalType.ENGANGSSTØNAD_AVSLAG));
        dokumentHendelse = lagStandardHendelseBuilder().medFritekst(null).build();

        Personinfo personinfo = Personinfo.getbuilder(AKTØR_ID)
                .medPersonIdent( new PersonIdent("9999999999"))
                .medNavn("Nav Navesen")
                .medNavBrukerKjønn(NavBrukerKjønn.KVINNE)
                .build();
        lenient().when(personAdapter.hentBrukerForAktør(any())).thenReturn(Optional.of(personinfo));

        engangsstønadAvslagDokumentdataMapper = new EngangsstønadAvslagDokumentdataMapper(DatamapperTestUtil.getBrevParametere(), domeneobjektProvider, personAdapter);
    }

    @Test
    void mapTilDokumentdata_avslag_ESFB_søknadsfrist_med_Fritekst_mappes_ok() {
        //Arrange
        FagsakBackend fagsak = opprettFagsak(RelasjonsRolleType.MORA);
        FamilieHendelse familieHendelse = new FamilieHendelse(BigInteger.ONE, 0, false, true, FamilieHendelseType.TERMIN, new FamilieHendelse.OptionalDatoer(Optional.of(LocalDate.now()), Optional.empty(), Optional.empty(), Optional.empty()));
        List<Vilkår> vilkårFraBehandling = List.of(new Vilkår(VilkårType.SØKNADSFRISTVILKÅRET));

        String avslagsfritekst = "Vi har ikke motatt informasjon som begrunner at du ikke har kunnet søke i tide. Derfor avslås saken.";

        Behandling avslagESFB = opprettBehandling(Avslagsårsak.SØKT_FOR_SENT, avslagsfritekst, fagsak );

        when(domeneobjektProvider.hentFamiliehendelse(avslagESFB)).thenReturn(familieHendelse);
        when(domeneobjektProvider.hentVilkår(avslagESFB)).thenReturn(vilkårFraBehandling);

        //Act
        EngangsstønadAvslagDokumentdata avslagDokumentdata = engangsstønadAvslagDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, avslagESFB, false);

        //Verify
        assertThat(avslagDokumentdata.getAvslagÅrsak()).isEqualTo(Avslagsårsak.SØKT_FOR_SENT.name());
        assertThat(avslagDokumentdata.getAntallBarn()).isEqualTo(1);
        assertThat(avslagDokumentdata.getFørstegangsbehandling()).isTrue();
        assertThat(avslagDokumentdata.getGjelderFødsel()).isTrue();
        assertThat(avslagDokumentdata.getRelasjonsRolle()).isEqualTo(RelasjonsRolleType.MORA.getKode());
        assertThat(avslagDokumentdata.getVilkårTyper()).hasSize(1);
        assertThat(avslagDokumentdata.getVilkårTyper()).containsExactly("FP_VK_3");
        assertThat(avslagDokumentdata.getFelles().getFritekst()).isEqualTo(Fritekst.fra(avslagsfritekst));
    }

    @Test
    void mapAvslagsårsakerBrev_mapper_riktig() {
        String avslagsårsak = engangsstønadAvslagDokumentdataMapper.mapAvslagsårsakerBrev(Avslagsårsak.SØKER_ER_MEDMOR);

        assertThat(avslagsårsak).isEqualTo("IKKE_ALENEOMSORG");
    }

     @Test
    void mapVilkårTIlBrev_skal_mappe_riktig_vilkår_string() {
        List<Vilkår> vilkårFraBehandling = List.of(new Vilkår(VilkårType.FØDSELSVILKÅRET_MOR));
        Behandling fbbehandling = opprettBehandling(Avslagsårsak.SØKER_ER_MEDMOR, null, null);

        List<String> vilkårTilBrev = engangsstønadAvslagDokumentdataMapper.utledVilkårTilBrev(vilkårFraBehandling, Avslagsårsak.SØKER_ER_MEDMOR, fbbehandling);

        assertThat(vilkårTilBrev).hasSize(1);
        assertThat(vilkårTilBrev.get(0)).isEqualTo("FPVK1_4");
    }

    @Test
    void utledRelasjonsRolle_skal_utlede_riktig_rolle() {
        FagsakBackend fagsak = opprettFagsak(RelasjonsRolleType.SAMBOER);
        String rolle = engangsstønadAvslagDokumentdataMapper.utledRelasjonsRolle(fagsak);

        assertThat(rolle).isEqualTo(RelasjonsRolleType.MEDMOR.getKode());
    }

    private Behandling opprettBehandling(Avslagsårsak avslagsårsak, String avslagsfritekst, FagsakBackend fagsak) {
        var behandlingresultat = Behandlingsresultat.builder()
                .medAvslagsårsak(avslagsårsak)
                .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT);

        if (avslagsfritekst != null) {
            behandlingresultat.medAvslagarsakFritekst(avslagsfritekst);
        }
        var behandlingBuilder = Behandling.builder().medUuid(EngangsstønadAvslagDokumentdataMapperTest.ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlingsresultat(behandlingresultat.build())
                .medFagsakBackend(fagsak);

        if (fagsak != null) {
            behandlingBuilder.medFagsakBackend(fagsak);
        }

        return behandlingBuilder.build();
    }

    private FagsakBackend opprettFagsak(RelasjonsRolleType relasjonsRolleType) {
        return FagsakBackend.ny()
                .medAktørId(AKTØR_ID)
                .medSaksnummer("123456")
                .medBrukerRolle(relasjonsRolleType)
                .build();
    }
}
