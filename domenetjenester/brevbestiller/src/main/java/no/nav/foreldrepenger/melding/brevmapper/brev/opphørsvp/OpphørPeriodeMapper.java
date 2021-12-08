package no.nav.foreldrepenger.melding.brevmapper.brev.opphørsvp;

import no.nav.foreldrepenger.melding.Tuple;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class OpphørPeriodeMapper {
    private static Set<String> lovReferanser;
    
    public static Tuple<OpphørPeriode, String> mapOpphørtePerioderOgLovhjemmel(Behandling behandling, List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode, InntektArbeidYtelse iay) {
        lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        Avslagsårsak avslagsårsak = behandlingsresultat.getAvslagsårsak();

        //Henter opphørte perioder fra uttak - hvis finnes.
        OpphørPeriode opphørtPeriode = mapOpphørtUttaksperiode(uttakResultatArbeidsforhold, språkKode);

        //I noen tilfeller er behandlingen opphørt før det har vært uttak
        if(opphørtPeriode == null) {
              if(avslagsårsak != null) {
                  opphørtPeriode = mapOpphørteUttaksperioderFraInnvilget(uttakResultatArbeidsforhold, språkKode, avslagsårsak.getKode());
                  if (opphørtPeriode == null) {
                      opphørtPeriode = opprettSvpOpphørUtenPeriode(avslagsårsak.getKode(), SvpMapperUtil.finnAntallArbeidsgivere(uttakResultatArbeidsforhold, iay));
                  }
                  lovReferanser.add(SvpMapperUtil.leggTilLovreferanse(avslagsårsak));
              } else {
                  //TODO Anja Avklare om dette er nødvendig. Hvis ikke fjerne koden
                  opphørtPeriode = mapOpphørteArbeidsforhold(uttakResultatArbeidsforhold, iay);
                  lovReferanser.add("§ 14-4");
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
                .map(a-> opprettSvpOpphørUtenPeriode(a.getArbeidsforholdIkkeOppfyltÅrsak().getKode(), SvpMapperUtil.finnAntallArbeidsgivere(uttakResultatArbeidsforhold, iay)))
                .orElse(null);
    }

    private static OpphørPeriode opprettSvpOpphørUtenPeriode(String avslagsÅrsak, int antallArbeidsgivere) {
        return OpphørPeriode.ny()
                .medÅrsak(Årsak.of(avslagsÅrsak))
                .medAntallArbeidsgivere(antallArbeidsgivere)
                .build();
    }

    private static OpphørPeriode mapOpphørtUttaksperiode(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode) {
        List<SvpUttakResultatPeriode> opphørtePerioder = uttakResultatArbeidsforhold.stream()
            .flatMap(ura -> ura.getPerioder().stream())
                .filter(ur -> PeriodeResultatType.AVSLÅTT.equals(ur.getPeriodeResultatType()))
                .filter(ur -> PeriodeIkkeOppfyltÅrsak.opphørsAvslagÅrsaker().contains(ur.getPeriodeIkkeOppfyltÅrsak()))
                .collect(Collectors.toList());

        if(opphørtePerioder.isEmpty()){
            return null;
        } else {
            return opprettSvpOpphørPeriode(opphørtePerioder, språkKode);
        }
    }

    private static OpphørPeriode mapOpphørteUttaksperioderFraInnvilget( List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode, String opphørÅrsak) {
        List<SvpUttakResultatPeriode> innvilgedePerioder = uttakResultatArbeidsforhold.stream()
                .flatMap(ura -> ura.getPerioder().stream())
                .filter(ur -> PeriodeResultatType.INNVILGET.equals(ur.getPeriodeResultatType()))
                .collect(Collectors.toList());

        if(innvilgedePerioder.isEmpty()) {
            return null;
        } else {
            return opprettSvpOpphørPeriodeMedÅrsak(innvilgedePerioder, språkKode, opphørÅrsak);
        }
    }

    private static OpphørPeriode opprettSvpOpphørPeriode(List<SvpUttakResultatPeriode> opphørtePerioder,  Språkkode språkkode) {
        PeriodeIkkeOppfyltÅrsak årsak = opphørtePerioder.stream().map(SvpUttakResultatPeriode::getPeriodeIkkeOppfyltÅrsak).findFirst().orElse(null);
        Optional<LocalDate> førsteDato = finnFørsteUttaksdato(opphørtePerioder);
        Optional<LocalDate> sisteDato = finnSisteUttaksdato(opphørtePerioder);

        if(årsak!=null) {
            lovReferanser.add(årsak.getLovHjemmelData());

            var builder = OpphørPeriode.ny().medÅrsak(Årsak.of(årsak.getKode()))
                    .medAntallArbeidsgivere(OpphørPeriodeMapper.finnAntallArbeidsgivereFraUttaket(opphørtePerioder));
            førsteDato.ifPresent(fd -> builder.medPeriodeFom(fd, språkkode));
            sisteDato.ifPresent(sd -> builder.medPeriodeTom(sd, språkkode));

            return builder.build();
        }
        return null;
    }

    private static OpphørPeriode opprettSvpOpphørPeriodeMedÅrsak(List<SvpUttakResultatPeriode> innvilgedePerioder,  Språkkode språkkode, String opphørÅrsak) {
        Optional<LocalDate> førsteDato = finnFørsteUttaksdato(innvilgedePerioder);
        Optional<LocalDate> sisteDato = finnSisteUttaksdato(innvilgedePerioder);

        var builder = OpphørPeriode.ny().medÅrsak(Årsak.of(opphørÅrsak))
                .medAntallArbeidsgivere(OpphørPeriodeMapper.finnAntallArbeidsgivereFraUttaket(innvilgedePerioder));
        førsteDato.ifPresent(fd -> builder.medPeriodeFom(fd, språkkode));
        sisteDato.ifPresent(sd -> builder.medPeriodeTom(sd, språkkode));

        return builder.build();
    }

    private static Optional<LocalDate> finnFørsteUttaksdato(List<SvpUttakResultatPeriode> perioder) {
        return perioder.stream().map(p-> p.getTidsperiode().getFomDato()).min(LocalDate::compareTo);
    }

    private static Optional<LocalDate> finnSisteUttaksdato(List<SvpUttakResultatPeriode> perioder) {
        return perioder.stream().map(p-> p.getTidsperiode().getTomDato()).max(LocalDate::compareTo);
    }

    private static int finnAntallArbeidsgivereFraUttaket(List<SvpUttakResultatPeriode> perioder) {
        return (int) perioder.stream().map(SvpUttakResultatPeriode::getArbeidsgiverNavn).distinct().count();
    }
}