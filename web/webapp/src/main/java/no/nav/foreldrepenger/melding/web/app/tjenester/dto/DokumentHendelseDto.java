package no.nav.foreldrepenger.melding.web.app.tjenester.dto;

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
    private String behandlingType;
    private String ytelseType;
    private String dokumentMal;

    public Long getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(Long behandlingId) {
        this.behandlingId = behandlingId;
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

    public String getDokumentMal() {
        return dokumentMal;
    }

    public void setDokumentMal(String dokumentMal) {
        this.dokumentMal = dokumentMal;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTilBehandlingsId(behandlingId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "behandlingId=" + behandlingId +
                ", behandlingType='" + behandlingType + '\'' +
                ", ytelseType='" + ytelseType + '\'' +
                ", dokumentMal='" + dokumentMal + '\'' +
                '}';
    }
}
