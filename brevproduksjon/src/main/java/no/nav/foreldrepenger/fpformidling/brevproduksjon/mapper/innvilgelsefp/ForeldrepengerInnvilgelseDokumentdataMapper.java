package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnAntallArbeidsgivere;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnDagsats;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harDelvisRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harFullRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harIngenRefusjon;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper.harUtbetaling;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.finnBrutto;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.finnSeksG;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.harBruktBruttoBeregningsgrunnlag;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.inntektOverSeksG;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.BeregningsgrunnlagMapper.mapRegelListe;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.ForMyeUtbetaltMapper.forMyeUtbetalt;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnDisponibleDager;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnDisponibleFellesDager;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnForeldrepengeperiodenUtvidetUkerHvisFinnes;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnPrematurDagerHvisFinnes;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.finnSaldo;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.StønadskontoMapper.kontoEksisterer;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereAvslag;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UndermalInkluderingMapper.skalInkludereInnvilget;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallAvslåttePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallInnvilgedePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnAntallPerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeFom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnStønadsperiodeTom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.UtbetalingsperiodeMapper.finnesPeriodeMedIkkeOmsorg;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;
import static no.nav.foreldrepenger.fpformidling.uttak.fp.SaldoVisningStønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.fpformidling.uttak.fp.SaldoVisningStønadskontoType.MINSTERETT;
import static no.nav.foreldrepenger.fpformidling.uttak.fp.SaldoVisningStønadskontoType.UTEN_AKTIVITETSKRAV;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Utbetalingsperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.VurderingsKode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.søknad.Søknad;
import no.nav.foreldrepenger.fpformidling.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.uttak.fp.UttakResultatPeriode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.FORELDREPENGER_INNVILGELSE)
public class ForeldrepengerInnvilgelseDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;
    private final DomeneobjektProvider domeneobjektProvider;

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
    public ForeldrepengerInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                    DokumentHendelse dokumentHendelse,
                                                                    Behandling behandling,
                                                                    boolean erUtkast) {
        var tilkjentYtelseForeldrepenger = domeneobjektProvider.hentTilkjentYtelseForeldrepenger(behandling);
        var beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        var uttakResultatPerioder = domeneobjektProvider.hentForeldrepengerUttak(behandling);
        var søknad = hentNyesteSøknad(behandling);
        var familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        var originalFamiliehendelse = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)
            .map(domeneobjektProvider::hentFamiliehendelse);
        var fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        var saldoer = domeneobjektProvider.hentSaldoer(behandling);
        var språkkode = behandling.getSpråkkode();
        var utenMinsterett = behandling.utenMinsterett();
        var ytelseFordeling = domeneobjektProvider.ytelseFordeling(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, dokumentHendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        FritekstDto.fra(dokumentHendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        var utbetalingsperioder = UtbetalingsperiodeMapper.mapUtbetalingsperioder(tilkjentYtelseForeldrepenger.getPerioder(), uttakResultatPerioder,
            beregningsgrunnlag.getBeregningsgrunnlagPerioder(), språkkode);
        var konsekvensForInnvilgetYtelse = mapKonsekvensForInnvilgetYtelse(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());
        var erInnvilgetRevurdering = erInnvilgetRevurdering(behandling);
        var dagsats = finnDagsats(tilkjentYtelseForeldrepenger);
        var antallBarn = familieHendelse.antallBarn();
        var antallDødeBarn = familieHendelse.antallDødeBarn();
        var innvilgedeUtbetalingsperioder = finnInnvilgedePerioderMedUtbetaling(utbetalingsperioder);
        var utenAktKrav = disponibleDagerUtenAktivitetskrav(saldoer);
        var medAktKrav = finnSaldo(saldoer, FORELDREPENGER) - utenAktKrav;

        var dokumentdataBuilder = ForeldrepengerInnvilgelseDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medBehandlingType(behandling.getBehandlingType().name())
            .medBehandlingResultatType(behandling.getBehandlingsresultat().getBehandlingResultatType().name())
            .medKonsekvensForInnvilgetYtelse(konsekvensForInnvilgetYtelse)
            .medSøknadsdato(formaterDato(søknad.mottattDato(), språkkode))
            .medDekningsgrad(Optional.ofNullable(fagsak.getDekningsgrad()).orElseThrow())
            .medHarUtbetaling(harUtbetaling(tilkjentYtelseForeldrepenger))
            .medDagsats(dagsats)
            .medMånedsbeløp(finnMånedsbeløp(tilkjentYtelseForeldrepenger))
            .medForMyeUtbetalt(forMyeUtbetalt(utbetalingsperioder, behandling))
            .medInntektMottattArbeidsgiver(erEndringMedEndretInntektsmelding(behandling))
            .medAnnenForelderHarRettVurdert(utledAnnenForelderRettVurdertKode(behandling, uttakResultatPerioder))
            .medAnnenForelderHarRett(uttakResultatPerioder.annenForelderHarRett())
            .medAnnenForelderRettEØS(uttakResultatPerioder.annenForelderRettEØS())
            .medOppgittAnnenForelderRettEØS(uttakResultatPerioder.oppgittAnnenForelderRettEØS())
            .medAleneomsorgKode(erAleneomsorg(søknad, uttakResultatPerioder))
            .medBarnErFødt(familieHendelse.barnErFødt())
            .medÅrsakErFødselshendelse(erRevurderingPgaFødselshendelse(behandling, familieHendelse, originalFamiliehendelse))
            .medIkkeOmsorg(finnesPeriodeMedIkkeOmsorg(utbetalingsperioder))
            .medGjelderMor(gjelderMor(fagsak))
            .medGjelderFødsel(familieHendelse.gjelderFødsel())
            .medIngenRefusjon(harIngenRefusjon(tilkjentYtelseForeldrepenger))
            .medDelvisRefusjon(harDelvisRefusjon(tilkjentYtelseForeldrepenger))
            .medFullRefusjon(harFullRefusjon(tilkjentYtelseForeldrepenger))
            .medFbEllerRvInnvilget(erFbEllerRvInnvilget(behandling))
            .medAntallPerioder(finnAntallPerioder(utbetalingsperioder))
            .medAntallInnvilgedePerioder(finnAntallInnvilgedePerioder(utbetalingsperioder))
            .medAntallAvslåttePerioder(finnAntallAvslåttePerioder(utbetalingsperioder))
            .medAntallArbeidsgivere(finnAntallArbeidsgivere(tilkjentYtelseForeldrepenger))
            .medDagerTaptFørTermin(saldoer.tapteDagerFpff())
            .medDisponibleDager(finnDisponibleDager(saldoer, fagsak.getRelasjonsRolleType()))
            .medDisponibleDagerUtenAktivitetskrav(utenAktKrav)
            .medDisponibleDagerMedAktivitetskrav(medAktKrav)
            .medDisponibleFellesDager(finnDisponibleFellesDager(saldoer))
            .medForeldrepengeperiodenUtvidetUker(finnForeldrepengeperiodenUtvidetUkerHvisFinnes(saldoer))
            .medAntallBarn(antallBarn)
            .medPrematurDager(finnPrematurDagerHvisFinnes(saldoer))
            .medKreverSammenhengendeUttak(behandling.kreverSammenhengendeUttakFraBehandlingen())
            .medUtbetalingsperioder(utbetalingsperioder)

            .medKlagefristUker(brevParametere.getKlagefristUker())
            .medLovhjemlerUttak(UttakMapper.mapLovhjemlerForUttak(uttakResultatPerioder, konsekvensForInnvilgetYtelse, erInnvilgetRevurdering))
            .medLovhjemlerBeregning(
                FellesMapper.formaterLovhjemlerForBeregning(beregningsgrunnlag.getHjemmel().getNavn(), konsekvensForInnvilgetYtelse,
                    erInnvilgetRevurdering, behandling))
            .medInkludereInnvilget(skalInkludereInnvilget(utbetalingsperioder, konsekvensForInnvilgetYtelse))
            .medInkludereAvslag(skalInkludereAvslag(utbetalingsperioder, konsekvensForInnvilgetYtelse))
            .medUtenMinsterett(utenMinsterett)
            .medØnskerJustertVedFødsel(ytelseFordeling.ønskerJustertVedFødsel())
            .medGraderingOgFulltUttak(
                vurderOmGraderingOgFulltUttakISammePeriode(beregningsgrunnlag, finnAntallArbeidsgivere(tilkjentYtelseForeldrepenger),
                    innvilgedeUtbetalingsperioder));

        finnSisteDagAvSistePeriode(uttakResultatPerioder).ifPresent(
            dato -> dokumentdataBuilder.medSisteDagAvSistePeriode(formaterDato(dato, språkkode)));
        finnUtbetalingFom(utbetalingsperioder).ifPresent(dato -> dokumentdataBuilder.medUtbetalingFom(formaterDato(dato, språkkode)));
        finnStønadsperiodeFom(utbetalingsperioder).ifPresent(dato -> dokumentdataBuilder.medStønadsperiodeFom(formaterDato(dato, språkkode)));
        finnStønadsperiodeTom(utbetalingsperioder).ifPresent(dato -> dokumentdataBuilder.medStønadsperiodeTom(formaterDato(dato, språkkode)));

        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, dokumentdataBuilder);

        //spesialhåndtering av 4095(avslått dager før fødsel) - skal kun vise tekst i brev om det fortsatt er mulig å søke om dagene
        mapFeltKnyttetTilOmMorIkkeTarAlleUkerFørFødsel(utbetalingsperioder, dokumentdataBuilder);

        //Dersom alle barna er døde skal vi sette dødsdato og faktisk antallDødeBarn. Det er kun i disse tilfellene at innvilgelsesbrevet skal informere om at barnet/barna er døde, ellers er saken fortsatt løpende
        if (antallBarn == antallDødeBarn) {
            familieHendelse.dødsdato().ifPresent(d -> dokumentdataBuilder.medDødsdato(formaterDato(d, språkkode)));
            dokumentdataBuilder.medAntallDødeBarn(antallDødeBarn);
        } else {
            dokumentdataBuilder.medAntallDødeBarn(0);
        }
        return dokumentdataBuilder.build();
    }

    private Optional<LocalDate> finnUtbetalingFom(List<Utbetalingsperiode> utbetalingsperioder) {
        //Denne kan være optional hvis saksbehandler overstyrer uttak og avslår alle perioder i en revurdering. Eks sak 152211741
        return utbetalingsperioder.stream()
            .filter(p -> p.getPeriodeDagsats() > 0)
            .map(Utbetalingsperiode::getPeriodeFom)
            .min(LocalDate::compareTo);
    }

    private static int disponibleDagerUtenAktivitetskrav(Saldoer saldoer) {
        if (kontoEksisterer(saldoer, UTEN_AKTIVITETSKRAV)) {
            return finnSaldo(saldoer, UTEN_AKTIVITETSKRAV);
        } else if (kontoEksisterer(saldoer, MINSTERETT)) {
            return finnSaldo(saldoer, MINSTERETT);
        }
        return  0;
    }

    private List<Utbetalingsperiode> finnInnvilgedePerioderMedUtbetaling(List<Utbetalingsperiode> utbetalingsperioder) {
        return utbetalingsperioder.stream()
            .filter(Utbetalingsperiode::isInnvilget)
            .filter(up -> up.getPrioritertUtbetalingsgrad().erStørreEnnNull())
            .toList();
    }

    private boolean vurderOmGraderingOgFulltUttakISammePeriode(Beregningsgrunnlag beregningsgrunnlag,
                                                               int antallArbeidsgivere,
                                                               List<Utbetalingsperiode> innvilgedeUtbetalingsperioder) {
        if (innvilgedeUtbetalingsperioder != null && (harFlereAktivitetStatuser(beregningsgrunnlag) || antallArbeidsgivere > 1)) {
            //Sjekk om gradering og fullt uttak i samme periode
            return innvilgedeUtbetalingsperioder.stream().filter(this::periodeHarGradering).anyMatch(this::harPeriodeOgsåFulltUttak);
        }
        return false;
    }

    private boolean harPeriodeOgsåFulltUttak(Utbetalingsperiode periodeMedGradering) {
        var fulltUttakArbforhold = periodeMedGradering.getArbeidsforholdsliste()
            .stream()
            .filter(Objects::nonNull)
            .anyMatch(af -> Prosent.HUNDRE.equals(af.getUtbetalingsgrad()));
        var fulltUttakAktvitet = periodeMedGradering.getAnnenAktivitetsliste()
            .stream()
            .filter(Objects::nonNull)
            .anyMatch(aa -> Prosent.HUNDRE.equals(aa.getUtbetalingsgrad()));
        var fulltUttakNæring = periodeMedGradering.getNæring() != null && Prosent.HUNDRE.equals(periodeMedGradering.getNæring().getUtbetalingsgrad());

        return fulltUttakArbforhold || fulltUttakAktvitet || fulltUttakNæring;
    }

    private boolean periodeHarGradering(Utbetalingsperiode periode) {
        return periode.getArbeidsforholdsliste().stream().anyMatch(Arbeidsforhold::isGradering) || periode.getAnnenAktivitetsliste()
            .stream()
            .anyMatch(AnnenAktivitet::isGradering) || (periode.getNæring() != null && periode.getNæring().isGradering());
    }

    private boolean harFlereAktivitetStatuser(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getAktivitetStatuser().size() > 1 || beregningsgrunnlag.getAktivitetStatuser()
            .stream()
            .anyMatch(as -> (as.aktivitetStatus().harKombinertStatus()));
    }

    private void mapFeltKnyttetTilOmMorIkkeTarAlleUkerFørFødsel(List<Utbetalingsperiode> utbetalingsperioder,
                                                                ForeldrepengerInnvilgelseDokumentdata.Builder builder) {
        var morTarIkkeAlleUkene = utbetalingsperioder.stream()
            .filter(Utbetalingsperiode::isAvslått)
            .anyMatch(p -> PeriodeResultatÅrsak.MOR_TAR_IKKE_ALLE_UKENE.getKode().equals(p.getÅrsak().getKode()));
        var innenforFristTilÅSøke = false;

        if (morTarIkkeAlleUkene) {
            innenforFristTilÅSøke = utbetalingsperioder.stream()
                .filter(Utbetalingsperiode::isInnvilget)
                .filter(up -> !StønadskontoType.FORELDREPENGER_FØR_FØDSEL.equals(up.getStønadskontoType()))
                .map(Utbetalingsperiode::getPeriodeFom)
                .min(LocalDate::compareTo)
                .map(md -> (LocalDate.now().isBefore(md.plusMonths(3))))
                .orElse(false);
        }
        builder.medMorKanSøkeOmDagerFørFødsel(innenforFristTilÅSøke);
    }

    private void mapFelterRelatertTilBeregningsgrunnlag(Beregningsgrunnlag beregningsgrunnlag,
                                                        ForeldrepengerInnvilgelseDokumentdata.Builder builder) {
        var beregningsgrunnlagregler = mapRegelListe(beregningsgrunnlag);
        builder.medBeregningsgrunnlagregler(beregningsgrunnlagregler);
        builder.medBruttoBeregningsgrunnlag(finnBrutto(beregningsgrunnlag));
        builder.medSekgG(finnSeksG(beregningsgrunnlag).longValue());
        builder.medInntektOverSekgG(inntektOverSeksG(beregningsgrunnlag));
        builder.medErBesteberegning(beregningsgrunnlag.getErBesteberegnet());
        builder.medSeksAvDeTiBeste(beregningsgrunnlag.getSeksAvDeTiBeste());
        builder.medHarBruktBruttoBeregningsgrunnlag(harBruktBruttoBeregningsgrunnlag(beregningsgrunnlagregler));
    }

    private Optional<LocalDate> finnSisteDagAvSistePeriode(ForeldrepengerUttak foreldrepengerUttak) {
        return Stream.concat(foreldrepengerUttak.perioder().stream(), foreldrepengerUttak.perioderAnnenPart().stream())
            .filter(UttakResultatPeriode::isInnvilget)
            .map(UttakResultatPeriode::getTom)
            .max(LocalDate::compareTo);
    }

    private boolean gjelderMor(FagsakBackend fagsak) {
        return RelasjonsRolleType.MORA.equals(fagsak.getRelasjonsRolleType());
    }

    private boolean erRevurderingPgaFødselshendelse(Behandling behandling,
                                                    FamilieHendelse familieHendelse,
                                                    Optional<FamilieHendelse> originalFamiliehendelse) {
        return behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_HENDELSE_FØDSEL) || familieHendelse.barnErFødt() && originalFamiliehendelse.map(
            fh -> !fh.barnErFødt()).orElse(false);
    }

    private VurderingsKode utledAnnenForelderRettVurdertKode(Behandling behandling, ForeldrepengerUttak foreldrepengerUttak) {
        VurderingsKode annenForelderHarRettVurdert;
        if (behandling.getHarAvklartAnnenForelderRett()) {
            annenForelderHarRettVurdert = foreldrepengerUttak.annenForelderHarRett() ? VurderingsKode.JA : VurderingsKode.NEI;
        } else {
            annenForelderHarRettVurdert = VurderingsKode.IKKE_VURDERT;
        }
        return annenForelderHarRettVurdert;
    }

    private VurderingsKode erAleneomsorg(Søknad søknad, ForeldrepengerUttak foreldrepengerUttak) {
        VurderingsKode vurderingsKode;
        if (søknad.oppgittAleneomsorg()) {
            vurderingsKode = foreldrepengerUttak.aleneomsorg() ? VurderingsKode.JA : VurderingsKode.NEI;
        } else {
            vurderingsKode = VurderingsKode.IKKE_VURDERT;
        }
        return vurderingsKode;
    }

    private Søknad hentNyesteSøknad(Behandling behandling) {
        var maxForsøk = 100;
        var nåværendeForsøk = 0;
        Optional<Søknad> søknad = Optional.empty();
        var nåværendeBehandling = behandling;
        while (søknad.isEmpty() && nåværendeForsøk < maxForsøk) {
            søknad = domeneobjektProvider.hentSøknad(nåværendeBehandling);
            if (søknad.isEmpty()) {
                var nesteBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(nåværendeBehandling)
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
        } else if (konsekvenserForYtelsen.contains(KonsekvensForYtelsen.ENDRING_I_UTTAK) && konsekvenserForYtelsen.contains(
            KonsekvensForYtelsen.ENDRING_I_BEREGNING)) {
            return KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK.name();
        } else {
            return konsekvenserForYtelsen.get(0).getKode(); // velger bare den første i listen (finnes ikke koder for andre ev.
            // kombinasjoner)
        }
    }

    private boolean erEndringMedEndretInntektsmelding(Behandling behandling) {
        return erEndring(behandling.getBehandlingType()) && behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING);
    }

    private boolean erEndring(BehandlingType behandlingType) {
        return BehandlingType.REVURDERING.equals(behandlingType) || BehandlingType.KLAGE.equals(behandlingType);
    }

    private boolean erFbEllerRvInnvilget(Behandling behandling) {
        return behandling.getBehandlingsresultat().erInnvilget() && (behandling.erRevurdering() || behandling.erFørstegangssøknad());
    }

    private boolean erInnvilgetRevurdering(Behandling behandling) {
        return behandling.getBehandlingsresultat().erInnvilget() && behandling.erRevurdering();
    }
}
