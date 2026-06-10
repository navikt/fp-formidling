package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Barn;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.BehandlingType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper.mapRelasjonsRolle;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EngangsstønadAvslagDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.ENGANGSSTØNAD_AVSLAG)
public class EngangsstønadAvslagDokumentdataMapper implements DokumentdataMapper {

    @Override
    public String getTemplateNavn() {
        return "engangsstonad-avslag";
    }

    @Override
    public EngangsstønadAvslagDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                              DokumentHendelse hendelse,
                                                              BrevGrunnlagDto behandling,
                                                              boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), dokumentFelles.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        FritekstDto.fraFritekst(hendelse, behandling.behandlingsresultat().fritekst()).ifPresent(fellesBuilder::medFritekst);

        var familieHendelse = behandling.familieHendelse();

        var avslagsårsak = Avslagsårsak.fraKode(behandling.behandlingsresultat().avslagsårsak());
        var vilkårTyper = behandling.behandlingsresultat().vilkårTyper().stream().map(KodeverkMapper::mapVilkårType).toList();
        var dokumentdataBuilder = EngangsstønadAvslagDokumentdata.ny()
            .medAvslagsÅrsak(mapAvslagsårsakerBrev(avslagsårsak, BrevGrunnlagDto.RelasjonsRolleType.MORA.equals(behandling.relasjonsRolleType())))
            .medFelles(fellesBuilder.build())
            .medFørstegangsbehandling(behandling.behandlingType() == BehandlingType.FØRSTEGANGSSØKNAD)
            .medGjelderFødsel(familieHendelse.gjelderFødsel())
            .medRelasjonsRolle(mapRelasjonsRolle(behandling.relasjonsRolleType()))
            .medVilkårTyper(utledVilkårTilBrev(vilkårTyper, avslagsårsak, behandling))
            .medAntallBarn(familieHendelse.antallBarn())
            .medMedlemskapFom(formaterDato(behandling.behandlingsresultat().medlemskapFom(), dokumentFelles.getSpråkkode()))
            .medKlagefristUker(BrevParametere.getKlagefristUker());

        utledAvslagsgrunnHvisMedlVilkår(vilkårTyper, avslagsårsak, isSkjæringstidspunktPassert(familieHendelse),
            familieHendelse.gjelderFødsel()).ifPresent(dokumentdataBuilder::medAvslagMedlemskap);

        return dokumentdataBuilder.build();
    }

    private boolean isSkjæringstidspunktPassert(BrevGrunnlagDto.FamilieHendelse familieHendelse) {
        var stp = familieHendelse.omsorgsovertakelse() != null ? familieHendelse.omsorgsovertakelse() : familieHendelse.barn().stream().map(
                Barn::fødselsdato).min(LocalDate::compareTo)
            .orElse(familieHendelse.termindato());
        return stp.isBefore(LocalDate.now());
    }

    List<String> utledVilkårTilBrev(List<VilkårType> vilkårFraBehandling, Avslagsårsak avslagsÅrsakKode, BrevGrunnlagDto behandling) {
        List<String> vilkårTilBrev = new ArrayList<>();
        FellesMapper.vilkårFraAvslagsårsak(FagsakYtelseType.ENGANGSTØNAD, vilkårFraBehandling, avslagsÅrsakKode)
            .forEach(vt -> vilkårTilBrev.add(mapVilkårBrev(vt, behandling)));
        if (vilkårTilBrev.isEmpty()) {
            throw new IllegalArgumentException("Utviklerfeil: Brev mangler vilkår for avslagskode " + avslagsÅrsakKode);
        }
        return vilkårTilBrev;
    }

    private String mapVilkårBrev(VilkårType vilkårType, BrevGrunnlagDto behandling) {
        if ((VilkårType.OMSORGSOVERTAKELSEVILKÅR.equals(vilkårType) || VilkårType.FØDSELSVILKÅRET_MOR.equals(vilkårType)) && !behandling.erRevurdering()) {
            return "FPVK1_4";
        } else {
            var erMedlemskapsvilkåret = VilkårType.MEDLEMSKAPSVILKÅRET.equals(vilkårType) || VilkårType.MEDLEMSKAPSVILKÅRET_FORUTGÅENDE.equals(vilkårType);
            if (erMedlemskapsvilkåret && behandling.erFørstegangssøknad()) {
                return "FPVK2_FB";
            } else if (erMedlemskapsvilkåret && behandling.erRevurdering()) {
                return "FPVK2_RV";
            } else if (erVilkår213(vilkårType) && behandling.erRevurdering()) {
                return "FPVK1_4_5_8_RV";
            } else {
                return vilkårType.getKode();
            }
        }
    }

    private boolean erVilkår213(VilkårType vilkårType) {
        return VilkårType.FØDSELSVILKÅRET_MOR.equals(vilkårType) || VilkårType.OMSORGSOVERTAKELSEVILKÅR.equals(vilkårType);
    }

    String mapAvslagsårsakerBrev(Avslagsårsak avslagsårsak, boolean erMor) {
        if (Avslagsårsak.ENGANGSTØNAD_ER_ALLEREDE_UTBETAL_TIL_MOR.equals(avslagsårsak)) {
            return erMor ? "ALLEREDE_UTBETALT_ENGANGSSTØNAD" : "ANNENFORELDER_UTBETALT_ENGANGSSTØNAD";
        } else if (Avslagsårsak.ENGANGSSTØNAD_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR.equals(avslagsårsak)) {
            return erMor ? "ANNENFORELDER_UTBETALT_ENGANGSSTØNAD" : "ALLEREDE_UTBETALT_ENGANGSSTØNAD";
        } else if (Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_MOR.equals(avslagsårsak)) {
            return erMor ? "ALLEREDE_UTBETALT_FORELDREPENGER" : "ANNENFORELDER_UTBETALT_FORELDREPENGER";
        } else if (Avslagsårsak.FORELDREPENGER_ER_ALLEREDE_UTBETALT_TIL_FAR_MEDMOR.equals(avslagsårsak)) {
            return erMor ? "ANNENFORELDER_UTBETALT_FORELDREPENGER" : "ALLEREDE_UTBETALT_FORELDREPENGER";
        } else if (Avslagsårsak.farHarIkkeAleneomsorg(avslagsårsak)) {
            return "IKKE_ALENEOMSORG";
        } else if (Avslagsårsak.barnIkkeRiktigAlder(avslagsårsak)) {
            return "BARN_IKKE_RIKTIG_ALDER";
        } else if (Avslagsårsak.ikkeBarnetsFar(avslagsårsak)) {
            return "IKKE_BARNETS_FAR";
        } else {
            return (avslagsårsak.name());
        }
    }

    private Optional<String> utledAvslagsgrunnHvisMedlVilkår(Collection<VilkårType> vilkår,
                                                             Avslagsårsak årsak,
                                                             boolean skjæringstispunktPassert,
                                                             boolean gjelderFødsel) {
        if (vilkår.stream()
            .anyMatch(v -> v.equals(VilkårType.MEDLEMSKAPSVILKÅRET_FORUTGÅENDE) && VilkårType.MEDLEMSKAPSVILKÅRET_FORUTGÅENDE.getAvslagsårsaker()
                .contains(årsak))) {
            return Optional.of("IKKE_MEDL_FORUTGÅENDE");
        }
        if (VilkårType.MEDLEMSKAPSVILKÅRET.getAvslagsårsaker().contains(årsak) && !skjæringstispunktPassert) {
            return Optional.of("IKKE_MEDL_FØR_STP");
        } else if (VilkårType.MEDLEMSKAPSVILKÅRET.getAvslagsårsaker().contains(årsak) && skjæringstispunktPassert && gjelderFødsel) {
            return Optional.of("IKKE_MEDL_ETTER_FØDSEL");
        } else if (VilkårType.MEDLEMSKAPSVILKÅRET.getAvslagsårsaker().contains(årsak) && skjæringstispunktPassert && !gjelderFødsel) {
            return Optional.of("IKKE_MEDL_ETTER_OVERTAGELSE");
        } else {
            return Optional.empty();
        }
    }
}
