package no.nav.foreldrepenger.melding.behandling;

import java.time.LocalDate;

public class MottattDokument {
    private String dokumentTypeId;
    private LocalDate mottattDato;

    public String getDokumentTypeId() {
        return dokumentTypeId;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }
}
