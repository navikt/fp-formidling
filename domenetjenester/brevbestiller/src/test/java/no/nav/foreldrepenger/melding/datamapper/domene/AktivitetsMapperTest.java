package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.AnnenAktivitetListeType;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdListeType;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.UttakAktivitet;
import no.nav.foreldrepenger.melding.uttak.UttakArbeidType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;

public class AktivitetsMapperTest {

    @Test
    public void gradert_alltid_øverst_på_lista_med_arbeidsforhold() {

        List<BeregningsresultatAndel> andelList = new ArrayList<>();
        addResultatAndel(andelList, "1234", "STIFTELSEN FRISCHSENTERET FOR SAMFUNNSØKONOMISK FORSKNING", "123452234", 350, 19, AktivitetStatus.ARBEIDSTAKER);
        addResultatAndel(andelList, "5678", "UNIVERSITETET I OSLO", "999887666655", 1563, 100, AktivitetStatus.ARBEIDSTAKER);

        BeregningsresultatPeriode beregningsresultatPeriode = BeregningsresultatPeriode.ny()
                .medDagsats(1913L)
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 12, 04), LocalDate.of(2020, 3, 6)))
                .medBeregningsresultatAndel(andelList)
                .build();

        List<UttakResultatPeriodeAktivitet> uttakPeriodeList = new ArrayList<>();

        addUttakPeriode(uttakPeriodeList,"1234", "STIFTELSEN FRISCHSENTERET FOR SAMFUNNSØKONOMISK FORSKNING", "123452234", 0, 100, false, UttakArbeidType.ORDINÆRT_ARBEID);
        addUttakPeriode(uttakPeriodeList,"5678", "UNIVERSITETET I OSLO", "999887666655", 20, 80, true, UttakArbeidType.ORDINÆRT_ARBEID);

        UttakResultatPeriode uttakResultatPeriode = UttakResultatPeriode.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 12, 04), LocalDate.of(2020, 3, 6)))
                .medAktiviteter(uttakPeriodeList)
                .build();

        List<BeregningsgrunnlagPrStatusOgAndel> bgPeriode = new ArrayList<>();

        bgPeriode.add(BeregningsgrunnlagPrStatusOgAndel.ny()
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .medAvkortetPrÅr(BigDecimal.valueOf(1913))
                .medDagsats(1913L)
                .build());


        BeregningsgrunnlagPeriode bgPerioder = BeregningsgrunnlagPeriode.ny()
                .medDagsats(1913L)
                .medBeregningsgrunnlagPrStatusOgAndelList(bgPeriode)
                .build();

        ArbeidsforholdListeType arbeidsforholdListeType = AktivitetsMapper.mapArbeidsforholdliste(beregningsresultatPeriode, uttakResultatPeriode, bgPerioder);

        assertThat(arbeidsforholdListeType.getArbeidsforhold().size()).isEqualTo(2);
        assertThat(arbeidsforholdListeType.getArbeidsforhold().get(0).isGradering()).isTrue();
        assertThat(arbeidsforholdListeType.getArbeidsforhold().get(1).isGradering()).isFalse();

    }

    @Test
    public void gradert_alltid_øverst_på_lista_med_annen_aktivetsliste() {

        List<BeregningsresultatAndel> andelList = new ArrayList<>();
        addResultatAndel(andelList, null, "", "", 350, 0, AktivitetStatus.ARBEIDSAVKLARINGSPENGER );
        addResultatAndel(andelList, null, "", "", 400, 0, AktivitetStatus.FRILANSER);
        addResultatAndel(andelList, null, "", "", 600, 0, AktivitetStatus.ARBEIDSAVKLARINGSPENGER);

        BeregningsresultatPeriode beregningsresultatPeriode = BeregningsresultatPeriode.ny()
                .medDagsats(1350L)
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 12, 04), LocalDate.of(2020, 3, 6)))
                .medBeregningsresultatAndel(andelList)
                .build();

        List<UttakResultatPeriodeAktivitet> uttakPeriodeList = new ArrayList<>();

        addUttakPeriode(uttakPeriodeList, null, "", "", 0, 100, false, UttakArbeidType.ANNET);
        addUttakPeriode(uttakPeriodeList,null, "", "", 30, 70, true, UttakArbeidType.FRILANS);
        addUttakPeriode(uttakPeriodeList,null, "", "", 0, 100, false, UttakArbeidType.ANNET);


        UttakResultatPeriode uttakResultatPeriode = UttakResultatPeriode.ny()
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 12, 04), LocalDate.of(2020, 3, 6)))
                .medAktiviteter(uttakPeriodeList)
                .build();

        AnnenAktivitetListeType annenAktivtetListe = AktivitetsMapper.mapAnnenAktivtetListe(beregningsresultatPeriode, uttakResultatPeriode);

        assertThat(annenAktivtetListe.getAnnenAktivitet().size()).isEqualTo(3);
        assertThat(annenAktivtetListe.getAnnenAktivitet().get(0).isGradering()).isTrue();
        assertThat(annenAktivtetListe.getAnnenAktivitet().get(1).isGradering()).isFalse();
        assertThat(annenAktivtetListe.getAnnenAktivitet().get(2).isGradering()).isFalse();

    }

    private boolean addUttakPeriode(List<UttakResultatPeriodeAktivitet> uttakPeriodeList, String arbForhold, String arbNavn, String orgNr, int arbeidsprosent, int utbetalingsprosent, boolean isGradering, UttakArbeidType arbType) {
        return uttakPeriodeList.add(UttakResultatPeriodeAktivitet.ny()
                .medArbeidsprosent(BigDecimal.valueOf(arbeidsprosent))
                .medUtbetalingsprosent(BigDecimal.valueOf(utbetalingsprosent))
                .medGraderingInnvilget(isGradering)
                .medUttakAktivitet(UttakAktivitet.ny()
                        .medArbeidsgiver(new Arbeidsgiver(arbNavn, new Virksomhet(arbNavn, orgNr), null))
                        .medUttakArbeidType(arbType)
                        .medArbeidsforholdRef(ArbeidsforholdRef.ref(arbForhold)).build())
                .build());
    }

    private boolean addResultatAndel(List<BeregningsresultatAndel> andelList, String arbForhold, String arbNavn, String orgNr, int dagsats, int stillingsprosent, AktivitetStatus aktivitetStatus) {
        return andelList.add(BeregningsresultatAndel.ny()
                .medAktivitetStatus(aktivitetStatus)
                .medArbeidsforholdRef(ArbeidsforholdRef.ref(arbForhold))
                .medArbeidsgiver(new Arbeidsgiver(arbNavn, new Virksomhet(arbNavn, orgNr), null))
                .medDagsats(dagsats)
                .medStillingsprosent(BigDecimal.valueOf(stillingsprosent))
                .build());
    }
}

