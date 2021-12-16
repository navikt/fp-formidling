package no.nav.foreldrepenger.melding.brevmapper.brev.avslagfp;

import static no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper.finnSøknadsdatoFraMottatteDokumenter;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.Tuple;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.avslagfp.AvslåttPeriode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.avslagfp.ForeldrepengerAvslagDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.FORELDREPENGER_AVSLAG)
public class ForeldrepengerAvslagDokumentdataMapper implements DokumentdataMapper {

    private static final Map<RelasjonsRolleType, String> relasjonskodeTypeMap;
    static {
        relasjonskodeTypeMap = new HashMap<>();
        relasjonskodeTypeMap.put(RelasjonsRolleType.MORA, "MOR");
        relasjonskodeTypeMap.put(RelasjonsRolleType.FARA, "FAR");
        relasjonskodeTypeMap.put(RelasjonsRolleType.MEDMOR, "MEDMOR");
    }

    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    ForeldrepengerAvslagDokumentdataMapper() {
        //CDI
    }

    @Inject
    public ForeldrepengerAvslagDokumentdataMapper(BrevParametere brevParametere,
                                                  DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-avslag";
    }

    @Override
    public ForeldrepengerAvslagDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse,
                                                               Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, dokumentHendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        Fritekst.fra(dokumentHendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        FamilieHendelse familiehendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Optional<Beregningsgrunnlag> beregningsgrunnlagOpt = domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(behandling);
        long halvG = BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlagOpt);
        Optional<UttakResultatPerioder> uttakResultatPerioder = domeneobjektProvider.hentUttaksresultatHvisFinnes(behandling);

        var dokumentdataBuilder = ForeldrepengerAvslagDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medRelasjonskode(finnRelasjonskode(fagsak))
                .medMottattDato(formaterDato(finnSøknadsdatoFraMottatteDokumenter(behandling, mottatteDokumenter), behandling.getSpråkkode()))
                .medGjelderFødsel(familiehendelse.isGjelderFødsel())
                .medBarnErFødt(familiehendelse.isBarnErFødt())
                .medAnnenForelderHarRett(uttakResultatPerioder.map(UttakResultatPerioder::isAnnenForelderHarRett).orElse(false))
                .medAntallBarn(familiehendelse.getAntallBarn().intValue())
                .medHalvG(halvG)
                .medKlagefristUker(brevParametere.getKlagefristUker())
                .medKreverSammenhengendeUttak(domeneobjektProvider.kreverSammenhengendeUttak(behandling));

        mapAvslåttePerioder(behandling, dokumentdataBuilder, uttakResultatPerioder);

        return dokumentdataBuilder.build();
    }

    private void mapAvslåttePerioder(Behandling behandling, ForeldrepengerAvslagDokumentdata.Builder dokumentdataBuilder,
                                     Optional<UttakResultatPerioder> uttakResultatPerioder) {
        Optional<BeregningsresultatFP> beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFPHvisFinnes(behandling);
        Tuple<List<AvslåttPeriode>, String> avslåttePerioderOgLovhjemmel = AvslåttPeriodeMapper.mapAvslåttePerioderOgLovhjemmel(
                behandling,
                beregningsresultatFP.map(BeregningsresultatFP::getBeregningsresultatPerioder).orElse(Collections.emptyList()),
                uttakResultatPerioder);

        dokumentdataBuilder.medLovhjemmelForAvslag(avslåttePerioderOgLovhjemmel.element2());
        dokumentdataBuilder.medAvslåttePerioder(avslåttePerioderOgLovhjemmel.element1());
    }

    private String finnRelasjonskode(FagsakBackend fagsak) {
        if (RelasjonsRolleType.erRegistrertForeldre(fagsak.getRelasjonsRolleType())) {
            return relasjonskodeTypeMap.get(fagsak.getRelasjonsRolleType());
        }
        return "ANNET";
    }
}