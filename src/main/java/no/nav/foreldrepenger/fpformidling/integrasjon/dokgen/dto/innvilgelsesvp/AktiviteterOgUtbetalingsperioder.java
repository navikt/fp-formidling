package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.innvilgelsesvp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.List;
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record AktiviteterOgUtbetalingsperioder(String beskrivelse, List<Utbetalingsperiode> utbetalingsperioder) {
}
