package no.nav.foreldrepenger.melding.klage;

import java.time.LocalDate;

public class KlageDokument {

    private LocalDate mottattDato;

    public KlageDokument(LocalDate motattDato) {
        this.mottattDato = motattDato;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }
}
