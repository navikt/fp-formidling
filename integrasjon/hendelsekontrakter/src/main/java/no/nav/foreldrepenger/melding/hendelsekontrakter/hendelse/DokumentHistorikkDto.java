package no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse;

import no.nav.foreldrepenger.melding.historikk.DokumentHistorikkinnslag;

public class DokumentHistorikkDto {

    private long behandlingId;
    private String dokumentMal;
    private String journalpostId;
    private String historikkinnslagType;
    private String historikkAktør;
    private String dokumentBegrunnelse;
    private String dokumentId;

    public DokumentHistorikkDto() {
    }

    public DokumentHistorikkDto(DokumentHistorikkinnslag historikkInnslag) {
        behandlingId = historikkInnslag.getBehandlingId();
        dokumentMal = historikkInnslag.getDokumentMalType().getKode();
        journalpostId = historikkInnslag.getJournalpostId().getVerdi();
        historikkAktør = historikkInnslag.getHistorikkAktør().getKode();
        historikkinnslagType = historikkInnslag.getHistorikkinnslagType().getMal();
        dokumentId = historikkInnslag.getDokumentId();
    }

    public String getDokumentId() {
        return dokumentId;
    }

    public String getHistorikkinnslagType() {
        return historikkinnslagType;
    }

    public void setHistorikkinnslagType(String historikkinnslagType) {
        this.historikkinnslagType = historikkinnslagType;
    }

    public String getHistorikkAktør() {
        return historikkAktør;
    }

    public void setHistorikkAktør(String historikkAktør) {
        this.historikkAktør = historikkAktør;
    }

    public String getDokumentBegrunnelse() {
        return dokumentBegrunnelse;
    }

    public void setDokumentBegrunnelse(String dokumentBegrunnelse) {
        this.dokumentBegrunnelse = dokumentBegrunnelse;
    }

    public long getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public String getDokumentMal() {
        return dokumentMal;
    }

    public void setDokumentMal(String dokumentMal) {
        this.dokumentMal = dokumentMal;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public void setJournalpostId(String journalpostId) {
        this.journalpostId = journalpostId;
    }
}
