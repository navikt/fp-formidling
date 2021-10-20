package no.nav.foreldrepenger.melding.datamapper.domene;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatPeriode;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;

public class BeregningsresultatMapperTest {

    @Test
    public void skal_finne_månedsbeløp() {
        DatoIntervall ubetydeligPeriode = DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusDays(1));
        BeregningsresultatFP beregningsresultat = BeregningsresultatFP.ny()
                .leggTilBeregningsresultatPerioder(List.of(BeregningsresultatPeriode.ny()
                                .medDagsats(100L)
                                .medPeriode(ubetydeligPeriode)
                                .build(),
                        BeregningsresultatPeriode.ny()
                                .medDagsats(100L * 2)
                                .medPeriode(ubetydeligPeriode)
                                .build()))
                .build();
        assertThat(BeregningsresultatMapper.finnMånedsbeløp(beregningsresultat)).isEqualTo(2166);
    }
}
