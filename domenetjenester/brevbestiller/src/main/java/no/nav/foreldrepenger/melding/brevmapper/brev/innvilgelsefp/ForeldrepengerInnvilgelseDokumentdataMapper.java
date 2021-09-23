package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import no.nav.foreldrepenger.melding.aksjonspunkt.Aksjonspunkt;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktDefinisjon;
import no.nav.foreldrepenger.melding.aksjonspunkt.AksjonspunktStatus;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.UttakMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.VurderingsKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;
import no.nav.vedtak.exception.FunksjonellException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.finnBrutto;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.finnSeksG;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.harBruktBruttoBeregningsgrunnlag;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.inntektOverSeksG;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.mapRegelListe;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.finnAntallArbeidsgivere;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.finnDagsats;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.harDelvisRefusjon;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.harFullRefusjon;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.harIngenRefusjon;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.harUtbetaling;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.ForMyeUtbetaltMapper.forMyeUtbetalt;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.StønadskontoMapper.finnDisponibleDager;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.StønadskontoMapper.finnDisponibleFellesDager;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.StønadskontoMapper.finnForeldrepengeperiodenUtvidetUkerHvisFinnes;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.StønadskontoMapper.finnPrematurDagerHvisFinnes;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereAvslag;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereInnvilget;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereNyeOpplysningerUtbet;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereUtbetNårGradering;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereUtbetaling;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallAvslåttePerioder;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallInnvilgedePerioder;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallPerioder;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeFom;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeTom;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnesPeriodeMedIkkeOmsorg;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.FORELDREPENGER_INNVILGELSE)
public class ForeldrepengerInnvilgelseDokumentdataMapper implements DokumentdataMapper {
    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    @Inject
    public ForeldrepengerInnvilgelseDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "innvilget-foreldrepenger";
    }

    @Override
    public ForeldrepengerInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse,
                                                                    Behandling behandling, boolean erUtkast) {
        YtelseFordeling ytelseFordeling = domeneobjektProvider.hentYtelseFordeling(behandling);
        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        Søknad søknad = hentNyesteSøknad(behandling);
        List<Aksjonspunkt> aksjonspunkter = domeneobjektProvider.hentAksjonspunkter(behandling);
        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Optional<FamilieHendelse> originalFamiliehendelse = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)
                .map(domeneobjektProvider::hentFamiliehendelse);
        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        Saldoer saldoer = domeneobjektProvider.hentSaldoer(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, dokumentHendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(
                dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        avklarFritekst(dokumentHendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        List<Utbetalingsperiode> utbetalingsperioder = UtbetalingsperiodeMapper.mapUtbetalingsperioder(
                beregningsresultatFP.getBeregningsresultatPerioder(), uttakResultatPerioder, beregningsgrunnlag.getBeregningsgrunnlagPerioder());
        String konsekvensForInnvilgetYtelse = mapKonsekvensForInnvilgetYtelse(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());
        boolean erInnvilgetRevurdering = erInnvilgetRevurdering(behandling);
        long dagsats = finnDagsats(beregningsresultatFP);

        var dokumentdataBuilder = ForeldrepengerInnvilgelseDokumentdata.ny()
                .medFelles(fellesBuilder.build())

                .medBehandlingType(behandling.getBehandlingType().name())
                .medBehandlingResultatType(behandling.getBehandlingsresultat().getBehandlingResultatType().name())
                .medKonsekvensForInnvilgetYtelse(konsekvensForInnvilgetYtelse)
                .medSøknadsdato(formaterDatoNorsk(søknad.mottattDato()))
                .medDekningsgrad(ytelseFordeling.dekningsgrad().getVerdi())
                .medHarUtbetaling(harUtbetaling(beregningsresultatFP))
                .medDagsats(dagsats)
                .medMånedsbeløp(finnMånedsbeløp(beregningsresultatFP))
                .medForMyeUtbetalt(forMyeUtbetalt(utbetalingsperioder, behandling))
                .medInntektMottattArbeidsgiver(erEndringMedEndretInntektsmelding(behandling))
                .medAnnenForelderHarRettVurdert(utledAnnenForelderRettVurdertKode(aksjonspunkter, uttakResultatPerioder))
                .medAnnenForelderHarRett(uttakResultatPerioder.isAnnenForelderHarRett())
                .medAleneomsorgKode(erAleneomsorg(søknad, uttakResultatPerioder))
                .medBarnErFødt(familieHendelse.isBarnErFødt())
                .medÅrsakErFødselshendelse(erRevurderingPgaFødselshendelse(behandling, familieHendelse, originalFamiliehendelse))
                .medIkkeOmsorg(finnesPeriodeMedIkkeOmsorg(utbetalingsperioder))
                .medGjelderMor(gjelderMor(fagsak))
                .medGjelderFødsel(familieHendelse.isGjelderFødsel())
                .medIngenRefusjon(harIngenRefusjon(beregningsresultatFP))
                .medDelvisRefusjon(harDelvisRefusjon(beregningsresultatFP))
                .medFullRefusjon(harFullRefusjon(beregningsresultatFP))
                .medFbEllerRvInnvilget(erFbEllerRvInnvilget(behandling))
                .medAntallPerioder(finnAntallPerioder(utbetalingsperioder))
                .medAntallInnvilgedePerioder(finnAntallInnvilgedePerioder(utbetalingsperioder))
                .medAntallAvslåttePerioder(finnAntallAvslåttePerioder(utbetalingsperioder))
                .medAntallArbeidsgivere(finnAntallArbeidsgivere(beregningsresultatFP))
                .medDagerTaptFørTermin(saldoer.tapteDagerFpff())
                .medDisponibleDager(finnDisponibleDager(saldoer, fagsak.getRelasjonsRolleType()))
                .medDisponibleFellesDager(finnDisponibleFellesDager(saldoer))
                .medForeldrepengeperiodenUtvidetUker(finnForeldrepengeperiodenUtvidetUkerHvisFinnes(saldoer))
                .medAntallBarn(familieHendelse.getAntallBarn().intValue())
                .medPrematurDager(finnPrematurDagerHvisFinnes(saldoer))
                .medUtbetalingsperioder(utbetalingsperioder)

                .medKlagefristUker(brevParametere.getKlagefristUker())
                .medLovhjemlerUttak(UttakMapper.mapLovhjemlerForUttak(uttakResultatPerioder, konsekvensForInnvilgetYtelse, erInnvilgetRevurdering))
                .medLovhjemlerBeregning(FellesMapper.formaterLovhjemlerForBeregning(beregningsgrunnlag.getHjemmel().getNavn(),
                        konsekvensForInnvilgetYtelse, erInnvilgetRevurdering, behandling))

                .medInkludereUtbetaling(skalInkludereUtbetaling(behandling, utbetalingsperioder))
                .medInkludereUtbetNårGradering(skalInkludereUtbetNårGradering(behandling, utbetalingsperioder))
                .medInkludereInnvilget(skalInkludereInnvilget(behandling, utbetalingsperioder, konsekvensForInnvilgetYtelse))
                .medInkludereAvslag(skalInkludereAvslag(utbetalingsperioder, konsekvensForInnvilgetYtelse))
                .medInkludereNyeOpplysningerUtbet(skalInkludereNyeOpplysningerUtbet(behandling, utbetalingsperioder, dagsats));

        finnSisteDagAvSistePeriode(uttakResultatPerioder).ifPresent( dato-> dokumentdataBuilder.medSisteDagAvSistePeriode(formaterDatoNorsk(dato)));
        finnStønadsperiodeFom(utbetalingsperioder).ifPresent(dato-> dokumentdataBuilder.medStønadsperiodeFom(formaterDatoNorsk(dato)));
        finnStønadsperiodeTom(utbetalingsperioder).ifPresent(dato-> dokumentdataBuilder.medStønadsperiodeTom(formaterDatoNorsk(dato)));


        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, dokumentdataBuilder);

        mapFelterRelatertTilDødeBarn(familieHendelse, dokumentdataBuilder);

        ForeldrepengerInnvilgelseDokumentdata  fpInnvilgelseDokumentdata = dokumentdataBuilder.build();

        skalFeileOmDødtBarnOgFlerlinger(behandling, fpInnvilgelseDokumentdata);

        return fpInnvilgelseDokumentdata;
    }

    private void skalFeileOmDødtBarnOgFlerlinger(Behandling behandling, ForeldrepengerInnvilgelseDokumentdata fpInnvilgelseDokumentdata) {
    if (fpInnvilgelseDokumentdata.getAntallDødeBarn() > 0 && fpInnvilgelseDokumentdata.getAntallBarn() > 1) {
            throw new FunksjonellException("FPFORMIDLING-0001",
            String.format("Feiler fordi vi ikke håndterer flere barn og død. Meld fra til produkteier for manuell håndtering. Gjelder behandling %s ", behandling.getUuid()));
        }
    }

    private void mapFelterRelatertTilDødeBarn(FamilieHendelse familieHendelse, //todo når fpsak gir oss informasjon om hvor mange barn som er døde må denne oppdateres
            ForeldrepengerInnvilgelseDokumentdata.Builder dokumentdataBuilder) {
        familieHendelse.getDødsdato().ifPresentOrElse(
                (value)
                        -> { dokumentdataBuilder.medDødsdato(formaterDatoNorsk(value));
                             dokumentdataBuilder.medAntallDødeBarn(1); },
                ()
                        -> dokumentdataBuilder.medAntallDødeBarn(0));
    }

    private void mapFelterRelatertTilBeregningsgrunnlag(Beregningsgrunnlag beregningsgrunnlag,
            ForeldrepengerInnvilgelseDokumentdata.Builder builder) {
        List<BeregningsgrunnlagRegel> beregningsgrunnlagregler = mapRegelListe(beregningsgrunnlag);
        builder.medBeregningsgrunnlagregler(beregningsgrunnlagregler);
        builder.medBruttoBeregningsgrunnlag(finnBrutto(beregningsgrunnlag));
        builder.medSekgG(finnSeksG(beregningsgrunnlag).longValue());
        builder.medInntektOverSekgG(inntektOverSeksG(beregningsgrunnlag));
        builder.medErBesteberegning(beregningsgrunnlag.getErBesteberegnet());
        builder.medHarBruktBruttoBeregningsgrunnlag(harBruktBruttoBeregningsgrunnlag(beregningsgrunnlagregler));
    }

    private Optional<LocalDate> finnSisteDagAvSistePeriode(UttakResultatPerioder uttakResultatPerioder) {
        return Stream.concat(
                uttakResultatPerioder.getPerioder().stream(),
                uttakResultatPerioder.getPerioderAnnenPart().stream()).filter(UttakResultatPeriode::isInnvilget)
                .map(UttakResultatPeriode::getTom)
                .max(LocalDate::compareTo);
    }

    private boolean gjelderMor(FagsakBackend fagsak) {
        return RelasjonsRolleType.MORA.equals(fagsak.getRelasjonsRolleType());
    }

    private boolean erRevurderingPgaFødselshendelse(Behandling behandling, FamilieHendelse familieHendelse,
            Optional<FamilieHendelse> originalFamiliehendelse) {
        return behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_HENDELSE_FØDSEL) ||
                familieHendelse.isBarnErFødt() && originalFamiliehendelse.map(fh -> !fh.isBarnErFødt()).orElse(false);
    }

    private VurderingsKode utledAnnenForelderRettVurdertKode(List<Aksjonspunkt> aksjonspunkter, UttakResultatPerioder uttakResultatPerioder) {
        VurderingsKode annenForelderHarRettVurdert;
        if (aksjonspunkter.stream()
                .filter(ap -> Objects.equals(ap.getAksjonspunktDefinisjon(), AksjonspunktDefinisjon.AVKLAR_FAKTA_ANNEN_FORELDER_HAR_IKKE_RETT))
                .anyMatch(ap -> Objects.equals(ap.getAksjonspunktStatus(), AksjonspunktStatus.UTFØRT))) {
            annenForelderHarRettVurdert = uttakResultatPerioder.isAnnenForelderHarRett() ? VurderingsKode.JA : VurderingsKode.NEI;
        } else {
            annenForelderHarRettVurdert = VurderingsKode.IKKE_VURDERT;
        }
        return annenForelderHarRettVurdert;
    }

    private VurderingsKode erAleneomsorg(Søknad søknad, UttakResultatPerioder uttakResultatPerioder) {
        VurderingsKode vurderingsKode;
        if (søknad.oppgittRettighet().harAleneomsorgForBarnet()) {
            vurderingsKode = uttakResultatPerioder.isAleneomsorg() ? VurderingsKode.JA : VurderingsKode.NEI;
        } else {
            vurderingsKode = VurderingsKode.IKKE_VURDERT;
        }
        return vurderingsKode;
    }

    private Søknad hentNyesteSøknad(Behandling behandling) {
        int maxForsøk = 100;
        int nåværendeForsøk = 0;
        Optional<Søknad> søknad = Optional.empty();
        Behandling nåværendeBehandling = behandling;
        while (søknad.isEmpty() && nåværendeForsøk < maxForsøk) {
            søknad = domeneobjektProvider.hentSøknad(nåværendeBehandling);
            if (søknad.isEmpty()) {
                Behandling nesteBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(nåværendeBehandling)
                        .orElseThrow(IllegalStateException::new);
                if (nåværendeBehandling.getUuid() == nesteBehandling.getUuid()) {
                    throw new IllegalStateException();
                }
                nåværendeBehandling = nesteBehandling;
            }
            nåværendeForsøk++;
        }
        return søknad.orElseThrow(IllegalStateException::new);
    }

    private String mapKonsekvensForInnvilgetYtelse(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
        if (konsekvenserForYtelsen.isEmpty()) {
            return KonsekvensForYtelsen.INGEN_ENDRING.name();
        } else if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_UTTAK)
                && konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) {
            return KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK.name();
        } else {
            return konsekvenserForYtelsen.get(0).getKode(); // velger bare den første i listen (finnes ikke koder for andre ev.
                                                            // kombinasjoner)
        }
    }

    private boolean erEndringMedEndretInntektsmelding(Behandling behandling) {
        return erEndring(behandling.getBehandlingType())
                && behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING);
    }

    private boolean erEndring(BehandlingType behandlingType) {
        return BehandlingType.REVURDERING.equals(behandlingType)
                || BehandlingType.KLAGE.equals(behandlingType);
    }

    private boolean erFbEllerRvInnvilget(Behandling behandling) {
        return behandling.getBehandlingsresultat().erInnvilget() && (behandling.erRevurdering() || behandling.erFørstegangssøknad());
    }

    private boolean erInnvilgetRevurdering(Behandling behandling) {
        return behandling.getBehandlingsresultat().erInnvilget() && behandling.erRevurdering();
    }
}
