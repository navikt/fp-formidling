package no.nav.foreldrepenger.melding.hendelsekontrakter.hendelse;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class DokumentHendelseDto implements AbacDto {

    @NotNull
    @Valid
    private UUID behandlingUuid;
    private String dokumentMal;
    @NotNull
    private String ytelseType;
    private String tittel;
    private String historikkAktør;
    private String fritekst;
    private boolean gjelderVedtak;
    private String arsakskode;
    private boolean erOpphevetKlage;

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

    public String getYtelseType() {
        return ytelseType;
    }

    public void setYtelseType(String ytelseType) {
        this.ytelseType = ytelseType;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public void setBehandlingUuid(UUID behandlingUuid) {
        this.behandlingUuid = behandlingUuid;
    }

    public String getDokumentMal() {
        return dokumentMal;
    }

    public void setDokumentMal(String dokumentMal) {
        this.dokumentMal = dokumentMal;
    }

    public String getArsakskode() {
        return arsakskode;
    }

    public boolean getErOpphevetKlage() {
        return erOpphevetKlage;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTilBehandlingsUUID(getBehandlingUuid());
    }

    @Override
    public String toString() {
        return "DokumentHendelseDto{" +
                "behandlingUuid=" + behandlingUuid +
                ", dokumentMal='" + dokumentMal + '\'' +
                ", ytelseType='" + ytelseType + '\'' +
                ", tittel='" + tittel + '\'' +
                ", historikkAktør='" + historikkAktør + '\'' +
                ", fritekst='" + fritekst + '\'' +
                ", gjelderVedtak=" + gjelderVedtak +
                ", arsakskode='" + arsakskode + '\'' +
                ", erOpphevetKlage=" + erOpphevetKlage +
                '}';
    }
}
