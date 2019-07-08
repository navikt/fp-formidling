package no.nav.foreldrepenger.melding.datamapper.domene;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndel;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innvilget.foreldrepenger.ArbeidsforholdListeType;
import no.nav.foreldrepenger.melding.typer.ArbeidsforholdRef;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.*;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;
import no.nav.foreldrepenger.melding.virksomhet.Virksomhet;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AktivitetsMapperTest {

    @Test
    public void gradert_alltid_øverst_på_lista_med_arbeidsforhold() {

        List<BeregningsresultatAndel> andelList = new ArrayList<>();
        addResultatAndel(andelList, "1234", "STIFTELSEN FRISCHSENTERET FOR SAMFUNNSØKONOMISK FORSKNING", "123452234", 350, 19);
        addResultatAndel(andelList, "5678", "UNIVERSITETET I OSLO", "999887666655", 1563, 100);

        BeregningsresultatPeriode beregningsresultatPeriode = BeregningsresultatPeriode.ny()
                .medDagsats(1913L)
                .medPeriode(DatoIntervall.fraOgMedTilOgMed(LocalDate.of(2019, 12, 04), LocalDate.of(2020, 3, 6)))
                .medBeregningsresultatAndel(andelList)
                .build();

        List<UttakResultatPeriodeAktivitet> uttakPeriodeList = new ArrayList<>();

        addUttakPeriode(uttakPeriodeList,"1234", "STIFTELSEN FRISCHSENTERET FOR SAMFUNNSØKONOMISK FORSKNING", "123452234", 0, 100, false);
        addUttakPeriode(uttakPeriodeList,"5678", "UNIVERSITETET I OSLO", "999887666655", 20, 80, true);

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

    private boolean addUttakPeriode(List<UttakResultatPeriodeAktivitet> uttakPeriodeList, String arbForhold, String arbNavn, String orgNr, int arbeidsprosent, int utbetalingsprosent, boolean isGradering) {
        return uttakPeriodeList.add(UttakResultatPeriodeAktivitet.ny()
                .medArbeidsprosent(BigDecimal.valueOf(arbeidsprosent))
                .medUtbetalingsprosent(BigDecimal.valueOf(utbetalingsprosent))
                .medGraderingInnvilget(isGradering)
                .medUttakAktivitet(UttakAktivitet.ny()
                        .medArbeidsgiver(new Arbeidsgiver(arbNavn, new Virksomhet(arbNavn, orgNr), null))
                        .medUttakArbeidType(UttakArbeidType.ORDINÆRT_ARBEID)
                        .medArbeidsforholdRef(ArbeidsforholdRef.ref(arbForhold)).build())
                .build());
    }

    private boolean addResultatAndel(List<BeregningsresultatAndel> andelList, String arbForhold, String arbNavn, String orgNr, int dagsats, int stillingsprosent) {
        return andelList.add(BeregningsresultatAndel.ny()
                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                .medArbeidsforholdRef(ArbeidsforholdRef.ref(arbForhold))
                .medArbeidsgiver(new Arbeidsgiver(arbNavn, new Virksomhet(arbNavn, orgNr), null))
                .medDagsats(dagsats)
                .medStillingsprosent(BigDecimal.valueOf(stillingsprosent))
                .build());
    }
}