package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.klage;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class MottattKlagedokumentDto {

    @JsonProperty("mottattDato")
    private LocalDate mottattDato;

    public LocalDate getMottattDato() {
        return mottattDato;
    }
}
