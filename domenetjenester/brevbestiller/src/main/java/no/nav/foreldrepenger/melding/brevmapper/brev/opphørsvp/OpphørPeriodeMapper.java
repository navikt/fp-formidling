package no.nav.foreldrepenger.melding.brevmapper.brev.opphørsvp;

import no.nav.foreldrepenger.melding.Tuple;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Behandlingsresultat;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.ArbeidsforholdIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatArbeidsforhold;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.melding.vilkår.VilkårType;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class OpphørPeriodeMapper {
    private static Set<String> lovReferanser;
    
    public static Tuple<List<OpphørPeriode>, String> mapOpphørtePerioderOgLovhjemmel(Behandling behandling, FagsakYtelseType ytelseType, List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode) {
        lovReferanser = new TreeSet<>(new LovhjemmelComparator());
        Behandlingsresultat behandlingsresultat = behandling.getBehandlingsresultat();
        Avslagsårsak avslagsårsak = behandlingsresultat.getAvslagsårsak();

        //Henter opphørte perioder fra uttak - hvis finnes.
        List<OpphørPeriode> opphørtePerioder = mapOpphørteUttaksperioder(uttakResultatArbeidsforhold, språkKode);

        //I noen tilfeller er behandlingen opphørt før det har vært uttak
        if(opphørtePerioder.isEmpty()) {
              if(avslagsårsak != null) {
                  opphørtePerioder = mapOpphørteUttaksperioderFraInnvilget(uttakResultatArbeidsforhold, språkKode, avslagsårsak.getKode());
                  if (opphørtePerioder.isEmpty()) {
                      opphørtePerioder.add(opprettSvpOpphørUtenPeriode(avslagsårsak.getKode(), Collections.emptyList()));
                  }
                  lovReferanser.add(leggTilLovreferanse(avslagsårsak, ytelseType));
              } else {
                  //TODO Anja: Usikker på om ikkeOppfyltÅrsak på arbeidsforholdet skal hentes ut når det er opphør. Må teste med noen relle caser. Koden må enten forbedres eller fjernes når dette er avklart
                  opphørtePerioder.addAll(mapOpphørteArbeidsforhold(uttakResultatArbeidsforhold));
                  lovReferanser.add("14-4");
              }
        }

        String lovhjemmelForOpphør = FellesMapper.formaterLovhjemlerUttak(lovReferanser,
                BehandlingMapper.kodeFra(behandlingsresultat.getKonsekvenserForYtelsen()),
                true);

        List<OpphørPeriode> sammenslåttePerioder = OpphørPeriodeMerger.mergePerioder(opphørtePerioder);

        return new Tuple<>(sammenslåttePerioder,lovhjemmelForOpphør);
    }

    private static String leggTilLovreferanse(Avslagsårsak avslagsårsak, FagsakYtelseType ytelseType) {
        Set<VilkårType> vilkårTyper = VilkårType.getVilkårTyper(avslagsårsak);
        return vilkårTyper.stream().map(vt -> vt.getLovReferanse(ytelseType)).findFirst().orElse("");
    }

    private static List<OpphørPeriode> mapOpphørteArbeidsforhold(List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold) {
        return uttakResultatArbeidsforhold.stream()
                .filter(a-> !(ArbeidsforholdIkkeOppfyltÅrsak.INGEN.equals(a.getArbeidsforholdIkkeOppfyltÅrsak())))
                .map(a-> opprettSvpOpphørUtenPeriode(a.getArbeidsforholdIkkeOppfyltÅrsak().getKode(), List.of(a.getArbeidsgiver().navn())))
                .collect(Collectors.toList());
    }

    private static OpphørPeriode opprettSvpOpphørUtenPeriode(String avslagsÅrsak, List<String> arbeidsgiverNavn) {
            var builder = OpphørPeriode.ny().medÅrsak(Årsak.of(avslagsÅrsak));
                    if (!arbeidsgiverNavn.isEmpty()) {
                       builder.medArbeidsgivere(arbeidsgiverNavn);
                    }
            return builder.build();
    }

    private static List<OpphørPeriode> mapOpphørteUttaksperioder( List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode) {
        return uttakResultatArbeidsforhold.stream()
                .flatMap(ura -> ura.getPerioder().stream())
                .filter(ur -> PeriodeResultatType.AVSLÅTT.equals(ur.getPeriodeResultatType()))
                .filter(ur -> PeriodeIkkeOppfyltÅrsak.opphørsAvslagÅrsaker().contains(ur.getPeriodeIkkeOppfyltÅrsak()))
                .map(avslP-> opprettSvpOpphørPeriode(avslP, språkKode))
                .sorted(Comparator.comparing(OpphørPeriode::getStønadsperiodeFomDate))
                .collect(Collectors.toList());
    }


    private static List<OpphørPeriode> mapOpphørteUttaksperioderFraInnvilget( List<SvpUttakResultatArbeidsforhold> uttakResultatArbeidsforhold, Språkkode språkKode, String opphørÅrsak) {
        return uttakResultatArbeidsforhold.stream()
                .flatMap(ura -> ura.getPerioder().stream())
                .filter(ur -> PeriodeResultatType.INNVILGET.equals(ur.getPeriodeResultatType()))
                .map(innvP-> opprettSvpOpphørPeriodeMedÅrsak(innvP, språkKode, opphørÅrsak))
                .collect(Collectors.toList());
    }

    private static OpphørPeriode opprettSvpOpphørPeriode(SvpUttakResultatPeriode p, Språkkode språkkode) {
        lovReferanser.add(p.getPeriodeIkkeOppfyltÅrsak().getLovHjemmelData());
        return OpphørPeriode.ny().medÅrsak(Årsak.of(p.getPeriodeIkkeOppfyltÅrsak().getKode()))
                .medPeriodeFom(p.getTidsperiode().getFomDato(), språkkode)
                .medPeriodeTom(p.getTidsperiode().getTomDato(), språkkode)
                .medArbeidsgivere(List.of(p.getArbeidsgiverNavn()))
                .build();
    }

    private static OpphørPeriode opprettSvpOpphørPeriodeMedÅrsak(SvpUttakResultatPeriode p, Språkkode språkkode, String opphørÅrsak) {
        return OpphørPeriode.ny().medÅrsak(Årsak.of(opphørÅrsak))
                .medPeriodeFom(p.getTidsperiode().getFomDato(), språkkode)
                .medPeriodeTom(p.getTidsperiode().getTomDato(), språkkode)
                .medArbeidsgivere(List.of(p.getArbeidsgiverNavn()))
                .build();
    }

}