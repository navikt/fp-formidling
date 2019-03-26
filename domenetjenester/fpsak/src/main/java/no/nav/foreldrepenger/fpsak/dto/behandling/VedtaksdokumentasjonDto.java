package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.time.LocalDate;

public class VedtaksdokumentasjonDto {

    // dokumentId er egentlig id for behandlingen vedtaket tilhører
    private String dokumentId;
    private String tittel;
    private LocalDate opprettetDato;

    public String getDokumentId() {
        return dokumentId;
    }

    public void setDokumentId(String dokumentId) {
        this.dokumentId = dokumentId;
    }

    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public LocalDate getOpprettetDato() {
        return opprettetDato;
    }

    public void setOpprettetDato(LocalDate opprettetDato) {
        this.opprettetDato = opprettetDato;
    }

}
