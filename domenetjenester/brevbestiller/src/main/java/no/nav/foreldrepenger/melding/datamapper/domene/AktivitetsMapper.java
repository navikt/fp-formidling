package no.nav.foreldrepenger.melding.datamapper.domene;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.PeriodeÅrsak;
import no.nav.foreldrepenger.melding.brevbestiller.XmlUtil;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeBeregner;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NaturalytelseEndringTypeKode;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.NæringType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ObjectFactory;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.StatusTypeKode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.vedtak.util.Tuple;

public class AktivitetsMapper {

    private static ObjectFactory objectFactory = new ObjectFactory();
    private static Map<AktivitetStatus, StatusTypeKode> aktivitetStatusKodeStatusTypeKodeMap = new HashMap<>();

    static {
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.ARBEIDSTAKER, StatusTypeKode.ARBEIDSTAKER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.FRILANSER, StatusTypeKode.FRILANSER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE, StatusTypeKode.SELVSTENDIG_NÆRINGSDRIVENDE);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_FL, StatusTypeKode.KOMBINERT_AT_FL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_FL_SN, StatusTypeKode.KOMBINERT_AT_FL_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_AT_SN, StatusTypeKode.KOMBINERT_AT_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KOMBINERT_FL_SN, StatusTypeKode.KOMBINERT_FL_SN);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.DAGPENGER, StatusTypeKode.DAGPENGER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.ARBEIDSAVKLARINGSPENGER, StatusTypeKode.ARBEIDSAVKLARINGSPENGER);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.MILITÆR_ELLER_SIVIL, StatusTypeKode.MILITÆR_ELLER_SIVIL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.BRUKERS_ANDEL, StatusTypeKode.BRUKERSANDEL);
        aktivitetStatusKodeStatusTypeKodeMap.put(AktivitetStatus.KUN_YTELSE, StatusTypeKode.KUN_YTELSE);
    }

    static ArbeidsforholdListeType mapArbeidsforholdliste(BeregningsresultatPeriode beregningsresultatPeriode, UttakResultatPeriode uttakResultatPeriode, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        ArbeidsforholdListeType arbeidsforholdListeType = objectFactory.createArbeidsforholdListeType();
        List<ArbeidsforholdType> arbeidsforholdListe = new ArrayList<>();
        for (BeregningsresultatAndel andel : finnArbeidsandeler(beregningsresultatPeriode)) {
            arbeidsforholdListe.add(mapArbeidsforholdAndel(beregningsresultatPeriode, andel, PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.getAktiviteter(), andel), PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel), beregningsgrunnlagPeriode));
        }
        arbeidsforholdListeType.getArbeidsforhold().addAll(arbeidsforholdListe);
        return arbeidsforholdListeType.getArbeidsforhold().isEmpty() ? null : arbeidsforholdListeType;
    }


    private static ArbeidsforholdType mapArbeidsforholdAndel(BeregningsresultatPeriode beregningsresultatPeriode, BeregningsresultatAndel beregningsresultatAndel, Optional<UttakResultatPeriodeAktivitet> uttakAktivitet, Optional<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagAndel, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        ArbeidsforholdType arbeidsforhold = objectFactory.createArbeidsforholdType();
        arbeidsforhold.setAktivitetDagsats((long) beregningsresultatAndel.getDagsats());
        arbeidsforhold.setArbeidsgiverNavn(beregningsresultatAndel.getArbeidsgiver().map(Arbeidsgiver::getNavn).orElse("Andel"));
        if (uttakAktivitet.isPresent()) {
            arbeidsforhold.setUttaksgrad(uttakAktivitet.get().getUtbetalingsprosent().toBigInteger());
            arbeidsforhold.setProsentArbeid(uttakAktivitet.get().getArbeidsprosent().toBigInteger());
            arbeidsforhold.setUtbetalingsgrad(uttakAktivitet.get().getUtbetalingsprosent().toBigInteger());
            arbeidsforhold.setGradering(uttakAktivitet.get().getGraderingInnvilget());
        }
        arbeidsforhold.setStillingsprosent(beregningsresultatAndel.getStillingsprosent().toBigInteger());
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            final BeregningsgrunnlagPrStatusOgAndel statusOgAndel = beregningsgrunnlagAndel.get();
            statusOgAndel.getBgAndelArbeidsforhold().ifPresent(bgAndelArbeidsforhold -> {
                if (bgAndelArbeidsforhold.getNaturalytelseBortfaltPrÅr() != null ||
                        bgAndelArbeidsforhold.getNaturalytelseTilkommetPrÅr() != null)
                    mapNaturalytelse(arbeidsforhold, beregningsgrunnlagPeriode, beregningsresultatPeriode);
            });
        });
        return arbeidsforhold;
    }

    private static void mapNaturalytelse(ArbeidsforholdType arbeidsforhold, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode, BeregningsresultatPeriode beregningsresultatPeriode) {
        for (PeriodeÅrsak årsak : beregningsgrunnlagPeriode.getperiodeÅrsaker()) {
            if (PeriodeÅrsak.NATURALYTELSE_BORTFALT.equals(årsak)) {
                arbeidsforhold.setNaturalytelseEndringType(NaturalytelseEndringTypeKode.STOPP);
                arbeidsforhold.setNaturalytelseNyDagsats(beregningsgrunnlagPeriode.getDagsats());
                arbeidsforhold.setNaturalytelseEndringDato(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
            } else if (PeriodeÅrsak.NATURALYTELSE_TILKOMMER.equals(årsak)) {
                arbeidsforhold.setNaturalytelseEndringType(NaturalytelseEndringTypeKode.START);
                arbeidsforhold.setNaturalytelseNyDagsats(beregningsgrunnlagPeriode.getDagsats());
                arbeidsforhold.setNaturalytelseEndringDato(XmlUtil.finnDatoVerdiAvUtenTidSone(beregningsresultatPeriode.getBeregningsresultatPeriodeFom()));
            } else {
                arbeidsforhold.setNaturalytelseEndringType(NaturalytelseEndringTypeKode.INGEN_ENDRING);
            }
        }
    }

    static NæringListeType mapNæringsliste(BeregningsresultatPeriode beregningsresultatPeriode, UttakResultatPeriode uttakResultatPeriode, BeregningsgrunnlagPeriode beregningsgrunnlagPeriode) {
        NæringListeType næringListe = objectFactory.createNæringListeType();
        finnNæringsandeler(beregningsresultatPeriode).stream().map(andel -> mapNæringsandel(andel,
                PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakResultatPeriode.getAktiviteter(), andel),
                PeriodeBeregner.finnBgPerStatusOgAndelHvisFinnes(beregningsgrunnlagPeriode.getBeregningsgrunnlagPrStatusOgAndelList(), andel)))
                .findFirst()
                .ifPresent(næringListe::setNæring);
        return næringListe.getNæring() != null ? næringListe : null;
        //Optional<UttakResultatPeriodeAktivitet>;
    }

    private static NæringType mapNæringsandel(BeregningsresultatAndel beregingsresultatAndel,
                                              Optional<UttakResultatPeriodeAktivitet> uttakAktivitet,
                                              Optional<BeregningsgrunnlagPrStatusOgAndel> beregningsgrunnlagAndel) {
        NæringType næring = objectFactory.createNæringType();
        næring.setAktivitetDagsats((long) beregingsresultatAndel.getDagsats());
        if (uttakAktivitet.isPresent()) {
            næring.setUttaksgrad(uttakAktivitet.get().getUtbetalingsprosent().toBigInteger());
            næring.setProsentArbeid(uttakAktivitet.get().getArbeidsprosent().toBigInteger());
            næring.setGradering(uttakAktivitet.get().getGraderingInnvilget());
        }
        beregningsgrunnlagAndel.ifPresent(bgAndel -> {
            if (bgAndel.getPgi1() != null) {
                næring.setInntekt1(bgAndel.getPgi1().longValue());
            }
            if (bgAndel.getPgi2() != null) {
                næring.setInntekt2(bgAndel.getPgi2().longValue());
            }
            if (bgAndel.getPgi3() != null) {
                næring.setInntekt3(bgAndel.getPgi3().longValue());
            }
            if (bgAndel.getBeregningsperiodeTom() != null) {
                næring.setSistLignedeÅr(BigInteger.valueOf(bgAndel.getBeregningsperiodeTom().getYear()));
            }
        });
        return næring;
    }

    private static List<BeregningsresultatAndel> finnNæringsandeler(BeregningsresultatPeriode beregningsresultatPeriode) {
        return beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                .filter(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus()))
                .collect(Collectors.toList());
    }

    static AnnenAktivitetListeType mapAnnenAktivtetListe(BeregningsresultatPeriode beregningsresultatPeriode, UttakResultatPeriode uttakPeriode) {
        AnnenAktivitetListeType annenAktivitetListe = objectFactory.createAnnenAktivitetListeType();
        finnAndelerOgUttakAnnenAktivitet(beregningsresultatPeriode, uttakPeriode).map(AktivitetsMapper::mapAnnenAktivitet).forEach(aktivitet -> annenAktivitetListe.getAnnenAktivitet().add(aktivitet));
        return annenAktivitetListe.getAnnenAktivitet().isEmpty() ? null : annenAktivitetListe;
    }

    static AnnenAktivitetType mapAnnenAktivitet(Tuple<BeregningsresultatAndel, Optional<UttakResultatPeriodeAktivitet>> tilkjentYtelseAndelMedTilhørendeUttaksaktivitet) {
        BeregningsresultatAndel beregningsresultatAndel = tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.getElement1();
        AnnenAktivitetType annenAktivitet = objectFactory.createAnnenAktivitetType();
        annenAktivitet.setAktivitetDagsats((long) beregningsresultatAndel.getDagsats());
        annenAktivitet.setAktivitetType(tilStatusTypeKode(beregningsresultatAndel.getAktivitetStatus()));
        tilkjentYtelseAndelMedTilhørendeUttaksaktivitet.getElement2().ifPresent(
                uttakAktivitet -> {
                    annenAktivitet.setGradering(uttakAktivitet.getGraderingInnvilget());
                    annenAktivitet.setUttaksgrad(uttakAktivitet.getUtbetalingsprosent().toBigInteger());
                    annenAktivitet.setProsentArbeid(uttakAktivitet.getArbeidsprosent().toBigInteger());
                });
        return annenAktivitet;
    }

    private static List<BeregningsresultatAndel> finnArbeidsandeler(BeregningsresultatPeriode beregningsresultatPeriode) {
        return beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                .filter(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus()))
                .collect(Collectors.toList());
    }

    private static Stream<Tuple<BeregningsresultatAndel, Optional<UttakResultatPeriodeAktivitet>>> finnAndelerOgUttakAnnenAktivitet(BeregningsresultatPeriode beregningsresultatPeriode, UttakResultatPeriode uttakPeriode) {
        return beregningsresultatPeriode.getBeregningsresultatAndelList().stream()
                .filter(Predicate.not(andel -> AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE.equals(andel.getAktivitetStatus())))
                .filter(Predicate.not(andel -> AktivitetStatus.ARBEIDSTAKER.equals(andel.getAktivitetStatus())))
                .map(andel -> matchBeregningsresultatAndelMedUttaksaktivitet(andel, uttakPeriode));
    }


    private static Tuple<BeregningsresultatAndel, Optional<UttakResultatPeriodeAktivitet>> matchBeregningsresultatAndelMedUttaksaktivitet(BeregningsresultatAndel andel, UttakResultatPeriode uttakPeriode) {
        return new Tuple<>(andel, PeriodeBeregner.finnAktivitetMedStatusHvisFinnes(uttakPeriode.getAktiviteter(), andel));
    }


    private static StatusTypeKode tilStatusTypeKode(AktivitetStatus statuskode) {

        if (aktivitetStatusKodeStatusTypeKodeMap.containsKey(statuskode)) {
            return aktivitetStatusKodeStatusTypeKodeMap.get(statuskode);
        }
        throw new IllegalArgumentException("Utviklerfeil: Fant ikke riktig aktivitetstatus " + statuskode.getKode());
    }
}
