package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FritekstDtoTest {

    @Test
    void SjekkOmfraFinnerLenker() {
        var fritekst = "nav.no/familie";

        var resultat = FritekstDto.fra(fritekst);
        assertThat(resultat.getFritekst()).isEqualTo("[nav.no/familie](https://nav.no/familie)");
    }
    @Test
    void SjekkOmfraFinnerLenker2() {
        var fritekst = "https://nav.no/familie/klage";

        var resultat = FritekstDto.fra(fritekst);
        assertThat(resultat.getFritekst()).contains("[nav.no/familie](https://nav.no/familie)");
    }

    @Test
    void fra3() {
        var fritekst = "Du får i gjennomsnitt 37 895 kroner i måneden før skatt.\\n"
            + "Svangerskapspengene blir utbetalt for alle dager, unntatt lørdag og søndag. Fordi det ikke er like \\n"
            + "mange dager i hver måned, vil de månedlige utbetalingene dine variere.\\n"
            + "Pengene er på kontoen din innen den 25. hver måned. Sjekk utbetalingene dine på nav.no/utbetalinger.\\n"
            + "I denne perioden får du svangerskapspenger\\n" + "TOLLBODEN SPISERI:\\n"
            + "Fra og med 16. oktober 2023 til og med 9. februar 2024 får du 100 prosent svangerskapspenger. Du \\n"
            + "får nav.no/klage utbetalt 1 749 kroner per dag før skatt.\\n" + "Vedtaket er gjort etter folketrygdloven § 14‑4.";

        var resultat = FritekstDto.fra(fritekst);
        assertThat(resultat.getFritekst()).contains("[nav.no/utbetalinger](https://nav.no/utbetalinger)");
        assertThat(resultat.getFritekst()).contains("[nav.no/klage](https://nav.no/klage)");
    }

    @Test
    void fra4() {
        var fritekst = "Du får i gjennomsnitt 37 895 kroner i måneden før skatt.\\n"
            + "Svangerskapspengene blir utbetalt for alle dager, unntatt lørdag og søndag. Fordi det ikke er like \\n"
            + "mange dager i hver måned, vil de månedlige utbetalingene dine variere.\\n"
            + "Pengene er på kontoen din innen den 25. hver måned. Sjekk utbetalingene dine på %s.\\n"
            + "I denne perioden får du svangerskapspenger\\n" + "TOLLBODEN SPISERI:\\n"
            + "Fra og med 16. oktober 2023 til og med 9. februar 2024 får du 100 prosent svangerskapspenger. Du \\n"
            + "får %s utbetalt 1 749 kroner per dag før skatt.\\n" + "Vedtaket er gjort etter folketrygdloven § 14‑4.";

        var resultat = FritekstDto.fra(String.format(fritekst, "nav.no/utbetalinger", "nav.no/klage" ));
        assertThat(resultat.getFritekst()).isEqualToIgnoringNewLines(String.format(fritekst, "[nav.no/utbetalinger](https://nav.no/utbetalinger)", "[nav.no/klage](https://nav.no/klage)" ));

    }
}
