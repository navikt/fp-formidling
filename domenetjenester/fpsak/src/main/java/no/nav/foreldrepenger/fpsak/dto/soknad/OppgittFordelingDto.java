package no.nav.foreldrepenger.fpsak.dto.soknad;

import java.time.LocalDate;
import java.util.Optional;

public class OppgittFordelingDto {
    private Optional<LocalDate> startDatoForPermisjon;

    public OppgittFordelingDto() {
        // trengs for deserialisering av JSON
    }

    public Optional<LocalDate> getStartDatoForPermisjon() {
        return startDatoForPermisjon;
    }

    public void setStartDatoForPermisjon(Optional<LocalDate> startDatoForPermisjon) {
        this.startDatoForPermisjon = startDatoForPermisjon;
    }
}
