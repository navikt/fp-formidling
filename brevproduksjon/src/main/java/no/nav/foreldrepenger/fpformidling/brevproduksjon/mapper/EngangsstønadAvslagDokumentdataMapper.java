package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.PersonAdapter;
import no.nav.foreldrepenger.fpformidling.aktør.Personinfo;
import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.EngangsstønadAvslagDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.fpformidling.personopplysning.NavBrukerKjønn;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.typer.AktørId;
import no.nav.foreldrepenger.fpformidling.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.vilkår.Vilkår;
import no.nav.foreldrepenger.fpformidling.vilkår.VilkårType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.ENGANGSSTØNAD_AVSLAG)
public class EngangsstønadAvslagDokumentdataMapper implements DokumentdataMapper {
    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;
    private PersonAdapter personAdapter;

    EngangsstønadAvslagDokumentdataMapper() {
        // CDI
    }

    @Inject
    public EngangsstønadAvslagDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider,
                                                 PersonAdapter personAdapter) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
        this.personAdapter = personAdapter;
    }

    @Override
    public String getTemplateNavn() {
        return "engangsstonad-avslag";
    }

    @Override
    public EngangsstønadAvslagDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                              Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(
                dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        Fritekst.fra(hendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        List<Vilkår> vilkår = domeneobjektProvider.hentVilkår(behandling);

        var dokumentdataBuilder = EngangsstønadAvslagDokumentdata.ny()
                .medAvslagsÅrsak(mapAvslagsårsakerBrev(behandling.getBehandlingsresultat().getAvslagsårsak()))
                .medFelles(fellesBuilder.build())
                .medFørstegangsbehandling(behandling.erFørstegangssøknad())
                .medGjelderFødsel(familieHendelse.isGjelderFødsel())
                .medRelasjonsRolle(utledRelasjonsRolle(behandling.getFagsakBackend()))
                .medVilkårTyper(utledVilkårTilBrev(vilkår, behandling.getBehandlingsresultat().getAvslagsårsak(), behandling))
                .medAntallBarn(familieHendelse.getAntallBarn().intValue())
                .medKlagefristUker(brevParametere.getKlagefristUker());

        utledAvslagsgrunnHvisMedlVilkår(behandling.getBehandlingsresultat().getAvslagsårsak(), isSkjæringstidspunktPassert(familieHendelse),
                familieHendelse.isGjelderFødsel()).ifPresent(dokumentdataBuilder::medAvslagMedlemskap);

        return dokumentdataBuilder.build();
    }

    private boolean isSkjæringstidspunktPassert(FamilieHendelse familieHendelse) {
        return familieHendelse.getSkjæringstidspunkt().isPresent() && familieHendelse.getSkjæringstidspunkt().get().isBefore(LocalDate.now());
    }

    String utledRelasjonsRolle(FagsakBackend fagsak) {
        if (!RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return hentKjønnOgMapRelasjonsrolle(fagsak.getAktørId());
        } else {
            return fagsak.getRelasjonsRolleType().toString();
        }
    }

    private String hentKjønnOgMapRelasjonsrolle(AktørId aktørId) {
        var kjønn = personAdapter.hentBrukerForAktør(aktørId).map(Personinfo::getKjønn).orElseThrow();
        return NavBrukerKjønn.MANN.equals(kjønn) ? RelasjonsRolleType.FARA.getKode() : RelasjonsRolleType.MEDMOR.getKode();
    }

    List<String> utledVilkårTilBrev(List<Vilkår> vilkårFraBehandling, Avslagsårsak avslagsÅrsakKode, Behandling behandling) {
        Set<VilkårType> vilkårTyper = VilkårType.getVilkårTyper(avslagsÅrsakKode);
        List<String> vilkårTilBrev = new ArrayList<>();
        vilkårFraBehandling.stream()
                .filter(v -> vilkårTyper.contains(v.vilkårType()))
                .map(Vilkår::vilkårType)
                .forEach(vt -> vilkårTilBrev.add(mapVilkårBrev(vt, behandling)));
        if (vilkårTilBrev.isEmpty()) {
            throw new IllegalArgumentException("Utviklerfeil: Brev mangler vilkår for avslagskode " + avslagsÅrsakKode);
        }
        return vilkårTilBrev;
    }

    private String mapVilkårBrev(VilkårType vilkårType, Behandling behandling) {
        if (VilkårType.ADOPSJONSVILKÅRET_ENGANGSSTØNAD.equals(vilkårType)
                || VilkårType.FØDSELSVILKÅRET_MOR.equals(vilkårType) && !behandling.erRevurdering()) {
            return "FPVK1_4";
        } else if (VilkårType.MEDLEMSKAPSVILKÅRET.equals(vilkårType) && behandling.erFørstegangssøknad()) {
            return "FPVK2_FB";
        } else if (VilkårType.MEDLEMSKAPSVILKÅRET.equals(vilkårType) && behandling.erRevurdering()) {
            return "FPVK2_RV";
        } else if (erVilkår213(vilkårType) && behandling.erRevurdering()) {
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
