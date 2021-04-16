package no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp;

import static java.util.List.of;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.finnAntallArbeidsgivere;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.finnDagsats;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.finnMånedsbeløp;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.harArbeidsgiverAndel;
import static no.nav.foreldrepenger.melding.brevmapper.brev.innvilgelsefp.BeregningsresultatMapper.harBrukerAndel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatAndel;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.AktivitetStatus;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.virksomhet.Arbeidsgiver;

public class BeregningsresultatMapperTest {

    private static final DatoIntervall UBETYDELIG_PERIODE = DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1));

    @Test
    public void skal_finne_månedsbeløp() {
        // Arrange
        BeregningsresultatFP beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(UBETYDELIG_PERIODE)
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(UBETYDELIG_PERIODE)
                                .build()))
                .build();

        // Act + Assert
        assertThat(finnMånedsbeløp(beregningsresultat)).isEqualTo(2166);
    }

    @Test
    public void skal_finne_dagsats() {
        // Arrange
        BeregningsresultatFP beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(UBETYDELIG_PERIODE)
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(UBETYDELIG_PERIODE)
                                .build()))
                .build();

        // Act + Assert
        assertThat(finnDagsats(beregningsresultat)).isEqualTo(100L);
    }

    @Test
    public void skal_finne_antall_arbeidsgivere() {
        // Arrange
        BeregningsresultatFP beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(
                        BeregningsresultatPeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medBeregningsresultatAndel(of(
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1")) // #1
                                                .build(),
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.FRILANSER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2")) // Frilanser
                                                .build()
                                        ))
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medBeregningsresultatAndel(of(
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1")) // Duplikat
                                                .build(),
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn3", "Referanse3")) // #2
                                                .build(),
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn4", "Referanse4")) // #3
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(finnAntallArbeidsgivere(beregningsresultat)).isEqualTo(3);
    }

    @Test
    public void skal_finne_at_bruker_har_andel() {
        // Arrange
        BeregningsresultatFP beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(
                        BeregningsresultatPeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medBeregningsresultatAndel(of(
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                                                .medBrukerErMottaker(false)
                                                .build()
                                ))
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medBeregningsresultatAndel(of(
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                                                .medBrukerErMottaker(true)
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(harBrukerAndel(beregningsresultat)).isTrue();
    }

    @Test
    public void skal_finne_at_arbeidsgiver_har_andel() {
        // Arrange
        BeregningsresultatFP beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(of(
                        BeregningsresultatPeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medBeregningsresultatAndel(of(
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn1", "Referanse1"))
                                                .medArbeidsgiverErMottaker(false)
                                                .build()
                                ))
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medPeriode(UBETYDELIG_PERIODE)
                                .medBeregningsresultatAndel(of(
                                        BeregningsresultatAndel.ny()
                                                .medAktivitetStatus(AktivitetStatus.ARBEIDSTAKER)
                                                .medArbeidsgiver(new Arbeidsgiver("Navn2", "Referanse2"))
                                                .medArbeidsgiverErMottaker(true)
                                                .build()
                                ))
                                .build()))
                .build();

        // Act + Assert
        assertThat(harArbeidsgiverAndel(beregningsresultat)).isTrue();
    }
}