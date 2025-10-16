package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorsvp;

import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Inntektsmelding;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.PeriodeResultatType;
import static no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto.Svangerskapspenger;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelseAndelDto;
import static no.nav.foreldrepenger.kontrakter.fpsak.tilkjentytelse.TilkjentYtelseDagytelseDto.TilkjentYtelsePeriodeDto;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.UnaryOperator;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.SvpMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.vedtak.exception.TekniskException;

public final class OpphørPeriodeMapper {

    private OpphørPeriodeMapper() {
    }

    public static Tuple<OpphørPeriode, String> mapOpphørtePerioderOgLovhjemmel(BrevGrunnlagDto behandling,
                                                                               List<Svangerskapspenger.UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                                                               Språkkode språkKode,
                                                                               List<Inntektsmelding> inntektsmeldinger,
                                                                               List<TilkjentYtelsePeriodeDto> tilkjentYtelsePerioder,
                                                                               UnaryOperator<String> hentNavn) {
        var lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        var behandlingsresultat = behandling.behandlingsresultat();
        var avslagsårsak = behandlingsresultat.avslagsårsak();

        //Sjekker om periodeIkkeOppfyltÅrsak finnes fra uttak - oppretter i såfall opphørt periode.
        var opphørtPeriode = mapOpphørtPeriodeMedÅrsakFraAvslåttUttak(uttakResultatArbeidsforhold, språkKode, tilkjentYtelsePerioder,
            inntektsmeldinger, lovReferanser, hentNavn);

        //I en del tilfeller må vi hente opphørsårsak fra behandlingsresultatet (ingen uttak eller opphørt inngangsvilkår i en revurdering)
        if (opphørtPeriode == null) {

            if (avslagsårsak != null) {
                opphørtPeriode = mapOpphørtPeriode(tilkjentYtelsePerioder, uttakResultatArbeidsforhold, språkKode, avslagsårsak, inntektsmeldinger,
                    hentNavn);
                var vilkårTyper = behandling.behandlingsresultat().vilkårTyper().stream().map(KodeverkMapper::mapVilkårType).toList();
                lovReferanser.add(SvpMapperUtil.leggTilLovreferanse(vilkårTyper, Avslagsårsak.fraKode(avslagsårsak)));
            } else {
                //kan skje i uttak, men ingen tilfeller i prod
                opphørtPeriode = mapOpphørteArbeidsforhold(uttakResultatArbeidsforhold, inntektsmeldinger, hentNavn);
                lovReferanser.add("§ 14-4");
            }

            if (opphørtPeriode == null) {
                throw new TekniskException("FPFORMIDLING-100003",
                    String.format("Det er ikke mulig å bestille opphørsbrev uten opphørt periode for behandling UUID %s", behandling.uuid()));
            }
        }

        var lovhjemmelForOpphør = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
            BehandlingMapper.kodeFra(behandlingsresultat.konsekvenserForYtelsen()), true);

        return new Tuple<>(opphørtPeriode, lovhjemmelForOpphør);
    }

    private static OpphørPeriode mapOpphørteArbeidsforhold(List<Svangerskapspenger.UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                                           List<Inntektsmelding> inntektsmeldinger,
                                                           UnaryOperator<String> hentNavn) {
        return uttakResultatArbeidsforhold.stream()
            .filter(a -> !ArbeidsforholdIkkeOppfyltÅrsak.INGEN.getKode().equals(a.arbeidsforholdIkkeOppfyltÅrsak()))
            .findFirst()
            .map(a -> opprettUtenPeriode(a.arbeidsforholdIkkeOppfyltÅrsak(),
                SvpMapperUtil.finnAntallArbeidsgivere(uttakResultatArbeidsforhold, inntektsmeldinger, hentNavn)))
            .orElse(null);
    }

    private static OpphørPeriode opprettUtenPeriode(String avslagsÅrsak, int antallArbeidsgivere) {
        return OpphørPeriode.ny().medÅrsak(Årsak.of(avslagsÅrsak)).medAntallArbeidsgivere(antallArbeidsgivere).build();
    }

    private static OpphørPeriode mapOpphørtPeriodeMedÅrsakFraAvslåttUttak(List<Svangerskapspenger.UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                                                          Språkkode språkKode,
                                                                          List<TilkjentYtelsePeriodeDto> tilkjentYtelsePerioder,
                                                                          List<Inntektsmelding> inntektsmeldinger,
                                                                          TreeSet<String> lovReferanser,
                                                                          UnaryOperator<String> hentNavn) {
        var opphørtePerioder = uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.perioder().stream())
            .filter(ur -> PeriodeResultatType.AVSLÅTT.equals(ur.periodeResultatType()))
            .filter(ur -> PeriodeIkkeOppfyltÅrsak.opphørsAvslagÅrsaker().stream().anyMatch(å -> å.getKode().equals(ur.periodeIkkeOppfyltÅrsak())))
            .toList();

        if (!opphørtePerioder.isEmpty()) {
            var årsak = finnNyestePeriodeIkkeOppfyltÅrsak(opphørtePerioder);
            if (PeriodeIkkeOppfyltÅrsak.opphørSvpInngangsvilkårMedUttak().contains(årsak)) {
                //spesialhåndtering dersom opphør av inngangsvilkår i en revurdering - henter faktisk årsak fra behandlingsresultat
                return null;
            }
            if (årsak != null) {
                årsak.getLovHjemmelData().ifPresent(lovReferanser::add);
                return mapOpphørtPeriode(tilkjentYtelsePerioder, uttakResultatArbeidsforhold, språkKode, årsak.getKode(), inntektsmeldinger,
                    hentNavn);
            }
        }
        return null;
    }

    private static PeriodeIkkeOppfyltÅrsak finnNyestePeriodeIkkeOppfyltÅrsak(List<Svangerskapspenger.Uttaksperiode> opphørtePerioder) {
        return opphørtePerioder.stream()
            .max(Comparator.comparing(Svangerskapspenger.Uttaksperiode::tom))
            .map(periode -> PeriodeIkkeOppfyltÅrsak.fra(periode.periodeIkkeOppfyltÅrsak()))
            .orElse(null);
    }

    private static OpphørPeriode mapOpphørtPeriode(List<TilkjentYtelsePeriodeDto> tilkjentYtelse,
                                                   List<Svangerskapspenger.UttakArbeidsforhold> uttakResultatArbeidsforhold,
                                                   Språkkode språkkode,
                                                   String opphørÅrsak,
                                                   List<Inntektsmelding> inntektsmeldinger,
                                                   UnaryOperator<String> hentNavn) {
        var førsteDato = finnFørsteStønadDato(tilkjentYtelse);
        var sisteDato = finnSisteStønadDato(tilkjentYtelse);

        if (førsteDato.isEmpty() && sisteDato.isEmpty() && !PeriodeIkkeOppfyltÅrsak.BRUKER_ER_DØD.getKode().equals(opphørÅrsak)) {
            førsteDato = finnførsteUttaksDatoFraInnvilget(uttakResultatArbeidsforhold);
            sisteDato = finnSisteUttaksDatoFraInnvilget(uttakResultatArbeidsforhold);
        }

        if (opphørÅrsak != null) {
            var antallArbeidsgivere = tilkjentYtelse.isEmpty() ? SvpMapperUtil.finnAntallArbeidsgivere(uttakResultatArbeidsforhold, inntektsmeldinger,
                hentNavn) : finnAntallArbeidsgivereFraTilkjentYtelse(tilkjentYtelse);
            var builder = OpphørPeriode.ny().medÅrsak(Årsak.of(opphørÅrsak)).medAntallArbeidsgivere(antallArbeidsgivere);

            førsteDato.ifPresent(fd -> builder.medPeriodeFom(fd, språkkode));
            sisteDato.ifPresent(sd -> builder.medPeriodeTom(sd, språkkode));

            return builder.build();
        }
        return null;
    }

    private static Optional<LocalDate> finnførsteUttaksDatoFraInnvilget(List<Svangerskapspenger.UttakArbeidsforhold> uttakResultatArbeidsforhold) {
        return uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.perioder().stream())
            .filter(ur -> PeriodeResultatType.INNVILGET.equals(ur.periodeResultatType()))
            .map(Svangerskapspenger.Uttaksperiode::fom)
            .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnSisteUttaksDatoFraInnvilget(List<Svangerskapspenger.UttakArbeidsforhold> uttakResultatArbeidsforhold) {
        return uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.perioder().stream())
            .filter(ur -> PeriodeResultatType.INNVILGET.equals(ur.periodeResultatType()))
            .map(Svangerskapspenger.Uttaksperiode::tom)
            .max(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnFørsteStønadDato(List<TilkjentYtelsePeriodeDto> perioder) {
        return perioder.stream().filter(p -> p.dagsats() > 0).map(TilkjentYtelsePeriodeDto::fom).min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnSisteStønadDato(List<TilkjentYtelsePeriodeDto> perioder) {
        return perioder.stream().filter(p -> p.dagsats() > 0).map(TilkjentYtelsePeriodeDto::tom).max(LocalDate::compareTo);
    }

    private static int finnAntallArbeidsgivereFraTilkjentYtelse(List<TilkjentYtelsePeriodeDto> perioder) {
        return (int) perioder.stream()
            .flatMap(p -> p.andeler().stream())
            .map(TilkjentYtelseAndelDto::arbeidsgiverReferanse)
            .distinct()
            .count();
    }
}
