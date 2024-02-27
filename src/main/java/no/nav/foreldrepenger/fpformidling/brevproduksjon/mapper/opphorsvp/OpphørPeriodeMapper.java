package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.opphorsvp;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BehandlingMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.SvpMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.Tuple;
import no.nav.foreldrepenger.fpformidling.domene.behandling.Behandling;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.inntektarbeidytelse.Inntektsmeldinger;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelseAndel;
import no.nav.foreldrepenger.fpformidling.domene.tilkjentytelse.TilkjentYtelsePeriode;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.PeriodeResultatType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.vedtak.exception.TekniskException;

public final class OpphørPeriodeMapper {

    private OpphørPeriodeMapper() {
    }

    public static Tuple<OpphørPeriode, String> mapOpphørtePerioderOgLovhjemmel(Behandling behandling,
                                                                               List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold,
                                                                               Språkkode språkKode,
                                                                               Inntektsmeldinger iay,
                                                                               List<TilkjentYtelsePeriode> tilkjentYtelsePerioder) {
        var lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        var behandlingsresultat = behandling.getBehandlingsresultat();
        var avslagsårsak = behandlingsresultat.getAvslagsårsak();

        //Sjekker om periodeIkkeOppfyltÅrsak finnes fra uttak - oppretter i såfall opphørt periode.
        var opphørtPeriode = mapOpphørtPeriodeMedÅrsakFraAvslåttUttak(uttakResultatArbeidsforhold, språkKode, tilkjentYtelsePerioder, iay,
            lovReferanser);

        //I en del tilfeller må vi hente opphørsårsak fra behandlingsresultatet (ingen uttak eller opphørt inngangsvilkår i en revurdering)
        if (opphørtPeriode == null) {

            if (avslagsårsak != null) {
                opphørtPeriode = mapOpphørtPeriode(tilkjentYtelsePerioder, uttakResultatArbeidsforhold, språkKode, avslagsårsak.getKode(), iay);
                lovReferanser.add(SvpMapperUtil.leggTilLovreferanse(avslagsårsak));
            } else {
                //kan skje i uttak, men ingen tilfeller i prod
                opphørtPeriode = mapOpphørteArbeidsforhold(uttakResultatArbeidsforhold, iay);
                lovReferanser.add("§ 14-4");
            }

            if (opphørtPeriode == null) {
                throw new TekniskException("FPFORMIDLING-100003",
                    String.format("Det er ikke mulig å bestille opphørsbrev uten opphørt periode for behandling UUID %s", behandling.getUuid()));
            }
        }

        var lovhjemmelForOpphør = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
            BehandlingMapper.kodeFra(behandlingsresultat.getKonsekvenserForYtelsen()), true);

        return new Tuple<>(opphørtPeriode, lovhjemmelForOpphør);
    }

    private static OpphørPeriode mapOpphørteArbeidsforhold(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Inntektsmeldinger iay) {
        return uttakResultatArbeidsforhold.stream()
            .filter(a -> !(ArbeidsforholdIkkeOppfyltÅrsak.INGEN.equals(a.getArbeidsforholdIkkeOppfyltÅrsak())))
            .findFirst()
            .map(a -> opprettUtenPeriode(a.getArbeidsforholdIkkeOppfyltÅrsak().getKode(),
                SvpMapperUtil.finnAntallArbeidsgivere(uttakResultatArbeidsforhold, iay)))
            .orElse(null);
    }

    private static OpphørPeriode opprettUtenPeriode(String avslagsÅrsak, int antallArbeidsgivere) {
        return OpphørPeriode.ny().medÅrsak(Årsak.of(avslagsÅrsak)).medAntallArbeidsgivere(antallArbeidsgivere).build();
    }

    private static OpphørPeriode mapOpphørtPeriodeMedÅrsakFraAvslåttUttak(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold,
                                                                          Språkkode språkKode,
                                                                          List<TilkjentYtelsePeriode> tilkjentYtelsePerioder,
                                                                          Inntektsmeldinger iay,
                                                                          TreeSet<String> lovReferanser) {
        var opphørtePerioder = uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.getPerioder().stream())
            .filter(ur -> PeriodeResultatType.AVSLÅTT.equals(ur.getPeriodeResultatType()))
            .filter(ur -> PeriodeIkkeOppfyltÅrsak.opphørsAvslagÅrsaker().contains(ur.getPeriodeIkkeOppfyltÅrsak()))
            .toList();

        if (!opphørtePerioder.isEmpty()) {
            var årsak = finnNyestePeriodeIkkeOppfyltÅrsak(opphørtePerioder);
            if (PeriodeIkkeOppfyltÅrsak.opphørSvpInngangsvilkårMedUttak().contains(årsak)) {
                //spesialhåndtering dersom opphør av inngangsvilkår i en revurdering - henter faktisk årsak fra behandlingsresultat
                return null;
            }
            if (årsak != null) {
                lovReferanser.add(årsak.getLovHjemmelData());
                return mapOpphørtPeriode(tilkjentYtelsePerioder, uttakResultatArbeidsforhold, språkKode, årsak.getKode(), iay);
            }
        }
        return null;
    }

    private static PeriodeIkkeOppfyltÅrsak finnNyestePeriodeIkkeOppfyltÅrsak(List<SvpUttakResultatPeriode> opphørtePerioder) {
        return opphørtePerioder.stream()
            .max(Comparator.comparing(SvpUttakResultatPeriode::getTidsperiode))
            .map(SvpUttakResultatPeriode::getPeriodeIkkeOppfyltÅrsak)
            .orElse(null);
    }

    private static OpphørPeriode mapOpphørtPeriode(List<TilkjentYtelsePeriode> tilkjentYtelse,
                                                   List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold,
                                                   Språkkode språkkode,
                                                   String opphørÅrsak,
                                                   Inntektsmeldinger iay) {
        var førsteDato = finnFørsteStønadDato(tilkjentYtelse);
        var sisteDato = finnSisteStønadDato(tilkjentYtelse);

        if (førsteDato.isEmpty() && sisteDato.isEmpty() && !PeriodeIkkeOppfyltÅrsak.BRUKER_ER_DØD.getKode().equals(opphørÅrsak)) {
            førsteDato = finnførsteUttaksDatoFraInnvilget(uttakResultatArbeidsforhold);
            sisteDato = finnSisteUttaksDatoFraInnvilget(uttakResultatArbeidsforhold);
        }

        if (opphørÅrsak != null) {
            var antallArbeidsgivere = tilkjentYtelse.isEmpty() ? SvpMapperUtil.finnAntallArbeidsgivere(uttakResultatArbeidsforhold,
                iay) : finnAntallArbeidsgivereFraTilkjentYtelse(tilkjentYtelse);
            var builder = OpphørPeriode.ny().medÅrsak(Årsak.of(opphørÅrsak)).medAntallArbeidsgivere(antallArbeidsgivere);

            førsteDato.ifPresent(fd -> builder.medPeriodeFom(fd, språkkode));
            sisteDato.ifPresent(sd -> builder.medPeriodeTom(sd, språkkode));

            return builder.build();
        }
        return null;
    }

    private static Optional<LocalDate> finnførsteUttaksDatoFraInnvilget(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold) {
        return uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.getPerioder().stream())
            .filter(ur -> PeriodeResultatType.INNVILGET.equals(ur.getPeriodeResultatType()))
            .map(p -> p.getTidsperiode().getFomDato())
            .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnSisteUttaksDatoFraInnvilget(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold) {
        return uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.getPerioder().stream())
            .filter(ur -> PeriodeResultatType.INNVILGET.equals(ur.getPeriodeResultatType()))
            .map(p -> p.getTidsperiode().getTomDato())
            .max(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnFørsteStønadDato(List<TilkjentYtelsePeriode> perioder) {
        return perioder.stream().filter(p -> p.getDagsats() > 0).map(TilkjentYtelsePeriode::getPeriodeFom).min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnSisteStønadDato(List<TilkjentYtelsePeriode> perioder) {
        return perioder.stream().filter(p -> p.getDagsats() > 0).map(TilkjentYtelsePeriode::getPeriodeTom).max(LocalDate::compareTo);
    }

    private static int finnAntallArbeidsgivereFraTilkjentYtelse(List<TilkjentYtelsePeriode> perioder) {
        return (int) perioder.stream().flatMap(p -> p.getAndeler().stream()).map(TilkjentYtelseAndel::getArbeidsgiver).distinct().count();
    }
}
