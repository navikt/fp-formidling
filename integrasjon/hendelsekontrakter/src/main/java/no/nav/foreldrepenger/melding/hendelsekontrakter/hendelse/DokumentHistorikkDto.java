package no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse;

public class DokumentHistorikkDto {

    private long behandlingId;
    private String dokumentMal;
    private String journalpostId;
    private String historikkinnslagType;
    private String HistorikkAktør;
    private String dokumentBegrunnelse;

    public String getHistorikkinnslagType() {
        return historikkinnslagType;
    }

    public void setHistorikkinnslagType(String historikkinnslagType) {
        this.historikkinnslagType = historikkinnslagType;
    }

    public String getHistorikkAktør() {
        return HistorikkAktør;
    }

    public void setHistorikkAktør(String historikkAktør) {
        HistorikkAktør = historikkAktør;
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
