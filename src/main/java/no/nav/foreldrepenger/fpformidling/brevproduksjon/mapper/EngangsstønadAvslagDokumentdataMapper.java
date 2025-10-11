package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.domene.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.Fagsak;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EngangsstønadAvslagDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.pdl.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.ENGANGSSTØNAD_AVSLAG)
public class EngangsstønadAvslagDokumentdataMapper implements DokumentdataMapper {
    private BrevParametere brevParametere;
    private PersonAdapter personAdapter;

    EngangsstønadAvslagDokumentdataMapper() {
        // CDI
    }

    @Inject
    public EngangsstønadAvslagDokumentdataMapper(BrevParametere brevParametere,
                                                 PersonAdapter personAdapter) {
        this.brevParametere = brevParametere;
        this.personAdapter = personAdapter;
    }

    @Override
    public String getTemplateNavn() {
        return "engangsstonad-avslag";
    }

    @Override
    public EngangsstønadAvslagDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                              DokumentHendelse hendelse,
                                                              Behandling behandling,
                                                              boolean erUtkast) {

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(
            dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), dokumentFelles.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        FritekstDto.fra(hendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        var familieHendelse = behandling.getFamilieHendelse();

        var dokumentdataBuilder = EngangsstønadAvslagDokumentdata.ny()
            .medAvslagsÅrsak(mapAvslagsårsakerBrev(behandling.getBehandlingsresultat().getAvslagsårsak()))
            .medFelles(fellesBuilder.build())
            .medFørstegangsbehandling(behandling.erFørstegangssøknad())
            .medGjelderFødsel(familieHendelse.gjelderFødsel())
            .medRelasjonsRolle(utledRelasjonsRolle(behandling.getFagsak()))
            .medVilkårTyper(utledVilkårTilBrev(behandling.getVilkårTyper(), behandling.getBehandlingsresultat().getAvslagsårsak(), behandling))
            .medAntallBarn(familieHendelse.antallBarn())
            .medMedlemskapFom(formaterDato(behandling.getMedlemskapFom(), dokumentFelles.getSpråkkode()))
            .medKlagefristUker(brevParametere.getKlagefristUker());

        utledAvslagsgrunnHvisMedlVilkår(behandling.getVilkårTyper(), behandling.getBehandlingsresultat().getAvslagsårsak(),
            isSkjæringstidspunktPassert(familieHendelse), familieHendelse.gjelderFødsel()).ifPresent(dokumentdataBuilder::medAvslagMedlemskap);

        return dokumentdataBuilder.build();
    }

    private boolean isSkjæringstidspunktPassert(FamilieHendelse familieHendelse) {
        var stp = familieHendelse.omsorgsovertakelse() != null ? familieHendelse.omsorgsovertakelse() : familieHendelse.tidligstFødselsdato()
            .orElse(familieHendelse.termindato());
        return stp.isBefore(LocalDate.now());
    }

    String utledRelasjonsRolle(Fagsak fagsak) {
        if (!RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return hentKjønnOgMapRelasjonsrolle(fagsak.getYtelseType(), fagsak.getAktørId());
        } else {
            return fagsak.getRelasjonsRolleType().toString();
        }
    }

    private String hentKjønnOgMapRelasjonsrolle(FagsakYtelseType ytelseType, AktørId aktørId) {
        var kjønn = personAdapter.hentBrukerForAktør(ytelseType, aktørId).map(Personinfo::getKjønn).orElseThrow();
        return NavBrukerKjønn.MANN.equals(kjønn) ? RelasjonsRolleType.FARA.getKode() : RelasjonsRolleType.MEDMOR.getKode();
    }

    List<String> utledVilkårTilBrev(Collection<VilkårType> vilkårFraBehandling, Avslagsårsak avslagsÅrsakKode, Behandling behandling) {
        List<String> vilkårTilBrev = new ArrayList<>();
        FellesMapper.vilkårFraAvslagsårsak(FagsakYtelseType.ENGANGSTØNAD, vilkårFraBehandling, avslagsÅrsakKode)
            .forEach(vt -> vilkårTilBrev.add(mapVilkårBrev(vt, behandling)));
        if (vilkårTilBrev.isEmpty()) {
            throw new IllegalArgumentException("Utviklerfeil: Brev mangler vilkår for avslagskode " + avslagsÅrsakKode);
        }
        return vilkårTilBrev;
    }

    private String mapVilkårBrev(VilkårType vilkårType, Behandling behandling) {
        if ((VilkårType.ADOPSJONSVILKÅRET_ENGANGSSTØNAD.equals(vilkårType) || VilkårType.OMSORGSOVERTAKELSEVILKÅR.equals(vilkårType)
            || VilkårType.FØDSELSVILKÅRET_MOR.equals(vilkårType)) && !behandling.erRevurdering()) {
            return "FPVK1_4";
        } else if ((VilkårType.MEDLEMSKAPSVILKÅRET.equals(vilkårType) || VilkårType.MEDLEMSKAPSVILKÅRET_FORUTGÅENDE.equals(vilkårType))
            && behandling.erFørstegangssøknad()) {
            return "FPVK2_FB";
        } else if ((VilkårType.MEDLEMSKAPSVILKÅRET.equals(vilkårType) || VilkårType.MEDLEMSKAPSVILKÅRET_FORUTGÅENDE.equals(vilkårType))
            && behandling.erRevurdering()) {
            return "FPVK2_RV";
        } else if (erVilkår213(vilkårType) && behandling.erRevurdering()) {
            return "FPVK1_4_5_8_RV";
        } else {
            return vilkårType.getKode();
        }
    }

    private boolean erVilkår213(VilkårType vilkårType) {
        return vilkårType.equals(VilkårType.ADOPSJONSVILKÅRET_ENGANGSSTØNAD) || VilkårType.FØDSELSVILKÅRET_MOR.equals(vilkårType)
            || VilkårType.OMSORGSVILKÅRET.equals(vilkårType) || VilkårType.FORELDREANSVARSVILKÅRET_2_LEDD.equals(vilkårType)
            || VilkårType.OMSORGSOVERTAKELSEVILKÅR.equals(vilkårType);
    }

    String mapAvslagsårsakerBrev(Avslagsårsak avslagsårsak) {
        if (Avslagsårsak.erAlleredeUtbetaltEngangsstønad(avslagsårsak)) {
            return "ALLEREDE_UTBETALT_ENGANGSSTØNAD";
        } else if (Avslagsårsak.erAlleredeUtbetaltForeldrepenger(avslagsårsak)) {
            return "ALLEREDE_UTBETALT_FORELDREPENGER";
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
            .anyMatch(v -> v.equals(VilkårType.MEDLEMSKAPSVILKÅRET_FORUTGÅENDE)
                && VilkårType.MEDLEMSKAPSVILKÅRET_FORUTGÅENDE.getAvslagsårsaker().contains(årsak))) {
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
