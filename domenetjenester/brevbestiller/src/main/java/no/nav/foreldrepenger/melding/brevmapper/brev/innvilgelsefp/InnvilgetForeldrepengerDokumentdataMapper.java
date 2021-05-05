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
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.InnvilgelseForeldrepengerDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.KonsekvensForInnvilgetYtelse;
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
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.ForMyeUtbetaltMapper.forMyeUtbetalt;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.StønadskontoMapper.finnDisponibleDager;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.StønadskontoMapper.finnDisponibleFellesDager;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.StønadskontoMapper.finnForeldrepengeperiodenUtvidetUkerHvisFinnes;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.StønadskontoMapper.finnPrematurDagerHvisFinnes;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereAvslag;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereGradering;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereInnvilget;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UndermalInkluderingMapper.skalInkludereUtbetaling;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallPerioder;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeFomHvisFinnes;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeTomHvisFinnes;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.finnesPeriodeMedIkkeOmsorg;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.UtbetalingsperiodeMapper.harInnvilgedePerioder;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.avklarFritekst;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesDokumentdataBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INNVILGET_FORELDREPENGER)
public class InnvilgetForeldrepengerDokumentdataMapper implements DokumentdataMapper {
    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    @Inject
    public InnvilgetForeldrepengerDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "innvilget-foreldrepenger";
    }

    @Override
    public InnvilgelseForeldrepengerDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse dokumentHendelse, Behandling behandling) {
        YtelseFordeling ytelseFordeling = domeneobjektProvider.hentYtelseFordeling(behandling);
        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        Søknad søknad = hentNyesteSøknad(behandling);
        List<Aksjonspunkt> aksjonspunkter = domeneobjektProvider.hentAksjonspunkter(behandling);
        FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        Optional<FamilieHendelse> originalFamiliehendelse = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling).map(domeneobjektProvider::hentFamiliehendelse);
        FagsakBackend fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        Saldoer saldoer = domeneobjektProvider.hentSaldoer(behandling);

        var fellesBuilder = opprettFellesDokumentdataBuilder(dokumentFelles, dokumentHendelse);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        avklarFritekst(dokumentHendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        List<Utbetalingsperiode> utbetalingsperioder = UtbetalingsperiodeMapper.mapUtbetalingsperioder(beregningsresultatFP.getBeregningsresultatPerioder(), uttakResultatPerioder, beregningsgrunnlag.getBeregningsgrunnlagPerioder());
        KonsekvensForInnvilgetYtelse konsekvensForInnvilgetYtelse = mapKonsekvensForInnvilgetYtelse(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());
        String konsekvensForytelsen = String.valueOf(konsekvensForInnvilgetYtelse);
        boolean erInnvilgetRevurdering = erInnvilgetRevurdering(behandling);

        var dokumentdataBuilder = InnvilgelseForeldrepengerDokumentdata.ny()
                .medFellesDokumentData(fellesBuilder.build())

                .medBehandlingType(behandling.getBehandlingType())
                .medBehandlingResultatType(behandling.getBehandlingsresultat().getBehandlingResultatType())
                .medKonsekvensForInnvilgetYtelse(konsekvensForInnvilgetYtelse)
                .medSøknadsdato(formaterDatoNorsk(søknad.getMottattDato()))
                .medDekningsgrad(ytelseFordeling.getDekningsgrad().getVerdi())
                .medDagsats(finnDagsats(beregningsresultatFP))
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
                .medAntallPerioder(finnAntallPerioder(utbetalingsperioder))
                .medFbEllerRvInnvilget(erFbEllerRvInnvilget(behandling))
                .medHarInnvilgedePerioder(harInnvilgedePerioder(utbetalingsperioder))
                .medAntallArbeidsgivere(finnAntallArbeidsgivere(beregningsresultatFP))
                .medDagerTaptFørTermin(saldoer.getTapteDagerFpff())
                .medDisponibleDager(finnDisponibleDager(saldoer, fagsak.getRelasjonsRolleType()))
                .medDisponibleFellesDager(finnDisponibleFellesDager(saldoer))
                .medSisteDagAvSistePeriode(formaterDatoNorsk(finnSisteDagAvSistePeriode(uttakResultatPerioder)))
                .medStønadsperiodeFom(formaterDatoNorsk(finnStønadsperiodeFomHvisFinnes(utbetalingsperioder)))
                .medStønadsperiodeTom(formaterDatoNorsk(finnStønadsperiodeTomHvisFinnes(utbetalingsperioder)))
                .medForeldrepengeperiodenUtvidetUker(finnForeldrepengeperiodenUtvidetUkerHvisFinnes(saldoer))
                .medAntallBarn(familieHendelse.getAntallBarn().intValue())
                .medPrematurDager(finnPrematurDagerHvisFinnes(saldoer))
                .medUtbetalingsperioder(utbetalingsperioder)

                .medKlagefristUker(brevParametere.getKlagefristUker())
                .medLovhjemlerUttak(UttakMapper.mapLovhjemlerForUttak(uttakResultatPerioder, konsekvensForytelsen, erInnvilgetRevurdering))
                .medLovhjemlerBeregning(FellesMapper.formaterLovhjemlerForBeregning(beregningsgrunnlag.getHjemmel().getNavn(), konsekvensForytelsen, erInnvilgetRevurdering))

                .medInkludereUtbetaling(skalInkludereUtbetaling(behandling, utbetalingsperioder))
                .medInkludereGradering(skalInkludereGradering(behandling, utbetalingsperioder))
                .medInkludereInnvilget(skalInkludereInnvilget(behandling, utbetalingsperioder, konsekvensForInnvilgetYtelse))
                .medInkludereAvslag(skalInkludereAvslag(utbetalingsperioder, konsekvensForInnvilgetYtelse))
                ;

                mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, dokumentdataBuilder);

        return dokumentdataBuilder.build();
    }

    private void mapFelterRelatertTilBeregningsgrunnlag(Beregningsgrunnlag beregningsgrunnlag, InnvilgelseForeldrepengerDokumentdata.Builder builder) {
        List<BeregningsgrunnlagRegel> beregningsgrunnlagregler = mapRegelListe(beregningsgrunnlag);
        builder.medBeregningsgrunnlagregler(beregningsgrunnlagregler);
        builder.medBruttoBeregningsgrunnlag(finnBrutto(beregningsgrunnlag));
        builder.medSekgG(finnSeksG(beregningsgrunnlag).longValue());
        builder.medInntektOverSekgG(inntektOverSeksG(beregningsgrunnlag));
        builder.medErBesteberegning(beregningsgrunnlag.getErBesteberegnet());
        builder.medHarBruktBruttoBeregningsgrunnlag(harBruktBruttoBeregningsgrunnlag(beregningsgrunnlagregler));
    }

    private LocalDate finnSisteDagAvSistePeriode(UttakResultatPerioder uttakResultatPerioder) {
        return Stream.concat(
                uttakResultatPerioder.getPerioder().stream(),
                uttakResultatPerioder.getPerioderAnnenPart().stream()
                ).filter(UttakResultatPeriode::isInnvilget)
                .map(UttakResultatPeriode::getTom)
                .max(LocalDate::compareTo)
                .orElse(null);
    }

    private boolean gjelderMor(FagsakBackend fagsak) {
        return RelasjonsRolleType.MORA.equals(fagsak.getRelasjonsRolleType());
    }

    private boolean erRevurderingPgaFødselshendelse(Behandling behandling, FamilieHendelse familieHendelse, Optional<FamilieHendelse> originalFamiliehendelse) {
        return behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_HENDELSE_FØDSEL) ||
                familieHendelse.isBarnErFødt() && originalFamiliehendelse.map(fh -> !fh.isBarnErFødt()).orElse(false);
    }

    private VurderingsKode utledAnnenForelderRettVurdertKode(List<Aksjonspunkt> aksjonspunkter, UttakResultatPerioder uttakResultatPerioder) {
        VurderingsKode annenForelderHarRettVurdert;
        if (aksjonspunkter.stream().
                filter(ap -> Objects.equals(ap.getAksjonspunktDefinisjon(), AksjonspunktDefinisjon.AVKLAR_FAKTA_ANNEN_FORELDER_HAR_IKKE_RETT)).
                anyMatch(ap -> Objects.equals(ap.getAksjonspunktStatus(), AksjonspunktStatus.UTFØRT))) {
            annenForelderHarRettVurdert = uttakResultatPerioder.isAnnenForelderHarRett() ? VurderingsKode.JA : VurderingsKode.NEI;
        }
        else {
            annenForelderHarRettVurdert = VurderingsKode.IKKE_VURDERT;
        }
        return annenForelderHarRettVurdert;
    }

    private VurderingsKode erAleneomsorg(Søknad søknad, UttakResultatPerioder uttakResultatPerioder) {
        VurderingsKode vurderingsKode;
        if (søknad.getOppgittRettighet().harAleneomsorgForBarnet()) {
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
                Behandling nesteBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(nåværendeBehandling).orElseThrow(IllegalStateException::new);
                if (nåværendeBehandling.getUuid() == nesteBehandling.getUuid()) {
                    throw new IllegalStateException();
                }
                nåværendeBehandling = nesteBehandling;
            }
            nåværendeForsøk++;
        }
        return søknad.orElseThrow(IllegalStateException::new);
    }

    private KonsekvensForInnvilgetYtelse mapKonsekvensForInnvilgetYtelse(List<KonsekvensForYtelsen> konsekvenserForYtelsen) {
        if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_UTTAK) && konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) {
            return KonsekvensForInnvilgetYtelse.ENDRING_I_BEREGNING_OG_UTTAK;
        } else if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_BEREGNING)) {
            return KonsekvensForInnvilgetYtelse.ENDRING_I_BEREGNING;
        } else if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_UTTAK)) {
            return KonsekvensForInnvilgetYtelse.ENDRING_I_UTTAK;
        } else {
            return null;
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
