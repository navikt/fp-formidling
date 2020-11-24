package no.nav.foreldrepenger.melding.brevmapper.brev;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.PersonTjeneste;
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
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadAvslagDokumentData;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingResultatType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.typer.PersonIdent;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;

@ExtendWith(MockitoExtension.class)
class AvslagEngangsstønadDokumentDataMapperTest {
    private AvslagEngangsstønadDokumentDataMapper avslagEngangsstønadDokumentDataMapper;
    private DokumentFelles dokumentFelles;
    private static final long ID = 123456;
    private static final AktørId AKTØR_ID = new AktørId("2222222222222");
    @Mock
    private DomeneobjektProvider domeneobjektProvider = mock(DomeneobjektProvider.class);
    @Mock
    private DokumentHendelse dokumentHendelse = mock(DokumentHendelse.class);
    @Mock
    private PersonTjeneste tpsTjeneste = mock(PersonTjeneste.class);

    @BeforeEach
    void setUp() {
        avslagEngangsstønadDokumentDataMapper = new AvslagEngangsstønadDokumentDataMapper(DatamapperTestUtil.getBrevParametere(), domeneobjektProvider, tpsTjeneste);
        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(DatamapperTestUtil.lagStandardDokumentData(DokumentMalType.AVSLAG_ENGANGSSTØNAD));
        Personinfo personinfo = Personinfo.getbuilder(AKTØR_ID)
                .medPersonIdent( new PersonIdent("9999999999"))
                .medNavn("Nav Navesen")
                .medNavBrukerKjønn(NavBrukerKjønn.KVINNE)
                .build();
        lenient().when(tpsTjeneste.hentBrukerForAktør(any())).thenReturn(Optional.of(personinfo));
    }

    @Test
    void mapTilDokumentData_avslag_ESFB_søknadsfrist_med_Fritekst_mappes_ok() {
        //Arrange
        FagsakBackend fagsak = opprettFagsak(RelasjonsRolleType.MORA);
        FamilieHendelse familieHendelse = new FamilieHendelse(BigInteger.ONE, false, true, FamilieHendelseType.TERMIN, new FamilieHendelse.OptionalDatoer(Optional.of(LocalDate.now()), Optional.empty(), Optional.empty(), Optional.empty()));
        List<Vilkår> vilkårFraBehandling = List.of(new Vilkår(VilkårType.SØKNADSFRISTVILKÅRET));

        String avslagsfritekst = "Vi har ikke motatt informasjon som begrunner at du ikke har kunnet søke i tide. Derfor avslås saken.";

        Behandling avslagESFB = opprettBehandling(Avslagsårsak.SØKT_FOR_SENT, avslagsfritekst, fagsak );

        when(domeneobjektProvider.hentFamiliehendelse(avslagESFB)).thenReturn(familieHendelse);
        when(domeneobjektProvider.hentVilkår(avslagESFB)).thenReturn(vilkårFraBehandling);

        //Act
        EngangsstønadAvslagDokumentData avslagDokumentData = avslagEngangsstønadDokumentDataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, avslagESFB);

        //Verify
        assertThat(avslagDokumentData.getAvslagÅrsak()).isEqualTo(Avslagsårsak.SØKT_FOR_SENT.name());
        assertThat(avslagDokumentData.getAntallBarn()).isEqualTo(1);
        assertThat(avslagDokumentData.getFørstegangsbehandling()).isTrue();
        assertThat(avslagDokumentData.getgjelderFødsel()).isTrue();
        assertThat(avslagDokumentData.getRelasjonsRolle().equals(RelasjonsRolleType.MORA.getKode()));
        assertThat(avslagDokumentData.getvilkårTyper()).hasSize(1);
        assertThat(avslagDokumentData.getvilkårTyper()).containsExactly("FP_VK_3");
        assertThat(avslagDokumentData.felles.getFritekst().equals(avslagsfritekst));
    }

    @Test
    void mapAvslagsårsakerBrev_mapper_riktig() {
        String avslagsårsak = avslagEngangsstønadDokumentDataMapper.mapAvslagsårsakerBrev(Avslagsårsak.SØKER_ER_MEDMOR);

        assertThat(avslagsårsak).isEqualTo("IKKE_ALENEOMSORG");
    }

     @Test
    void mapVilkårTIlBrev_skal_mappe_riktig_vilkår_string() {
        List<Vilkår> vilkårFraBehandling = List.of(new Vilkår(VilkårType.FØDSELSVILKÅRET_MOR));
        Behandling fbbehandling = opprettBehandling(Avslagsårsak.SØKER_ER_MEDMOR, null, null);

        List<String> vilkårTilBrev = avslagEngangsstønadDokumentDataMapper.utledVilkårTilBrev(vilkårFraBehandling, Avslagsårsak.SØKER_ER_MEDMOR, fbbehandling);

        assertThat(vilkårTilBrev).hasSize(1);
        assertThat(vilkårTilBrev.get(0)).isEqualTo("FPVK1_4");
    }

    @Test
    void utledRelasjonsRolle_skal_utlede_riktig_rolle() {
        FagsakBackend fagsak = opprettFagsak(RelasjonsRolleType.SAMBOER);
        String rolle = avslagEngangsstønadDokumentDataMapper.utledRelasjonsRolle(fagsak);

        assertThat(rolle).isEqualTo(RelasjonsRolleType.MEDMOR.getKode());
    }

    private Behandling opprettBehandling(Avslagsårsak avslagsårsak, String avslagsfritekst, FagsakBackend fagsak) {
        var behandlingresultat = Behandlingsresultat.builder()
                .medAvslagsårsak(avslagsårsak)
                .medBehandlingResultatType(BehandlingResultatType.AVSLÅTT);

        if (avslagsfritekst != null) {
            behandlingresultat.medAvslagarsakFritekst(avslagsfritekst);
        }
        var behandlingBuilder = Behandling.builder().medId(AvslagEngangsstønadDokumentDataMapperTest.ID)
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