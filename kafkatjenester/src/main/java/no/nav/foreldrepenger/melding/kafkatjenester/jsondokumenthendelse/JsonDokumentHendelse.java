package no.nav.foreldrepenger.melding.kafkatjenester.jsondokumenthendelse;

public class JsonDokumentHendelse {
    private Long behandlingId;
    private String dokumentMal;
    private String behandlingType;
    private String ytelseType;
    private String tittel;
    private String fritekst;
    private boolean gjelderVedtak;

    public String getTittel() {
        return tittel;
    }

    public void setTittel(String tittel) {
        this.tittel = tittel;
    }

    public String getFritekst() {
        return fritekst;
    }

    public void setFritekst(String fritekst) {
        this.fritekst = fritekst;
    }

    public boolean isGjelderVedtak() {
        return gjelderVedtak;
    }

    public void setGjelderVedtak(boolean gjelderVedtak) {
        this.gjelderVedtak = gjelderVedtak;
    }

    public String getBehandlingType() {
        return behandlingType;
    }

    public void setBehandlingType(String behandlingType) {
        this.behandlingType = behandlingType;
    }

    public String getYtelseType() {
        return ytelseType;
    }

    public void setYtelseType(String ytelseType) {
        this.ytelseType = ytelseType;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public String getDokumentMal() {
        return dokumentMal;
    }

    public void setDokumentMal(String dokumentMal) {
        this.dokumentMal = dokumentMal;
    }
}
