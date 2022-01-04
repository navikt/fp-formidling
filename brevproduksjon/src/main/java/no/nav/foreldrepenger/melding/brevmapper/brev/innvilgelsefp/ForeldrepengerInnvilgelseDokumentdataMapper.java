package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.finnAntallArbeidsgivere;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.finnDagsats;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.harDelvisRefusjon;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.harFullRefusjon;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.harIngenRefusjon;
import static no.nav.foreldrepenger.melding.brevmapper.brev.felles.BeregningsresultatMapper.harUtbetaling;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.finnBrutto;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.finnSeksG;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.harBruktBruttoBeregningsgrunnlag;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.inntektOverSeksG;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsgrunnlagMapper.mapRegelListe;
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
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.fagsak.FagsakBackend;
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.BeregningsgrunnlagRegel;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.innvilgelsefp.VurderingsKode;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.melding.søknad.Søknad;
import no.nav.foreldrepenger.melding.uttak.Saldoer;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.kodeliste.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.ytelsefordeling.YtelseFordeling;

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
        return "foreldrepenger-innvilgelse";
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
        Språkkode språkkode = behandling.getSpråkkode();

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, dokumentHendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(
                dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        Fritekst.fra(dokumentHendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        List<Utbetalingsperiode> utbetalingsperioder = UtbetalingsperiodeMapper.mapUtbetalingsperioder(
                beregningsresultatFP.getBeregningsresultatPerioder(), uttakResultatPerioder,
                beregningsgrunnlag.getBeregningsgrunnlagPerioder(), språkkode);
        String konsekvensForInnvilgetYtelse = mapKonsekvensForInnvilgetYtelse(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());
        boolean erInnvilgetRevurdering = erInnvilgetRevurdering(behandling);
        long dagsats = finnDagsats(beregningsresultatFP);
        int antallBarn = familieHendelse.getAntallBarn().intValue();
        int antallDødeBarn = familieHendelse.getAntallDødeBarn();

        var dokumentdataBuilder = ForeldrepengerInnvilgelseDokumentdata.ny()
                .medFelles(fellesBuilder.build())

                .medBehandlingType(behandling.getBehandlingType().name())
                .medBehandlingResultatType(behandling.getBehandlingsresultat().getBehandlingResultatType().name())
                .medKonsekvensForInnvilgetYtelse(konsekvensForInnvilgetYtelse)
                .medSøknadsdato(formaterDato(søknad.mottattDato(), språkkode))
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
                .medAntallBarn(antallBarn)
                .medPrematurDager(finnPrematurDagerHvisFinnes(saldoer))
                .medKreverSammenhengendeUttak(domeneobjektProvider.kreverSammenhengendeUttak(behandling))
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

        finnSisteDagAvSistePeriode(uttakResultatPerioder).ifPresent(dato -> dokumentdataBuilder.medSisteDagAvSistePeriode(formaterDato(dato, språkkode)));
        finnStønadsperiodeFom(utbetalingsperioder).ifPresent(dato -> dokumentdataBuilder.medStønadsperiodeFom(formaterDato(dato, språkkode)));
        finnStønadsperiodeTom(utbetalingsperioder).ifPresent(dato -> dokumentdataBuilder.medStønadsperiodeTom(formaterDato(dato, språkkode)));

        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, dokumentdataBuilder);

        //spesialhåndtering av 4095(avslått dager før fødsel) - skal kun vise tekst i brev om det fortsatt er mulig å søke om dagene
        mapFeltKnyttetTilOmMorIkkeTarAlleUkerFørFødsel(utbetalingsperioder, dokumentdataBuilder);

        //Dersom alle barna er døde skal vi sette dødsdato og faktisk antallDødeBarn. Det er kun i disse tilfellene at innvilgelsesbrevet skal informere om at barnet/barna er døde, ellers er saken fortsatt løpende
        if (antallBarn == antallDødeBarn) {
            familieHendelse.getDødsdato().ifPresent(d-> dokumentdataBuilder.medDødsdato(formaterDato(d, språkkode)));
            dokumentdataBuilder.medAntallDødeBarn(antallDødeBarn);
        } else {
            dokumentdataBuilder.medAntallDødeBarn(0);
        }
        return dokumentdataBuilder.build();
    }

    private void mapFeltKnyttetTilOmMorIkkeTarAlleUkerFørFødsel(List<Utbetalingsperiode> utbetalingsperioder, ForeldrepengerInnvilgelseDokumentdata.Builder builder) {
        boolean morTarIkkeAlleUkene  = utbetalingsperioder.stream().filter(Utbetalingsperiode::isAvslått).anyMatch(p -> PeriodeResultatÅrsak.MOR_TAR_IKKE_ALLE_UKENE.getKode().equals(p.getÅrsak().getKode()));
        boolean innenforFristTilÅSøke = false;

        if (morTarIkkeAlleUkene) {
              innenforFristTilÅSøke = utbetalingsperioder.stream()
                      .filter(Utbetalingsperiode::isInnvilget)
                      .filter(up -> !StønadskontoType.FORELDREPENGER_FØR_FØDSEL.equals(up.getStønadskontoType()))
                      .map(Utbetalingsperiode::getPeriodeFom)
                      .min(LocalDate::compareTo)
                      .map(md -> md.plusMonths(3).isBefore(LocalDate.now()))
                      .orElse(false);
        }

        builder.medMorKanSøkeOmDagerFørFødsel(innenforFristTilÅSøke);
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
