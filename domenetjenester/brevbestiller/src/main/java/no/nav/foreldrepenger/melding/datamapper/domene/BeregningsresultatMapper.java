package no.nav.foreldrepenger.melding.datamapper.domene;

import static no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner.alleAktiviteterHarNullUtbetaling;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import no.nav.foreldrepenger.melding.behandling.ÅrsakskodeMedLovreferanse;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.PeriodeType;
import no.nav.foreldrepenger.melding.uttak.GraderingAvslagÅrsak;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;

@ApplicationScoped
public class BeregningsresultatMapper {

    private static ObjectFactory objectFactory = new ObjectFactory();

    public static BigInteger antallArbeidsgivere(BeregningsresultatFP beregningsresultat) {
        return BigInteger.valueOf(beregningsresultat.getBeregningsresultatPerioder().stream()
                .map(BeregningsresultatPeriode::getBeregningsresultatAndelList)
                .flatMap(Collection::stream)
                .filter(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus()))
                .map(BeregningsresultatAndel::getArbeidsgiver)
                .distinct()
                .count());

    }

    public static PeriodeListeType mapPeriodeListe(List<BeregningsresultatPeriode> beregningsresultatPerioder,
                                                   UttakResultatPerioder uttakResultatPerioder,
                                                   List<BeregningsgrunnlagPeriode> beregningingsgrunnlagperioder) {
        PeriodeListeType periodeListe = objectFactory.createPeriodeListeType();
        for (BeregningsresultatPeriode beregningsresultatPeriode : beregningsresultatPerioder) {
            //TODO Må nok ignorere "ukjente" perioder
            periodeListe.getPeriode().add(mapEnkelPeriode(beregningsresultatPeriode,
                    PeriodeBeregner.finnUttaksPeriode(beregningsresultatPeriode, uttakResultatPerioder.getPerioder()),
                    PeriodeBeregner.finnBeregninsgrunnlagperiode(beregningsresultatPeriode, beregningingsgrunnlagperioder)
            ));
        }
        return periodeListe;
    }

    static PeriodeType mapEnkelPeriode(BeregningsresultatPeriode beregningsresultatPeriode,
                                       UttakResultatPeriode uttakResultatPeriode,
                                       BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        //TODO Avslåtte perioder må mappes seperat da de ikke har tilkjent ytelse
        PeriodeType periode = objectFactory.createPeriodeType();
        periode.setAntallTapteDager(BigInteger.valueOf(mapAntallTapteDagerFra(uttakResultatPeriode.getAktiviteter())));
        periode.setInnvilget(uttakResultatPeriode.isInnvilget() && !erGraderingAvslått(uttakResultatPeriode));
        periode.setPeriodeFom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
        periode.setPeriodeTom(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeTom()));
        ÅrsakskodeMedLovreferanse årsakskodeMedLovreferanse = utledÅrsakskode(uttakResultatPeriode);
        periode.setÅrsak(årsakskodeMedLovreferanse.getKode());
        periode.setPeriodeDagsats(beregningsresultatPeriode.getDagsats());

        periode.setArbeidsforholdListe(AktivtetsMapper.mapArbeidsforholdliste(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode));
        periode.setNæringListe(AktivtetsMapper.mapNæringsliste(beregningsresultatPeriode, uttakResultatPeriode, beregningsgrunnlagPeriode));
        periode.setAnnenAktivitetListe(AktivtetsMapper.mapAnnenAktivtetListe(beregningsresultatPeriode, uttakResultatPeriode));
        return periode;
    }


    private static int mapAntallTapteDagerFra(List<UttakResultatPeriodeAktivitet> uttakAktiviteter) {
        return alleAktiviteterHarNullUtbetaling(uttakAktiviteter) ?
                uttakAktiviteter.stream()
                        .mapToInt(UttakResultatPeriodeAktivitet::getTrekkdager)
                        .max()
                        .orElse(0) : 0;
    }


    private static ÅrsakskodeMedLovreferanse utledÅrsakskode(UttakResultatPeriode uttakPeriode) {
        if (erGraderingAvslått(uttakPeriode) && erInnvilget(uttakPeriode)) {
            return uttakPeriode.getGraderingAvslagÅrsak();
        } else if (uttakPeriode.getPeriodeResultatÅrsak() != null) {
            return uttakPeriode.getPeriodeResultatÅrsak();
        }
        return PeriodeResultatÅrsak.UKJENT;
    }

    private static boolean erInnvilget(UttakResultatPeriode uttakPeriode) {
        return PeriodeResultatType.INNVILGET.equals(uttakPeriode.getPeriodeResultatType());
    }

    private static boolean erGraderingAvslått(UttakResultatPeriode uttakPeriode) {
        return !uttakPeriode.erGraderingInnvilget() && erGraderingÅrsakKjent(uttakPeriode.getGraderingAvslagÅrsak());
    }

    private static boolean erGraderingÅrsakKjent(GraderingAvslagÅrsak årsak) {
        return årsak != null && !årsak.getKode().equals(GraderingAvslagÅrsak.UKJENT.getKode());
    }

}
