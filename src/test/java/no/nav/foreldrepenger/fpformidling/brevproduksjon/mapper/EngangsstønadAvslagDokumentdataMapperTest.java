package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil.lagStandardHendelseBuilder;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.familieHendelse;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagBuilders.fritekst;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.FamilieHendelse;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DatamapperTestUtil;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMal;

@ExtendWith(MockitoExtension.class)
class EngangsstønadAvslagDokumentdataMapperTest {

    private DokumentFelles dokumentFelles;
    private DokumentHendelse dokumentHendelse;

    private EngangsstønadAvslagDokumentdataMapper engangsstønadAvslagDokumentdataMapper;

    @BeforeEach
    void setUp() {
        dokumentFelles = DatamapperTestUtil.lagStandardDokumentFelles(FagsakYtelseType.FORELDREPENGER);
        dokumentHendelse = lagStandardHendelseBuilder().medFritekst(null).medDokumentMal(DokumentMal.ENGANGSSTØNAD_AVSLAG).build();

        engangsstønadAvslagDokumentdataMapper = new EngangsstønadAvslagDokumentdataMapper(DatamapperTestUtil.getBrevParametere());
    }

    @Test
    void mapTilDokumentdata_avslag_ESFB_søknadsfrist_med_Fritekst_mappes_ok() {
        //Arrange
        var familieHendelse = getFamilieHendelse();
        var vilkårType = Behandlingsresultat.VilkårType.SØKNADSFRISTVILKÅRET;

        var avslagsfritekst = "Vi har ikke motatt informasjon som begrunner at du ikke har kunnet søke i tide. Derfor avslås saken.";

        var avslagESFB = opprettBrevGrunnlag(Avslagsårsak.SØKT_FOR_SENT, avslagsfritekst, familieHendelse, vilkårType);

        //Act
        var avslagDokumentdata = engangsstønadAvslagDokumentdataMapper.mapTilDokumentdata(dokumentFelles, dokumentHendelse, avslagESFB, false);

        //Verify
        assertThat(avslagDokumentdata.getAvslagÅrsak()).isEqualTo(Avslagsårsak.SØKT_FOR_SENT.name());
        assertThat(avslagDokumentdata.getAntallBarn()).isEqualTo(1);
        assertThat(avslagDokumentdata.getFørstegangsbehandling()).isTrue();
        assertThat(avslagDokumentdata.getGjelderFødsel()).isTrue();
        assertThat(avslagDokumentdata.getRelasjonsRolle()).isEqualTo(RelasjonsRolleType.MORA);
        assertThat(avslagDokumentdata.getVilkårTyper()).hasSize(1);
        assertThat(avslagDokumentdata.getVilkårTyper()).containsExactly("FP_VK_3");
        assertThat(avslagDokumentdata.getFelles().getFritekst()).isEqualTo(FritekstDto.fra(avslagsfritekst));
    }

    private static FamilieHendelse getFamilieHendelse() {
        return familieHendelse()
            .barn(List.of())
            .termindato(LocalDate.now())
            .antallBarn(1)
            .build();
    }

    @Test
    void mapAvslagsårsakerBrev_mapper_riktig() {
        var avslagsårsak = engangsstønadAvslagDokumentdataMapper.mapAvslagsårsakerBrev(Avslagsårsak.SØKER_ER_MEDMOR, false);

        assertThat(avslagsårsak).isEqualTo("IKKE_ALENEOMSORG");
    }

    @Test
    void mapVilkårTIlBrev_skal_mappe_riktig_vilkår_string() {
        var vilkårFraBehandling = List.of(VilkårType.FØDSELSVILKÅRET_MOR);
        var fbbehandling = opprettBrevGrunnlag(Avslagsårsak.SØKER_ER_MEDMOR, null, getFamilieHendelse(),
            Behandlingsresultat.VilkårType.FØDSELSVILKÅRET_MOR);

        var vilkårTilBrev = engangsstønadAvslagDokumentdataMapper.utledVilkårTilBrev(vilkårFraBehandling, Avslagsårsak.SØKER_ER_MEDMOR, fbbehandling);

        assertThat(vilkårTilBrev).hasSize(1);
        assertThat(vilkårTilBrev.getFirst()).isEqualTo("FPVK1_4");
    }

    private BrevGrunnlagDto opprettBrevGrunnlag(Avslagsårsak avslagsårsak,
                                                String avslagsfritekst,
                                                FamilieHendelse familieHendelse,
                                                Behandlingsresultat.VilkårType vilkårType) {
        return DatamapperTestUtil.defaultBuilder()
            .fagsakYtelseType(BrevGrunnlagDto.FagsakYtelseType.ENGANGSTØNAD)
            .behandlingType(BrevGrunnlagDto.BehandlingType.FØRSTEGANGSSØKNAD)
            .familieHendelse(familieHendelse)
            .behandlingsresultat(behandlingsresultat()
                .behandlingResultatType(Behandlingsresultat.BehandlingResultatType.AVSLÅTT)
                .avslagsårsak(avslagsårsak.getKode())
                .fritekst(fritekst()
                    .overskrift("avslag")
                    .avslagsarsakFritekst(avslagsfritekst)
                    .build())
                .vilkårTyper(List.of(vilkårType))
                .build())
            .build();
    }
}
