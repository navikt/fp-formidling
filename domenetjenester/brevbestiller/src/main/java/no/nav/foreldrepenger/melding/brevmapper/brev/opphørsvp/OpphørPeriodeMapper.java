package no.nav.foreldrepenger.melding.brevmapper.brev.opphørsvp;

import no.nav.foreldrepenger.melding.Tuple;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.brevmapper.brev.felles.SvpMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.inntektarbeidytelse.InntektArbeidYtelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.vedtak.exception.TekniskException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class OpphørPeriodeMapper {
    private static Set<String> lovReferanser;
    
    public static Tuple<OpphørPeriode, String> mapOpphørtePerioderOgLovhjemmel(Behandling behandling, List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode, InntektArbeidYtelse iay, List<BeregningsresultatPeriode> tilkjentYtelsePerioder) {
        lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        Avslagsårsak avslagsårsak = behandlingsresultat.getAvslagsårsak();

        //Sjekker om periodeIkkeOppfyltÅrsak finnes fra uttak - oppretter i såfall opphørt periode.
        OpphørPeriode opphørtPeriode = mapOpphørtPeriodeMedÅrsakFraAvslåttUttak(uttakResultatArbeidsforhold, språkKode, tilkjentYtelsePerioder, iay);

        //I en del tilfeller er behandlingen opphørt før det har kommet så langt som å beregne uttak
        if (opphørtPeriode == null) {

            if (avslagsårsak != null) {
                opphørtPeriode = mapOpphørtPeriode(tilkjentYtelsePerioder, uttakResultatArbeidsforhold, språkKode, avslagsårsak.getKode(), iay);
                lovReferanser.add(SvpMapperUtil.leggTilLovreferanse(avslagsårsak));
            } else {
                //TODO Anja Avklare om dette er nødvendig. Hvis ikke fjerne koden
                opphørtPeriode = mapOpphørteArbeidsforhold(uttakResultatArbeidsforhold, iay);
                lovReferanser.add("§ 14-4");
            }

            if (opphørtPeriode == null) {
                throw  new TekniskException("FPFORMIDLING-100003", String.format("Det er ikke mulig å bestille opphørsbrev uten opphørt periode for behandling UUID %s", behandling.getUuid()));
            }
        }

        String lovhjemmelForOpphør = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
                BehandlingMapper.kodeFra(behandlingsresultat.getKonsekvenserForYtelsen()),
                true);

        return new Tuple<>(opphørtPeriode,lovhjemmelForOpphør);
    }

    private static OpphørPeriode mapOpphørteArbeidsforhold(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, InntektArbeidYtelse iay) {
        return uttakResultatArbeidsforhold.stream()
                .filter(a-> !(ArbeidsforholdIkkeOppfyltÅrsak.INGEN.equals(a.getArbeidsforholdIkkeOppfyltÅrsak())))
                .findFirst()
                .map(a-> opprettUtenPeriode(a.getArbeidsforholdIkkeOppfyltÅrsak().getKode(), SvpMapperUtil.finnAntallArbeidsgivere(uttakResultatArbeidsforhold, iay)))
                .orElse(null);
    }

    private static OpphørPeriode opprettUtenPeriode(String avslagsÅrsak, int antallArbeidsgivere) {
        return OpphørPeriode.ny()
                .medÅrsak(Årsak.of(avslagsÅrsak))
                .medAntallArbeidsgivere(antallArbeidsgivere)
                .build();
    }

    private static OpphørPeriode mapOpphørtPeriodeMedÅrsakFraAvslåttUttak(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode, List<BeregningsresultatPeriode> tilkjentYtelsePerioder, InntektArbeidYtelse iay) {
        List<SvpUttakResultatPeriode> opphørtePerioder = uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.getPerioder().stream())
                .filter(ur -> PeriodeResultatType.AVSLÅTT.equals(ur.getPeriodeResultatType()))
                .filter(ur -> PeriodeIkkeOppfyltÅrsak.opphørsAvslagÅrsaker().contains(ur.getPeriodeIkkeOppfyltÅrsak()))
                .collect(Collectors.toList());

        if (!opphørtePerioder.isEmpty()) {
            PeriodeIkkeOppfyltÅrsak årsak = finnPeriodeIkkeOppfyltÅrsak(opphørtePerioder);
            if (årsak != null) {
                lovReferanser.add(årsak.getLovHjemmelData());
                return mapOpphørtPeriode(tilkjentYtelsePerioder, uttakResultatArbeidsforhold, språkKode, årsak.getKode(), iay);
            }
        }
        return null;
    }

    private static PeriodeIkkeOppfyltÅrsak finnPeriodeIkkeOppfyltÅrsak(List<SvpUttakResultatPeriode> opphørtePerioder) {
        return opphørtePerioder.stream().map(SvpUttakResultatPeriode::getPeriodeIkkeOppfyltÅrsak).findFirst().orElse(null);
    }

    private static OpphørPeriode mapOpphørtPeriode(List<BeregningsresultatPeriode> tilkjentYtelse, List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkkode, String opphørÅrsak, InntektArbeidYtelse iay) {
        Optional<LocalDate> førsteDato = finnFørsteStønadDato(tilkjentYtelse);
        Optional<LocalDate> sisteDato = finnSisteStønadDato(tilkjentYtelse);

        if (førsteDato.isEmpty() && sisteDato.isEmpty() && !PeriodeIkkeOppfyltÅrsak._8304.getKode().equals(opphørÅrsak)) {
            førsteDato = finnførsteUttaksDatoFraInnvilget(uttakResultatArbeidsforhold);
            sisteDato = finnSisteUttaksDatoFraInnvilget(uttakResultatArbeidsforhold);
        }

        if (opphørÅrsak != null) {
            int antallArbeidsgivere = tilkjentYtelse.isEmpty() ?  SvpMapperUtil.finnAntallArbeidsgivere(uttakResultatArbeidsforhold, iay) : finnAntallArbeidsgivereFraTilkjentYtelse(tilkjentYtelse);
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
                .map(p-> p.getTidsperiode().getFomDato())
                .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnSisteUttaksDatoFraInnvilget(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold) {
        return uttakResultatArbeidsforhold.stream()
                .flatMap(ura -> ura.getPerioder().stream())
                .filter(ur -> PeriodeResultatType.INNVILGET.equals(ur.getPeriodeResultatType()))
                .map(p -> p.getTidsperiode().getTomDato())
                .max(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnFørsteStønadDato(List<BeregningsresultatPeriode> perioder) {
        return perioder.stream()
                .filter(p-> p.getDagsats() > 0)
                .map(BeregningsresultatPeriode::getBeregningsresultatPeriodeFom)
                .min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnSisteStønadDato(List<BeregningsresultatPeriode> perioder) {
        return perioder.stream()
                .filter(p-> p.getDagsats() > 0)
                .map(BeregningsresultatPeriode::getBeregningsresultatPeriodeTom)
                .max(LocalDate::compareTo);
    }

    private static int finnAntallArbeidsgivereFraTilkjentYtelse(List<BeregningsresultatPeriode> perioder) {
        return (int) perioder.stream().flatMap(p-> p.getBeregningsresultatAndelList().stream()).map(BeregningsresultatAndel::getArbeidsgiver).distinct().count();
    }
}