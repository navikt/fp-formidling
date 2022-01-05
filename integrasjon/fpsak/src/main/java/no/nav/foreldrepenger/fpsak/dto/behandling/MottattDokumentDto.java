package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.time.LocalDate;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class MottattDokumentDto {

    private LocalDate mottattDato;
    private KodeDto dokumentTypeId;
    private KodeDto dokumentKategori;

    public MottattDokumentDto() {
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public KodeDto getDokumentTypeId() {
        return dokumentTypeId;
    }

    public KodeDto getDokumentKategori() {
        return dokumentKategori;
    }
}
