package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.ForMyeUtbetaltMapper.forMyeUtbetalt;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnAntallAvslåttePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnAntallInnvilgedePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnStønadsperiodeFom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnStønadsperiodeTom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnesPeriodeMedIkkeOmsorg;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.sistePeriodeAvslåttPgaBarnOver3år;
import static no.nav.foreldrepenger.fpformidling.domene.uttak.fp.SaldoVisningStønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.fpformidling.domene.uttak.fp.SaldoVisningStønadskontoType.MINSTERETT;
import static no.nav.foreldrepenger.fpformidling.domene.uttak.fp.SaldoVisningStønadskontoType.UTEN_AKTIVITETSKRAV;
import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.TilkjentYtelseMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.DomeneobjektProvider;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakBackend;
import no.nav.foreldrepenger.fpformidling.domene.familiehendelse.FamilieHendelse;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.Rettigheter;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.ForeldrepengerUttak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.Saldoer;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.UttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_INNVILGELSE)
public class ForeldrepengerInnvilgelseDokumentdataMapper implements DokumentdataMapper {


    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerInnvilgelseDokumentdataMapper.class);
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
        var uttak = domeneobjektProvider.hentForeldrepengerUttak(behandling);
        var familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);
        var originalFamiliehendelse = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling)
            .map(domeneobjektProvider::hentFamiliehendelse);
        var fagsak = domeneobjektProvider.hentFagsakBackend(behandling);
        var saldoer = domeneobjektProvider.hentSaldoer(behandling);
        if (uttak.perioder().isEmpty() || saldoer.stønadskontoer().isEmpty()) {
            throw new IllegalStateException("Ikke støttet å generere innvilgelsesbrev uten uttak");
        }
        var språkkode = behandling.getSpråkkode();
        var utenMinsterett = behandling.utenMinsterett();
        var ytelseFordeling = domeneobjektProvider.ytelseFordeling(behandling);

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        FritekstDto.fra(dokumentHendelse, behandling).ifPresent(fellesBuilder::medFritekst);

        var vedtaksperioder = VedtaksperiodeMapper.mapVedtaksperioder(tilkjentYtelseForeldrepenger.getPerioder(), uttak,
            beregningsgrunnlag.getBeregningsgrunnlagPerioder(), språkkode);
        var konsekvensForInnvilgetYtelse = mapKonsekvensForInnvilgetYtelse(behandling.getBehandlingsresultat().getKonsekvenserForYtelsen());
        var erInnvilgetRevurdering = erInnvilgetRevurdering(behandling);
        var dagsats = TilkjentYtelseMapper.finnDagsats(tilkjentYtelseForeldrepenger);
        var antallBarn = familieHendelse.antallBarn();
        var antallDødeBarn = familieHendelse.antallDødeBarn();
        var innvilgedeUtbetalingsperioder = finnInnvilgedePerioderMedUtbetaling(vedtaksperioder);

        int utenAktKrav = 0;
        int medAktKrav = 0;
        int maksukerUtenAktKrav = 0;
        var rettigheter = behandling.getRettigheter();
        if (bfhrMedMinsterett(saldoer, rettigheter)) {
            utenAktKrav = disponibleDagerUtenAktivitetskrav(saldoer);
            medAktKrav = disponibleDagerMedAktivitetskrav(saldoer);
            maksukerUtenAktKrav = maksdagerUtenAktivitetskrav(saldoer) / 5;
        }
        var behandlingType = behandling.getBehandlingType();
        var relasjonsRolleType = fagsak.getRelasjonsRolleType();

        if (rettigheter.opprinnelig() != rettigheter.gjeldende()) {
            LOG.info("Rettighetstype endret fra {} til {} - {} - {}", rettigheter.opprinnelig(), rettigheter.gjeldende(), behandlingType,
                relasjonsRolleType);
        }
        if (rettigheter.eøsUttak() != null) {
            LOG.info("Annen parts uttak i eøs {}", rettigheter.eøsUttak());
        }

        var dokumentdataBuilder = ForeldrepengerInnvilgelseDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medBehandlingType(behandlingType.name())
            .medBehandlingResultatType(behandling.getBehandlingsresultat().getBehandlingResultatType().name())
            .medKonsekvensForInnvilgetYtelse(konsekvensForInnvilgetYtelse)
            .medDekningsgrad(Optional.ofNullable(fagsak.getDekningsgrad()).orElseThrow())
            .medEndretDekningsgrad(behandling.getBehandlingsresultat().isEndretDekningsgrad())
            .medHarUtbetaling(TilkjentYtelseMapper.harUtbetaling(tilkjentYtelseForeldrepenger))
            .medDagsats(dagsats)
            .medMånedsbeløp(TilkjentYtelseMapper.finnMånedsbeløp(tilkjentYtelseForeldrepenger))
            .medForMyeUtbetalt(forMyeUtbetalt(vedtaksperioder, behandling))
            .medInntektMottattArbeidsgiver(erEndringMedEndretInntektsmelding(behandling))
            .medBarnErFødt(familieHendelse.barnErFødt())
            .medÅrsakErFødselshendelse(erRevurderingPgaFødselshendelse(behandling, familieHendelse, originalFamiliehendelse))
            .medIkkeOmsorg(finnesPeriodeMedIkkeOmsorg(vedtaksperioder))
            .medAvslagBarnOver3år(sistePeriodeAvslåttPgaBarnOver3år(vedtaksperioder))
            .medGjelderMor(gjelderMor(fagsak))
            .medGjelderFødsel(familieHendelse.gjelderFødsel())
            .medIngenRefusjon(TilkjentYtelseMapper.harIngenRefusjon(tilkjentYtelseForeldrepenger))
            .medDelvisRefusjon(TilkjentYtelseMapper.harDelvisRefusjon(tilkjentYtelseForeldrepenger))
            .medFullRefusjon(TilkjentYtelseMapper.harFullRefusjon(tilkjentYtelseForeldrepenger))
            .medFbEllerRvInnvilget(erFbEllerRvInnvilget(behandling))
            .medAntallInnvilgedePerioder(finnAntallInnvilgedePerioder(vedtaksperioder))
            .medAntallAvslåttePerioder(finnAntallAvslåttePerioder(vedtaksperioder))
            .medAntallArbeidsgivere(TilkjentYtelseMapper.finnAntallArbeidsgivere(tilkjentYtelseForeldrepenger))
            .medDagerTaptFørTermin(saldoer.tapteDagerFpff())
            .medDisponibleDager(StønadskontoMapper.finnDisponibleDager(saldoer, relasjonsRolleType))
            .medDisponibleDagerUtenAktivitetskrav(utenAktKrav)
            .medDisponibleDagerMedAktivitetskrav(medAktKrav)
            .medDisponibleFellesDager(StønadskontoMapper.finnDisponibleFellesDager(saldoer))
            .medFlerbarnsdagerUtvidetUker(StønadskontoMapper.finnFlerbarnsdagerUtvidetUkerHvisFinnes(saldoer))
            .medFlerbarnsdagerUtvidetDager(StønadskontoMapper.finnFlerbarnsdagerUtvidetDagerHvisFinnes(saldoer))
            .medAntallBarn(antallBarn)
            .medPrematurDager(StønadskontoMapper.finnPrematurDagerHvisFinnes(saldoer))
            .medPerioder(vedtaksperioder)
            .medHarVarierendeDagsats(harVarierendeDagsats(vedtaksperioder))
            .medMedlemskapOpphørsårsak(behandling.getMedlemskapOpphørsårsak() == null ? null : behandling.getMedlemskapOpphørsårsak().getKode())
            .medStarterMedFullUtbetaling(starterMedFullUtbetaling(vedtaksperioder))
            .medKlagefristUker(brevParametere.getKlagefristUker())
            .medLovhjemlerUttak(UttakMapper.mapLovhjemlerForUttak(uttak, konsekvensForInnvilgetYtelse, erInnvilgetRevurdering))
            .medLovhjemlerBeregning(
                FellesMapper.formaterLovhjemlerForBeregning(beregningsgrunnlag.getHjemmel().getNavn(), konsekvensForInnvilgetYtelse,
                    erInnvilgetRevurdering, behandling))
            .medInkludereInnvilget(UndermalInkluderingMapper.skalInkludereInnvilget(vedtaksperioder, konsekvensForInnvilgetYtelse))
            .medInkludereAvslag(UndermalInkluderingMapper.skalInkludereAvslag(vedtaksperioder, konsekvensForInnvilgetYtelse))
            .medUtenMinsterett(utenMinsterett)
            .medRettigheter(map(rettigheter, språkkode))
            .medØnskerJustertVedFødsel(ytelseFordeling.ønskerJustertVedFødsel())
            .medGraderingOgFulltUttak(
                vurderOmGraderingOgFulltUttakISammePeriode(beregningsgrunnlag, TilkjentYtelseMapper.finnAntallArbeidsgivere(tilkjentYtelseForeldrepenger),
                    innvilgedeUtbetalingsperioder))
            .medRelasjonsRolleType(relasjonsRolleType)
            .medMaksukerUtenAktivitetskrav(maksukerUtenAktKrav);

        finnSisteDagAvSistePeriode(uttak).ifPresent(
            dato -> dokumentdataBuilder.medSisteDagAvSistePeriode(formaterDato(dato, språkkode)));
        finnUtbetalingFom(vedtaksperioder).ifPresent(dato -> dokumentdataBuilder.medUtbetalingFom(formaterDato(dato, språkkode)));
        finnStønadsperiodeFom(vedtaksperioder).ifPresent(dato -> dokumentdataBuilder.medStønadsperiodeFom(formaterDato(dato, språkkode)));
        finnStønadsperiodeTom(vedtaksperioder).ifPresent(dato -> dokumentdataBuilder.medStønadsperiodeTom(formaterDato(dato, språkkode)));

        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, dokumentdataBuilder);

        //spesialhåndtering av 4095(avslått dager før fødsel) - skal kun vise tekst i brev om det fortsatt er mulig å søke om dagene
        mapFeltKnyttetTilOmMorIkkeTarAlleUkerFørFødsel(vedtaksperioder, dokumentdataBuilder);

        //Dersom alle barna er døde skal vi sette dødsdato og faktisk antallDødeBarn. Det er kun i disse tilfellene at innvilgelsesbrevet skal informere om at barnet/barna er døde, ellers er saken fortsatt løpende
        if (antallBarn == antallDødeBarn) {
            familieHendelse.dødsdato().ifPresent(d -> dokumentdataBuilder.medDødsdato(formaterDato(d, språkkode)));
            dokumentdataBuilder.medAntallDødeBarn(antallDødeBarn);
        } else {
            dokumentdataBuilder.medAntallDødeBarn(0);
        }
        return dokumentdataBuilder.build();
    }

    private static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter map(Rettigheter rettigheter, Språkkode språkkode) {
        var eøsUttak = rettigheter.eøsUttak();
        return new no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter(map(rettigheter.opprinnelig()), map(rettigheter.gjeldende()),
            eøsUttak == null ? null : map(eøsUttak, språkkode));
    }

    private static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.EøsUttak map(Rettigheter.EøsUttak eøsUttak,
                                                                                                      Språkkode språkkode) {
        return new no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.EøsUttak(formaterDato(eøsUttak.fom(), språkkode),
            formaterDato(eøsUttak.tom(), språkkode), eøsUttak.forbruktFellesperiode(), eøsUttak.fellesperiodeINorge());
    }

    private static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.Rettighetstype map(Rettigheter.Rettighetstype opprinnelig) {
        return switch (opprinnelig) {
            case ALENEOMSORG -> no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.Rettighetstype.ALENEOMSORG;
            case BEGGE_RETT -> no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.Rettighetstype.BEGGE_RETT;
            case BEGGE_RETT_EØS -> no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.Rettighetstype.BEGGE_RETT_EØS;
            case BARE_MOR_RETT, BARE_FAR_RETT -> no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.Rettighetstype.BARE_SØKER_RETT;
            case BARE_FAR_RETT_MOR_UFØR ->
                no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.Rettighetstype.BARE_FAR_RETT_MOR_UFØR;
        };
    }

    static boolean starterMedFullUtbetaling(List<Vedtaksperiode> vedtaksperioder) {
        if (vedtaksperioder.isEmpty()) {
            return false;
        }
        return vedtaksperioder.get(0).isFullUtbetaling();
    }

    static boolean harVarierendeDagsats(List<Vedtaksperiode> vedtaksperioder) {
        if (vedtaksperioder.isEmpty()) {
            return false;
        }
        return vedtaksperioder.stream().map(Vedtaksperiode::getPeriodeDagsats).filter(dagsats -> dagsats > 0).distinct().count() > 1;
    }

    private Optional<LocalDate> finnUtbetalingFom(List<Vedtaksperiode> vedtaksperioder) {
        //Denne kan være optional hvis saksbehandler overstyrer uttak og avslår alle perioder i en revurdering. Eks sak 152211741
        return vedtaksperioder.stream()
            .filter(p -> p.getPeriodeDagsats() > 0)
            .map(Vedtaksperiode::getPeriodeFom)
            .min(LocalDate::compareTo);
    }

    private static int disponibleDagerUtenAktivitetskrav(Saldoer saldoer) {
        if (StønadskontoMapper.kontoEksisterer(saldoer, UTEN_AKTIVITETSKRAV)) {
            return StønadskontoMapper.finnSaldo(saldoer, UTEN_AKTIVITETSKRAV);
        }
        return StønadskontoMapper.finnSaldo(saldoer, MINSTERETT);
    }

    private static int maksdagerUtenAktivitetskrav(Saldoer saldoer) {
        if (StønadskontoMapper.kontoEksisterer(saldoer, UTEN_AKTIVITETSKRAV)) {
            return StønadskontoMapper.finnMaksdager(saldoer, UTEN_AKTIVITETSKRAV);
        }
        return StønadskontoMapper.finnMaksdager(saldoer, MINSTERETT);
    }

    private static int disponibleDagerMedAktivitetskrav(Saldoer saldoer) {
        return StønadskontoMapper.finnSaldo(saldoer, FORELDREPENGER) - disponibleDagerUtenAktivitetskrav(saldoer);
    }

    private static boolean bfhrMedMinsterett(Saldoer saldoer, Rettigheter rettigheter) {
        return Set.of(Rettigheter.Rettighetstype.BARE_FAR_RETT, Rettigheter.Rettighetstype.BARE_FAR_RETT_MOR_UFØR).contains(rettigheter.gjeldende())
            && (StønadskontoMapper.kontoEksisterer(saldoer, MINSTERETT) || StønadskontoMapper.kontoEksisterer(saldoer, UTEN_AKTIVITETSKRAV));
    }

    private List<Vedtaksperiode> finnInnvilgedePerioderMedUtbetaling(List<Vedtaksperiode> vedtaksperioder) {
        return vedtaksperioder.stream()
            .filter(Vedtaksperiode::isInnvilget)
            .filter(up -> up.getPrioritertUtbetalingsgrad().erStørreEnnNull())
            .toList();
    }

    private boolean vurderOmGraderingOgFulltUttakISammePeriode(Beregningsgrunnlag beregningsgrunnlag,
                                                               int antallArbeidsgivere,
                                                               List<Vedtaksperiode> innvilgedeUtbetalingsperioder) {
        if (innvilgedeUtbetalingsperioder != null && (harFlereAktivitetStatuser(beregningsgrunnlag) || antallArbeidsgivere > 1)) {
            //Sjekk om gradering og fullt uttak i samme periode
            return innvilgedeUtbetalingsperioder.stream().filter(this::periodeHarGradering).anyMatch(this::harPeriodeOgsåFulltUttak);
        }
        return false;
    }

    private boolean harPeriodeOgsåFulltUttak(Vedtaksperiode periodeMedGradering) {
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

    private boolean periodeHarGradering(Vedtaksperiode periode) {
        return periode.getArbeidsforholdsliste().stream().anyMatch(Arbeidsforhold::isGradering) || periode.getAnnenAktivitetsliste()
            .stream()
            .anyMatch(AnnenAktivitet::isGradering) || (periode.getNæring() != null && periode.getNæring().isGradering());
    }

    private boolean harFlereAktivitetStatuser(Beregningsgrunnlag beregningsgrunnlag) {
        return beregningsgrunnlag.getAktivitetStatuser().size() > 1 || beregningsgrunnlag.getAktivitetStatuser()
            .stream()
            .anyMatch(as -> (as.aktivitetStatus().harKombinertStatus()));
    }

    private void mapFeltKnyttetTilOmMorIkkeTarAlleUkerFørFødsel(List<Vedtaksperiode> vedtaksperioder,
                                                                ForeldrepengerInnvilgelseDokumentdata.Builder builder) {
        var morTarIkkeAlleUkene = vedtaksperioder.stream()
            .filter(Vedtaksperiode::isAvslått)
            .anyMatch(p -> PeriodeResultatÅrsak.MOR_TAR_IKKE_ALLE_UKENE.getKode().equals(p.getÅrsak().getKode()));
        var innenforFristTilÅSøke = false;

        if (morTarIkkeAlleUkene) {
            innenforFristTilÅSøke = vedtaksperioder.stream()
                .filter(Vedtaksperiode::isInnvilget)
                .filter(up -> !StønadskontoType.FORELDREPENGER_FØR_FØDSEL.equals(up.getStønadskontoType()))
                .map(Vedtaksperiode::getPeriodeFom)
                .min(LocalDate::compareTo)
                .map(md -> (LocalDate.now().isBefore(md.plusMonths(3))))
                .orElse(false);
        }
        builder.medMorKanSøkeOmDagerFørFødsel(innenforFristTilÅSøke);
    }

    private void mapFelterRelatertTilBeregningsgrunnlag(Beregningsgrunnlag beregningsgrunnlag,
                                                        ForeldrepengerInnvilgelseDokumentdata.Builder builder) {
        var beregningsgrunnlagregler = BeregningsgrunnlagMapper.mapRegelListe(beregningsgrunnlag);
        builder.medBeregningsgrunnlagregler(beregningsgrunnlagregler);
        builder.medBruttoBeregningsgrunnlag(BeregningsgrunnlagMapper.finnBrutto(beregningsgrunnlag));
        builder.medSekgG(BeregningsgrunnlagMapper.finnSeksG(beregningsgrunnlag).longValue());
        builder.medInntektOverSekgG(BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag));
        builder.medErBesteberegning(beregningsgrunnlag.getErBesteberegnet());
        builder.medSeksAvDeTiBeste(beregningsgrunnlag.getSeksAvDeTiBeste());
        builder.medHarBruktBruttoBeregningsgrunnlag(BeregningsgrunnlagMapper.harBruktBruttoBeregningsgrunnlag(beregningsgrunnlagregler));
    }

    private Optional<LocalDate> finnSisteDagAvSistePeriode(ForeldrepengerUttak foreldrepengerUttak) {
        return Stream.concat(foreldrepengerUttak.perioder().stream(), foreldrepengerUttak.perioderAnnenPart().stream())
            .filter(UttakResultatPeriode::isInnvilget)
            .map(UttakResultatPeriode::getTom)
            .max(LocalDate::compareTo);
    }

    private static boolean gjelderMor(FagsakBackend fagsak) {
        return RelasjonsRolleType.MORA.equals(fagsak.getRelasjonsRolleType());
    }

    private boolean erRevurderingPgaFødselshendelse(Behandling behandling,
                                                    FamilieHendelse familieHendelse,
                                                    Optional<FamilieHendelse> originalFamiliehendelse) {
        return behandling.harBehandlingÅrsak(BehandlingÅrsakType.RE_HENDELSE_FØDSEL) || familieHendelse.barnErFødt() && originalFamiliehendelse.map(
            fh -> !fh.barnErFødt()).orElse(false);
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
