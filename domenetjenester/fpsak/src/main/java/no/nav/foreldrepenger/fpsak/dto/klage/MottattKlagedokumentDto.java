package no.nav.foreldrepenger.fpsak.dto.klage;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MottattKlagedokumentDto {

    private String journalpostId;
    private String dokumentTypeId;
    private String dokumentKategori;
    private Long behandlingId;
    private LocalDate mottattDato;
    private LocalDateTime mottattTidspunkt;
    private String xmlPayload;
    private boolean elektroniskRegistrert;
    private Long fagsakId;

    public MottattKlagedokumentDto() {
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public void setJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
    }

    public String getDokumentTypeId() {
        return dokumentTypeId;
    }

    public void setDokumentTypeId(String dokumentTypeId) {
        this.dokumentTypeId = dokumentTypeId;
    }

    public String getDokumentKategori() {
        return dokumentKategori;
    }

    public void setDokumentKategori(String dokumentKategori) {
        this.dokumentKategori = dokumentKategori;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public LocalDate getMottattDato() {
        return mottattDato;
    }

    public void setMottattDato(LocalDate mottattDato) {
        this.mottattDato = mottattDato;
    }

    public LocalDateTime getMottattTidspunkt() {
        return mottattTidspunkt;
    }

    public void setMottattTidspunkt(LocalDateTime mottattTidspunkt) {
        this.mottattTidspunkt = mottattTidspunkt;
    }

    public String getXmlPayload() {
        return xmlPayload;
    }

    public void setXmlPayload(String xmlPayload) {
        this.xmlPayload = xmlPayload;
    }

    public boolean isElektroniskRegistrert() {
        return elektroniskRegistrert;
    }

    public void setElektroniskRegistrert(boolean elektroniskRegistrert) {
        this.elektroniskRegistrert = elektroniskRegistrert;
    }

    public Long getFagsakId() {
        return fagsakId;
    }

    public void setFagsakId(Long fagsakId) {
        this.fagsakId = fagsakId;
    }
}
