package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp;

import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.ForMyeUtbetaltMapper.forMyeUtbetalt;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnAntallAvslåttePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnAntallInnvilgedePerioder;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnStønadsperiodeFom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnStønadsperiodeTom;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.finnesPeriodeMedIkkeOmsorg;
import static no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.innvilgelsefp.VedtaksperiodeMapper.sistePeriodeAvslåttPgaBarnOver3år;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat;
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
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.EøsUttak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Prosent;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.AnnenAktivitet;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Arbeidsforhold;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.ForeldrepengerInnvilgelseDokumentdata.Builder;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsefp.Vedtaksperiode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Behandlingsresultat.BehandlingResultatType;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Foreldrepenger.Stønadskonto.Type;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Rettigheter.Rettighetstype;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.FORELDREPENGER_INNVILGELSE)
public class ForeldrepengerInnvilgelseDokumentdataMapper implements DokumentdataMapper {


    private static final Logger LOG = LoggerFactory.getLogger(ForeldrepengerInnvilgelseDokumentdataMapper.class);
    private final BrevParametere brevParametere;
    private final ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    @Inject
    public ForeldrepengerInnvilgelseDokumentdataMapper(BrevParametere brevParametere, ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        this.brevParametere = brevParametere;
        this.arbeidsgiverTjeneste = arbeidsgiverTjeneste;
    }

    @Override
    public String getTemplateNavn() {
        return "foreldrepenger-innvilgelse";
    }

    @Override
    public ForeldrepengerInnvilgelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                    DokumentHendelse dokumentHendelse,
                                                                    BrevGrunnlagDto behandling,
                                                                    boolean erUtkast) {
        var tilkjentYtelseForeldrepenger = behandling.tilkjentYtelse().dagytelse();
        var beregningsgrunnlag = behandling.beregningsgrunnlag();
        var uttak = behandling.foreldrepenger();
        var familieHendelse = behandling.familieHendelse();
        var originalFamiliehendelse = Optional.ofNullable(behandling.originalBehandling()).map(BrevGrunnlagDto.OriginalBehandling::familieHendelse);
        var saldoer = uttak == null ? null : uttak.stønadskontoer();
        if (uttak == null || uttak.perioderSøker().isEmpty() || saldoer.isEmpty()) {
            throw new IllegalStateException("Ikke støttet å generere innvilgelsesbrev uten uttak");
        }
        var språkkode = dokumentFelles.getSpråkkode();
        var utenMinsterett = behandling.behandlingsresultat().skjæringstidspunkt().utenMinsterett();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        FritekstDto.fraFritekst(dokumentHendelse, behandling.behandlingsresultat().fritekst()).ifPresent(fellesBuilder::medFritekst);

        var vedtaksperioder = VedtaksperiodeMapper.mapVedtaksperioder(tilkjentYtelseForeldrepenger.perioder(), uttak,
            beregningsgrunnlag.beregningsgrunnlagperioder(), språkkode, arbeidsgiverTjeneste::hentArbeidsgiverNavn);
        var konsekvensForInnvilgetYtelse = mapKonsekvensForInnvilgetYtelse(behandling.behandlingsresultat().konsekvenserForYtelsen(),
            behandling.behandlingÅrsakTyper());
        var erInnvilgetRevurdering = erInnvilgetRevurdering(behandling);
        var dagsats = TilkjentYtelseMapper.finnDagsats(tilkjentYtelseForeldrepenger);
        var antallBarn = familieHendelse.antallBarn();
        var antallDødeBarn = familieHendelse.barn().stream().filter(b -> b.dødsdato() != null).count();
        var innvilgedeUtbetalingsperioder = finnInnvilgedePerioderMedUtbetaling(vedtaksperioder);

        int utenAktKrav = 0;
        int medAktKrav = 0;
        int maksukerUtenAktKrav = 0;
        var rettigheter = uttak.rettigheter();
        if (bfhrMedMinsterett(saldoer, rettigheter)) {
            utenAktKrav = disponibleDagerUtenAktivitetskrav(saldoer);
            medAktKrav = disponibleDagerMedAktivitetskrav(saldoer);
            maksukerUtenAktKrav = maksdagerUtenAktivitetskrav(saldoer) / 5;
        }
        var behandlingType = behandling.behandlingType();
        var relasjonsRolleType = behandling.relasjonsRolleType();

        if (rettigheter.opprinnelig() != rettigheter.gjeldende()) {
            LOG.info("Rettighetstype endret fra {} til {} - {} - {}", rettigheter.opprinnelig(), rettigheter.gjeldende(), behandlingType,
                relasjonsRolleType);
        }
        if (rettigheter.eøsUttak() != null) {
            LOG.info("Annen parts uttak i eøs {}", rettigheter.eøsUttak());
        }
        if (familieHendelse.barn().stream().anyMatch(b -> b.fødselsdato() == null)) {
            throw new IllegalStateException("Barn mangler fødselsdato");
        }

        var dokumentdataBuilder = ForeldrepengerInnvilgelseDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medBehandlingType(tilString(behandlingType))
            .medBehandlingResultatType(tilString(behandling.behandlingsresultat().behandlingResultatType()))
            .medKonsekvensForInnvilgetYtelse(konsekvensForInnvilgetYtelse)
            .medDekningsgrad(Optional.ofNullable(uttak.dekningsgrad()).map(d -> switch (d) {
                case HUNDRE -> 100;
                case ÅTTI -> 80;
            }).orElseThrow())
            .medEndretDekningsgrad(behandling.behandlingsresultat().endretDekningsgrad())
            .medHarUtbetaling(TilkjentYtelseMapper.harUtbetaling(tilkjentYtelseForeldrepenger))
            .medDagsats(dagsats)
            .medMånedsbeløp(TilkjentYtelseMapper.finnMånedsbeløp(tilkjentYtelseForeldrepenger))
            .medForMyeUtbetalt(forMyeUtbetalt(vedtaksperioder, behandling))
            .medInntektMottattArbeidsgiver(erEndringMedEndretInntektsmelding(behandling))
            .medBarnErFødt(familieHendelse.barnErFødt())
            .medÅrsakErFødselshendelse(erRevurderingPgaFødselshendelse(behandling, familieHendelse, originalFamiliehendelse))
            .medIkkeOmsorg(finnesPeriodeMedIkkeOmsorg(vedtaksperioder))
            .medAvslagBarnOver3år(sistePeriodeAvslåttPgaBarnOver3år(vedtaksperioder))
            .medGjelderMor(gjelderMor(behandling))
            .medGjelderFødsel(familieHendelse.gjelderFødsel())
            .medIngenRefusjon(TilkjentYtelseMapper.harIngenRefusjon(tilkjentYtelseForeldrepenger))
            .medDelvisRefusjon(TilkjentYtelseMapper.harDelvisRefusjon(tilkjentYtelseForeldrepenger))
            .medFullRefusjon(TilkjentYtelseMapper.harFullRefusjon(tilkjentYtelseForeldrepenger))
            .medFbEllerRvInnvilget(erFbEllerRvInnvilget(behandling))
            .medAntallInnvilgedePerioder(finnAntallInnvilgedePerioder(vedtaksperioder))
            .medAntallAvslåttePerioder(finnAntallAvslåttePerioder(vedtaksperioder))
            .medAntallArbeidsgivere(TilkjentYtelseMapper.finnAntallArbeidsgivere(tilkjentYtelseForeldrepenger))
            .medDagerTaptFørTermin(uttak.tapteDagerFpff())
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
            .medMedlemskapOpphørsårsak(behandling.behandlingsresultat().medlemskapOpphørsårsak())
            .medStarterMedFullUtbetaling(starterMedFullUtbetaling(vedtaksperioder))
            .medKlagefristUker(brevParametere.getKlagefristUker())
            .medLovhjemlerUttak(UttakMapper.mapLovhjemlerForUttak(uttak, konsekvensForInnvilgetYtelse, erInnvilgetRevurdering))
            .medLovhjemlerBeregning(
                FellesMapper.formaterLovhjemlerForBeregning(KodeverkMapper.mapBeregningHjemmel(beregningsgrunnlag.hjemmel()).getNavn(),
                    konsekvensForInnvilgetYtelse, erInnvilgetRevurdering, behandling.uuid()))
            .medInkludereInnvilget(UndermalInkluderingMapper.skalInkludereInnvilget(vedtaksperioder, konsekvensForInnvilgetYtelse))
            .medInkludereAvslag(UndermalInkluderingMapper.skalInkludereAvslag(vedtaksperioder, konsekvensForInnvilgetYtelse))
            .medUtenMinsterett(utenMinsterett)
            .medRettigheter(map(rettigheter, språkkode))
            .medØnskerJustertVedFødsel(uttak.ønskerJustertUttakVedFødsel())
            .medGraderingOgFulltUttak(vurderOmGraderingOgFulltUttakISammePeriode(beregningsgrunnlag,
                TilkjentYtelseMapper.finnAntallArbeidsgivere(tilkjentYtelseForeldrepenger), innvilgedeUtbetalingsperioder))
            .medRelasjonsRolleType(KodeverkMapper.mapRelasjonsRolle(relasjonsRolleType))
            .medMaksukerUtenAktivitetskrav(maksukerUtenAktKrav);

        finnSisteDagAvSistePeriode(uttak).ifPresent(dato -> dokumentdataBuilder.medSisteDagAvSistePeriode(formaterDato(dato, språkkode)));
        finnUtbetalingFom(vedtaksperioder).ifPresent(dato -> dokumentdataBuilder.medUtbetalingFom(formaterDato(dato, språkkode)));
        finnStønadsperiodeFom(vedtaksperioder).ifPresent(dato -> dokumentdataBuilder.medStønadsperiodeFom(formaterDato(dato, språkkode)));
        finnStønadsperiodeTom(vedtaksperioder).ifPresent(dato -> dokumentdataBuilder.medStønadsperiodeTom(formaterDato(dato, språkkode)));

        mapFelterRelatertTilBeregningsgrunnlag(beregningsgrunnlag, dokumentdataBuilder);

        //spesialhåndtering av 4095(avslått dager før fødsel) - skal kun vise tekst i brev om det fortsatt er mulig å søke om dagene
        mapFeltKnyttetTilOmMorIkkeTarAlleUkerFørFødsel(vedtaksperioder, dokumentdataBuilder);

        //Dersom alle barna er døde skal vi sette dødsdato og faktisk antallDødeBarn. Det er kun i disse tilfellene at innvilgelsesbrevet skal informere om at barnet/barna er døde, ellers er saken fortsatt løpende
        if (antallDødeBarn > 0 && antallBarn == antallDødeBarn) {
            var tidligstDødsdato = familieHendelse.barn().stream().map(BrevGrunnlagDto.Barn::dødsdato).min(LocalDate::compareTo).orElseThrow();
            dokumentdataBuilder.medDødsdato(formaterDato(tidligstDødsdato, språkkode));
            dokumentdataBuilder.medAntallDødeBarn((int) antallDødeBarn);
        } else {
            dokumentdataBuilder.medAntallDødeBarn(0);
        }
        return dokumentdataBuilder.build();
    }

    private static String tilString(BrevGrunnlagDto.BehandlingType behandlingType) {
        return switch (behandlingType) {
            case FØRSTEGANGSSØKNAD -> "FØRSTEGANGSSØKNAD";
            case REVURDERING -> "REVURDERING";
            case KLAGE -> "KLAGE";
            case ANKE -> "ANKE";
            case INNSYN -> "INNSYN";
            case TILBAKEKREVING -> "TILBAKEKREVING";
            case TILBAKEKREVING_REVURDERING -> "TILBAKEKREVING_REVURDERING";
        };
    }

    private static String tilString(BehandlingResultatType behandlingResultatType) {
        return switch (behandlingResultatType) {
            case IKKE_FASTSATT -> "IKKE_FASTSATT";
            case INNVILGET -> "INNVILGET";
            case AVSLÅTT -> "AVSLÅTT";
            case OPPHØR -> "OPPHØR";
            case HENLAGT_SØKNAD_TRUKKET -> "HENLAGT_SØKNAD_TRUKKET";
            case HENLAGT_FEILOPPRETTET -> "HENLAGT_FEILOPPRETTET";
            case HENLAGT_BRUKER_DØD -> "HENLAGT_BRUKER_DØD";
            case MERGET_OG_HENLAGT -> "MERGET_OG_HENLAGT";
            case HENLAGT_SØKNAD_MANGLER -> "HENLAGT_SØKNAD_MANGLER";
            case FORELDREPENGER_ENDRET -> "FORELDREPENGER_ENDRET";
            case FORELDREPENGER_SENERE -> "FORELDREPENGER_SENERE";
            case INGEN_ENDRING -> "INGEN_ENDRING";
            case MANGLER_BEREGNINGSREGLER -> "MANGLER_BEREGNINGSREGLER";
            case KLAGE_AVVIST -> "KLAGE_AVVIST";
            case KLAGE_MEDHOLD -> "KLAGE_MEDHOLD";
            case KLAGE_DELVIS_MEDHOLD -> "KLAGE_DELVIS_MEDHOLD";
            case KLAGE_OMGJORT_UGUNST -> "KLAGE_OMGJORT_UGUNST";
            case KLAGE_YTELSESVEDTAK_OPPHEVET -> "KLAGE_YTELSESVEDTAK_OPPHEVET";
            case KLAGE_YTELSESVEDTAK_STADFESTET -> "KLAGE_YTELSESVEDTAK_STADFESTET";
            case KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET -> "KLAGE_TILBAKEKREVING_VEDTAK_STADFESTET";
            case HENLAGT_KLAGE_TRUKKET -> "HENLAGT_KLAGE_TRUKKET";
            case HJEMSENDE_UTEN_OPPHEVE -> "HJEMSENDE_UTEN_OPPHEVE";
            case ANKE_AVVIST -> "ANKE_AVVIST";
            case ANKE_MEDHOLD -> "ANKE_MEDHOLD";
            case ANKE_DELVIS_MEDHOLD -> "ANKE_DELVIS_MEDHOLD";
            case ANKE_OMGJORT_UGUNST -> "ANKE_OMGJORT_UGUNST";
            case ANKE_OPPHEVE_OG_HJEMSENDE -> "ANKE_OPPHEVE_OG_HJEMSENDE";
            case ANKE_HJEMSENDE_UTEN_OPPHEV -> "ANKE_HJEMSENDE_UTEN_OPPHEV";
            case ANKE_YTELSESVEDTAK_STADFESTET -> "ANKE_YTELSESVEDTAK_STADFESTET";
            case HENLAGT_ANKE_TRUKKET -> "HENLAGT_ANKE_TRUKKET";
            case INNSYN_INNVILGET -> "INNSYN_INNVILGET";
            case INNSYN_DELVIS_INNVILGET -> "INNSYN_DELVIS_INNVILGET";
            case INNSYN_AVVIST -> "INNSYN_AVVIST";
            case HENLAGT_INNSYN_TRUKKET -> "HENLAGT_INNSYN_TRUKKET";
        };
    }

    private static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter map(BrevGrunnlagDto.Rettigheter rettigheter,
                                                                                             Språkkode språkkode) {
        var eøsUttak = rettigheter.eøsUttak();
        return new no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter(map(rettigheter.opprinnelig()), map(rettigheter.gjeldende()),
            eøsUttak == null ? null : map(eøsUttak, språkkode));
    }

    private static EøsUttak map(BrevGrunnlagDto.Rettigheter.EøsUttak eøsUttak, Språkkode språkkode) {
        return new EøsUttak(formaterDato(eøsUttak.fom(), språkkode), formaterDato(eøsUttak.tom(), språkkode), eøsUttak.forbruktFellesperiode(),
            eøsUttak.fellesperiodeINorge());
    }

    private static no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.Rettigheter.Rettighetstype map(Rettighetstype opprinnelig) {
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
        return vedtaksperioder.stream().filter(p -> p.getPeriodeDagsats() > 0).map(Vedtaksperiode::getPeriodeFom).min(LocalDate::compareTo);
    }

    private static int disponibleDagerUtenAktivitetskrav(List<Stønadskonto> saldoer) {
        if (StønadskontoMapper.kontoEksisterer(saldoer, Type.UTEN_AKTIVITETSKRAV)) {
            return StønadskontoMapper.finnSaldo(saldoer, Type.UTEN_AKTIVITETSKRAV);
        }
        return StønadskontoMapper.finnSaldo(saldoer, Type.MINSTERETT);
    }

    private static int maksdagerUtenAktivitetskrav(List<Stønadskonto> saldoer) {
        if (StønadskontoMapper.kontoEksisterer(saldoer, Type.UTEN_AKTIVITETSKRAV)) {
            return StønadskontoMapper.finnMaksdager(saldoer, Type.UTEN_AKTIVITETSKRAV);
        }
        return StønadskontoMapper.finnMaksdager(saldoer, Type.MINSTERETT);
    }

    private static int disponibleDagerMedAktivitetskrav(List<Stønadskonto> saldoer) {
        return StønadskontoMapper.finnSaldo(saldoer, Type.FORELDREPENGER) - disponibleDagerUtenAktivitetskrav(saldoer);
    }

    private static boolean bfhrMedMinsterett(List<Stønadskonto> saldoer, BrevGrunnlagDto.Rettigheter rettigheter) {
        return Set.of(BrevGrunnlagDto.Rettigheter.Rettighetstype.BARE_FAR_RETT, BrevGrunnlagDto.Rettigheter.Rettighetstype.BARE_FAR_RETT_MOR_UFØR).contains(rettigheter.gjeldende()) && (
            StønadskontoMapper.kontoEksisterer(saldoer, Type.MINSTERETT) || StønadskontoMapper.kontoEksisterer(saldoer, Type.UTEN_AKTIVITETSKRAV));
    }

    private List<Vedtaksperiode> finnInnvilgedePerioderMedUtbetaling(List<Vedtaksperiode> vedtaksperioder) {
        return vedtaksperioder.stream()
            .filter(Vedtaksperiode::isInnvilget)
            .filter(up -> up.getPrioritertUtbetalingsgrad().erStørreEnnNull())
            .toList();
    }

    private boolean vurderOmGraderingOgFulltUttakISammePeriode(BeregningsgrunnlagDto beregningsgrunnlag,
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

    private boolean harFlereAktivitetStatuser(BeregningsgrunnlagDto beregningsgrunnlag) {
        return beregningsgrunnlag.aktivitetstatusListe().size() > 1 || beregningsgrunnlag.aktivitetstatusListe()
            .stream()
            .anyMatch(no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper::erKombinertStatus);
    }

    private void mapFeltKnyttetTilOmMorIkkeTarAlleUkerFørFødsel(List<Vedtaksperiode> vedtaksperioder, Builder builder) {
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

    private void mapFelterRelatertTilBeregningsgrunnlag(BeregningsgrunnlagDto beregningsgrunnlag, Builder builder) {
        var beregningsgrunnlagregler = BeregningsgrunnlagMapper.mapRegelListe(beregningsgrunnlag, arbeidsgiverTjeneste::hentArbeidsgiverNavn);
        builder.medBeregningsgrunnlagregler(beregningsgrunnlagregler);
        builder.medBruttoBeregningsgrunnlag(BeregningsgrunnlagMapper.finnBrutto(beregningsgrunnlag));
        builder.medSekgG(BeregningsgrunnlagMapper.finnSeksG(beregningsgrunnlag).longValue());
        builder.medInntektOverSekgG(BeregningsgrunnlagMapper.inntektOverSeksG(beregningsgrunnlag));
        builder.medErBesteberegning(beregningsgrunnlag.erBesteberegnet());
        builder.medSeksAvDeTiBeste(beregningsgrunnlag.seksAvDeTiBeste());
        builder.medHarBruktBruttoBeregningsgrunnlag(BeregningsgrunnlagMapper.harBruktBruttoBeregningsgrunnlag(beregningsgrunnlagregler));
    }

    private Optional<LocalDate> finnSisteDagAvSistePeriode(BrevGrunnlagDto.Foreldrepenger foreldrepenger) {
        return Stream.concat(foreldrepenger.perioderSøker().stream(), foreldrepenger.perioderAnnenpart().stream())
            .filter(p -> p.periodeResultatType() == BrevGrunnlagDto.PeriodeResultatType.INNVILGET)
            .map(BrevGrunnlagDto.Foreldrepenger.Uttaksperiode::tom)
            .max(LocalDate::compareTo);
    }

    private static boolean gjelderMor(BrevGrunnlagDto brevGrunnlag) {
        return BrevGrunnlagDto.RelasjonsRolleType.MORA.equals(brevGrunnlag.relasjonsRolleType());
    }

    private boolean erRevurderingPgaFødselshendelse(BrevGrunnlagDto behandling,
                                                    BrevGrunnlagDto.FamilieHendelse familieHendelse,
                                                    Optional<BrevGrunnlagDto.FamilieHendelse> originalFamiliehendelse) {
        return behandling.harBehandlingÅrsak(BrevGrunnlagDto.BehandlingÅrsakType.RE_HENDELSE_FØDSEL)
            || familieHendelse.barnErFødt() && originalFamiliehendelse.map(fh -> !fh.barnErFødt()).orElse(false);
    }

    String mapKonsekvensForInnvilgetYtelse(List<Behandlingsresultat.KonsekvensForYtelsen> konsekvenserForYtelsen,
                                           List<BrevGrunnlagDto.BehandlingÅrsakType> behandlingÅrsaker) {
        if (konsekvenserForYtelsen.isEmpty()) {
            return KonsekvensForYtelsen.INGEN_ENDRING.name();
        } else if (konsekvenserForYtelsen.contains(Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING) && behandlingÅrsaker.stream()
            .anyMatch(ba -> ba.equals(BrevGrunnlagDto.BehandlingÅrsakType.FEIL_PRAKSIS_BG_AAP_KOMBI))) {
            return KonsekvensForYtelsen.ENDRING_AAP_PRAKSISENDRING.name();
        } else if (konsekvenserForYtelsen.contains(Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_UTTAK) && konsekvenserForYtelsen.contains(
            Behandlingsresultat.KonsekvensForYtelsen.ENDRING_I_BEREGNING)) {
            return KonsekvensForYtelsen.ENDRING_I_BEREGNING_OG_UTTAK.name();
        } else {
            return KodeverkMapper.mapKonsekvensForYtelsen(konsekvenserForYtelsen.getFirst())
                .getKode(); // velger bare den første i listen (finnes ikke koder for andre ev.kombinasjoner)
        }
    }

    private boolean erEndringMedEndretInntektsmelding(BrevGrunnlagDto behandling) {
        return erEndring(behandling.behandlingType()) && behandling.harBehandlingÅrsak(BrevGrunnlagDto.BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING);
    }

    private boolean erEndring(BrevGrunnlagDto.BehandlingType behandlingType) {
        return BrevGrunnlagDto.BehandlingType.REVURDERING.equals(behandlingType) || BrevGrunnlagDto.BehandlingType.KLAGE.equals(behandlingType);
    }

    private boolean erFbEllerRvInnvilget(BrevGrunnlagDto behandling) {
        return behandling.behandlingsresultat().behandlingResultatType() == BehandlingResultatType.INNVILGET && (behandling.erRevurdering()
            || behandling.erFørstegangssøknad());
    }

    private boolean erInnvilgetRevurdering(BrevGrunnlagDto behandling) {
        return behandling.behandlingsresultat().behandlingResultatType() == BehandlingResultatType.INNVILGET && behandling.erRevurdering();
    }
}
