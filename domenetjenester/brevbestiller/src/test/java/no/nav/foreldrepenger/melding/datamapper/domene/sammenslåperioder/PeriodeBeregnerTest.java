package no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import no.nav.foreldrepenger.melding.uttak.Stønadskonto;
import no.nav.foreldrepenger.melding.uttak.StønadskontoType;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPeriodeAktivitet;

public class PeriodeBeregnerTest {

    List<UttakResultatPeriode> perioder;
    Optional<Stønadskonto> stønadskonto;


    @Before
    public void setup() {
        perioder = List.of(opprettUttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, BigDecimal.TEN, BigDecimal.ZERO));
        stønadskonto = Optional.of(opprettStønadskonto(10));
    }

    @Test
    public void skalFinne0Dager_hvis_ingenDagerMed_foreldrepengerFørFødsel() {
        assertThat(PeriodeBeregner.beregnTapteDagerFørTermin(Collections.emptyList(), Optional.empty())).isZero();
        assertThat(PeriodeBeregner.beregnTapteDagerFørTermin(Collections.emptyList(), Optional.of(opprettStønadskonto(0)))).isZero();
    }

    @Test
    public void skalFinneDagerMedTapteForeldrepenger() {
        assertThat(PeriodeBeregner.beregnTapteDagerFørTermin(perioder, stønadskonto)).isEqualTo(10);
    }

    @Test
    public void skalFinne0HvisIngenTapteDager() {
        perioder = List.of(opprettUttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, BigDecimal.TEN, BigDecimal.TEN));
        assertThat(PeriodeBeregner.beregnTapteDagerFørTermin(perioder, stønadskonto)).isZero();
    }

    @Test
    public void skalFinneAlleDagerTaptHvisIngenUttakMedKontoType() {
        perioder = List.of(opprettUttaksperiode(StønadskontoType.FORELDREPENGER, BigDecimal.TEN, BigDecimal.ZERO));
        assertThat(PeriodeBeregner.beregnTapteDagerFørTermin(perioder, stønadskonto)).isEqualTo(10);
    }

    @Test
    public void tapteDagerKanIkkeOverstigeTotaleDager() {
        perioder = List.of(opprettUttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, BigDecimal.TEN, BigDecimal.ZERO));
        stønadskonto = Optional.of(opprettStønadskonto(1));
        assertThat(PeriodeBeregner.beregnTapteDagerFørTermin(perioder, stønadskonto)).isEqualTo(1);
    }

    @Test
    public void AktiviteterSkalBareTrekkesFraEnGang() {
        perioder = List.of(opprettUttaksperiodeMedMangeLikeAktiviteter(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, BigDecimal.TEN, BigDecimal.TEN));
        stønadskonto = Optional.of(opprettStønadskonto(15));
        assertThat(PeriodeBeregner.beregnTapteDagerFørTermin(perioder, stønadskonto)).isEqualTo(5);
    }

    private UttakResultatPeriode opprettUttaksperiode(StønadskontoType trekkonto, BigDecimal trekkdager, BigDecimal utbetalingsprosent) {
        return UttakResultatPeriode.ny()
                .medAktiviteter(List.of(opprettAktivtet(trekkonto, trekkdager, utbetalingsprosent)))
                .build();
    }

    private UttakResultatPeriode opprettUttaksperiodeMedMangeLikeAktiviteter(StønadskontoType trekkonto, BigDecimal trekkdager, BigDecimal utbetalingsprosent) {
        return UttakResultatPeriode.ny()
                .medAktiviteter(List.of(opprettAktivtet(trekkonto, trekkdager, utbetalingsprosent),
                        opprettAktivtet(trekkonto, trekkdager, utbetalingsprosent),
                        opprettAktivtet(trekkonto, trekkdager, utbetalingsprosent)))
                .build();
    }

    private UttakResultatPeriodeAktivitet opprettAktivtet(StønadskontoType trekkonto, BigDecimal trekkdager, BigDecimal utbetalingsprosent) {
        return UttakResultatPeriodeAktivitet.ny()
                .medTrekkonto(trekkonto)
                .medTrekkdager(trekkdager)
                .medUtbetalingsprosent(utbetalingsprosent)
                .build();
    }


    private Stønadskonto opprettStønadskonto(int maxDager) {
        return new Stønadskonto(maxDager, StønadskontoType.FORELDREPENGER_FØR_FØDSEL, 0);
    }

}
