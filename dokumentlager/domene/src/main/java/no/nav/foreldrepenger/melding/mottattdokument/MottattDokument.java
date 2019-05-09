package no.nav.foreldrepenger.melding.mottattdokument;

import java.time.LocalDate;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentKategori;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentTypeId;

public class MottattDokument {

    private LocalDate mottattDato;
    private DokumentTypeId dokumentTypeId;
    private DokumentKategori dokumentKategori;

    public MottattDokument(LocalDate mottattDato, DokumentTypeId dokumentTypeId, DokumentKategori dokumentKategori) {
        this.mottattDato = mottattDato;
        this.dokumentTypeId = dokumentTypeId;
        this.dokumentKategori = dokumentKategori;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public DokumentTypeId getDokumentTypeId() {
        return dokumentTypeId;
    }

    public DokumentKategori getDokumentKategori() {
        return dokumentKategori;
    }
}
