package no.nav.foreldrepenger.melding.brevmapper.brev;

import no.nav.foreldrepenger.melding.aktør.Personinfo;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.EngangsstønadAvslagDokumentData;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.typer.AktørId;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.vilkår.Vilkår;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;
import no.nav.foreldrepenger.tps.TpsTjeneste;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.brevSendesTilVerge;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erKopi;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.AVSLAG_ENGANGSSTØNAD)
public class AvslagEngangsstønadDokumentDataMapper implements DokumentdataMapper {
    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;
    private TpsTjeneste tpsTjeneste;

    AvslagEngangsstønadDokumentDataMapper() {
        //CDI
    }

    @Inject
    public AvslagEngangsstønadDokumentDataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider, TpsTjeneste tpsTjeneste) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
        this.tpsTjeneste = tpsTjeneste;
    }

    @Override
    public String getTemplateNavn() {
        return "engangsstonad-avslag";
    }

    @Override
    public EngangsstønadAvslagDokumentData mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {
        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        List<Vilkår> vilkår = domeneobjektProvider.hentVilkår(behandling);

        var fellesDataBuilder = FellesDokumentdata.ny()
                .medSøkerNavn(dokumentFelles.getSakspartNavn())
                .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
                .medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato()) : null)
                .medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet())
                .medHarVerge(dokumentFelles.getErKopi().isPresent())
                .medErKopi(dokumentFelles.getErKopi().isPresent() && erKopi(dokumentFelles.getErKopi().get()))
                .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi());

        avklarFritekst(hendelse, behandling).ifPresent(fellesDataBuilder::medFritekst);

        if (brevSendesTilVerge(dokumentFelles)) {
            fellesDataBuilder.medMottakerNavn(dokumentFelles.getMottakerNavn());
        }


        var avslagsBuilder = EngangsstønadAvslagDokumentData.ny()
                .medAvslagsÅrsaker(mapAvslagsårsakerBrev(behandling.getBehandlingsresultat().getAvslagsårsak()))
                .medFelles(fellesDataBuilder.build())
                .medFørstegangBehandling(behandling.erFørstegangssøknad())
                .medGjelderFødsel(familieHendelse.isGjelderFødsel())
                .medRelasjonsRolle(utledRelasjonsRolle(behandling.getFagsakBackend()))
                .medVilkårTyper(utledVilkårTilBrev(vilkår, behandling.getBehandlingsresultat().getAvslagsårsak(), behandling))
                .medAntallBarn(familieHendelse.getAntallBarn().intValue())
                .medKlagefristUker(brevParametere.getKlagefristUker());

        utledAvslagsgrunnHvisMedlVilkår(behandling.getBehandlingsresultat().getAvslagsårsak(), isSkjæringstidspunktPassert(familieHendelse),familieHendelse.isGjelderFødsel()).ifPresent(avslagsBuilder::medAvslagMedlemskap);

        return avslagsBuilder.build();
    }
    private boolean isSkjæringstidspunktPassert(FamilieHendelse familieHendelse){
        return familieHendelse.getSkjæringstidspunkt().isPresent() && familieHendelse.getSkjæringstidspunkt().get().isBefore(LocalDate.now());
    }

    String utledRelasjonsRolle(FagsakBackend fagsak) {
        if(!RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return hentKjønnOgMapRelasjonsrolle(fagsak.getAktørId());
        } else {
            return fagsak.getRelasjonsRolleType().toString();
        }
    }

    private String hentKjønnOgMapRelasjonsrolle(AktørId aktørId) {
        var kjønn = tpsTjeneste.hentBrukerForAktør(aktørId).map(Personinfo::getKjønn).orElseThrow();
        return NavBrukerKjønn.MANN.equals(kjønn) ? RelasjonsRolleType.FARA.getKode() : RelasjonsRolleType.MEDMOR.getKode();
    }

    List<String> utledVilkårTilBrev(List<Vilkår> vilkårFraBehandling, Avslagsårsak avslagsÅrsakKode, Behandling behandling) {
        Set<VilkårType> vilkårTyper = VilkårType.getVilkårTyper(avslagsÅrsakKode);
        List<String> vilkårTilBrev = new ArrayList<>();
        vilkårFraBehandling.stream()
            .filter(v-> vilkårTyper.contains(v.getVilkårType()))
            .map(Vilkår::getVilkårType)
            .forEach(vt-> vilkårTilBrev.add(mapVilkårBrev(vt, behandling)));
        if(vilkårTilBrev.isEmpty()) {
            throw new IllegalArgumentException("Utviklerfeil: Brev mangler vilkår for avslagskode "+avslagsÅrsakKode);
        }
        return vilkårTilBrev;
    }

    private String mapVilkårBrev(VilkårType vilkårType, Behandling behandling) {
        if(VilkårType.ADOPSJONSVILKÅRET_ENGANGSSTØNAD.equals(vilkårType) || VilkårType.FØDSELSVILKÅRET_MOR.equals(vilkårType) && !behandling.erRevurdering()) {
            return "FPVK1_4";
        } else if(VilkårType.MEDLEMSKAPSVILKÅRET.equals(vilkårType) && behandling.erFørstegangssøknad()) {
            return "FPVK2_FB";
        } else if(VilkårType.MEDLEMSKAPSVILKÅRET.equals(vilkårType) && behandling.erRevurdering()) {
            return "FPVK2_RV";
        } else if( erVilkår213(vilkårType) && behandling.erRevurdering()) {
            return "FPVK1_4_5_8_RV";
        } else {
            return vilkårType.getKode();
        }
    }

    private boolean erVilkår213(VilkårType vilkårType) {
        return vilkårType.equals(VilkårType.ADOPSJONSVILKÅRET_ENGANGSSTØNAD)
                || VilkårType.FØDSELSVILKÅRET_MOR.equals(vilkårType)
                || VilkårType.OMSORGSVILKÅRET.equals(vilkårType)
                || VilkårType.FORELDREANSVARSVILKÅRET_2_LEDD.equals(vilkårType);
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

    private Optional<String> utledAvslagsgrunnHvisMedlVilkår(Avslagsårsak årsak, boolean skjæringstispunktPassert, boolean gjelderFødsel) {

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
