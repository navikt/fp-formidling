package no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class DokumentHendelseDto implements AbacDto {

    @NotNull
    @Min(0)
    @Max(Long.MAX_VALUE)
    private Long behandlingId;
    private String dokumentMal;
    private String behandlingType;
    @NotNull
    private String ytelseType;
    private String tittel;
    private String historikkAktør;
    private String fritekst;
    private boolean gjelderVedtak;

    public String getHistorikkAktør() {
        return historikkAktør;
    }

    public void setHistorikkAktør(String historikkAktør) {
        this.historikkAktør = historikkAktør;
    }

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


    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTilBehandlingsId(getBehandlingId());
    }

    @Override
    public String toString() {
        return "DokumentHendelseDto{" +
                "behandlingId=" + behandlingId +
                ", dokumentMal='" + dokumentMal + '\'' +
                ", behandlingType='" + behandlingType + '\'' +
                ", ytelseType='" + ytelseType + '\'' +
                ", tittel='" + tittel + '\'' +
                ", historikkAktør='" + historikkAktør + '\'' +
                ", fritekst='" + fritekst + '\'' +
                ", gjelderVedtak=" + gjelderVedtak +
                '}';
    }
}
