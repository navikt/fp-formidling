package no.nav.foreldrepenger.melding.brevmapper.brev.opphørsvp;

import no.nav.foreldrepenger.melding.brevmapper.brev.felles.DatoVerktøy;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.opphørsvp.OpphørPeriode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OpphørPeriodeMerger {
    public static List<OpphørPeriode> mergePerioder(List<OpphørPeriode> opphørtePerioder) {
        if (opphørtePerioder.size() <= 1) {
            return opphørtePerioder;
        }
        return slåSammenSammenhengendeEllerLikePerioder(opphørtePerioder);
    }

    private static List<OpphørPeriode> slåSammenSammenhengendeEllerLikePerioder(List<OpphørPeriode> perioder) {
        List<OpphørPeriode> resultat = new ArrayList<>();
        for (int index = 0; index < perioder.size() - 1; index++) {
            boolean sistePeriode = (index == perioder.size() - 2);
            OpphørPeriode periodeEn = perioder.get(index);
            OpphørPeriode periodeTo = perioder.get(index + 1);
            if (erPerioderSammenhengendeEllerLikeOgSkalSlåSammen(periodeEn, periodeTo)) {
                OpphørPeriode nyPeriode = slåSammenPerioder(periodeEn, periodeTo);
                perioder.set(index + 1, nyPeriode);
                if (sistePeriode) {
                    resultat.add(nyPeriode);
                }
            } else {
                resultat.add(periodeEn);
                if (sistePeriode) {
                    resultat.add(periodeTo);
                }
            }
        }
        return resultat;
    }

    private static boolean erPerioderSammenhengendeEllerLikeOgSkalSlåSammen(OpphørPeriode periodeEn, OpphørPeriode periodeTo) {
        return  Objects.equals(periodeEn.getÅrsak(), periodeTo.getÅrsak())
                && (DatoVerktøy.erFomRettEtterTomDato(periodeEn.getStønadsperiodeTomDate(), periodeTo.getStønadsperiodeFomDate()) || periodeneErLike(periodeEn, periodeTo));
    }

    private static boolean periodeneErLike(OpphørPeriode periodeEn, OpphørPeriode periodeTo) {
        return Objects.equals(periodeEn.getStønadsperiodeFomDate(), periodeTo.getStønadsperiodeFomDate())
                && Objects.equals(periodeEn.getStønadsperiodeTomDate(), periodeTo.getStønadsperiodeTomDate());
    }

    private static OpphørPeriode slåSammenPerioder(OpphørPeriode periodeEn, OpphørPeriode periodeTo) {
        List<String> arbeidsgiverNavnListe = new ArrayList<>();
        if (periodeEn.getArbeidsgivere() != null) {
            arbeidsgiverNavnListe.addAll(periodeEn.getArbeidsgivere());
        }
        if (!finnesArbeidsgiverAlleredeIlisten(periodeEn, periodeTo) && (periodeTo.getArbeidsgivere() != null )) {
            arbeidsgiverNavnListe.addAll(periodeTo.getArbeidsgivere());
        }
        return OpphørPeriode.ny()
                .medÅrsak(periodeEn.getÅrsak())
                .medPeriodeFom(periodeEn.getStønadsperiodeFomDate(), periodeEn.getSpråkkode())
                .medPeriodeTom(periodeTo.getStønadsperiodeTomDate(), periodeTo.getSpråkkode())
                .medArbeidsgivere(arbeidsgiverNavnListe)
                .build();
    }

    private static boolean finnesArbeidsgiverAlleredeIlisten(OpphørPeriode periodeEn, OpphørPeriode periodeTo) {
        if (periodeEn.getArbeidsgivere() == null || periodeTo.getArbeidsgivere() == null) {
            return false;
        } else {
            List<String> arbNavnPeriodeEn = periodeEn.getArbeidsgivere().stream().toList();
            boolean finnes = false;
            for (String navn : arbNavnPeriodeEn) {
                finnes = periodeTo.getArbeidsgivere().stream().anyMatch(navn::equals);
                if (finnes) {
                    return true;
                }
            }
            return finnes;
        }
    }
}
