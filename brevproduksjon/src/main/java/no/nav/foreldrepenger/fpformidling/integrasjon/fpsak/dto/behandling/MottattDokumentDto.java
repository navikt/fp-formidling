package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpformidling.dokumentdata.DokumentKategori;

public class MottattDokumentDto {

    private LocalDate mottattDato;
    private String dokumentTypeId;
    private DokumentKategori dokumentKategori;

    public MottattDokumentDto() {
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public String getDokumentTypeId() {
        return dokumentTypeId;
    }

    public DokumentKategori getDokumentKategori() {
        return dokumentKategori;
    }
}
