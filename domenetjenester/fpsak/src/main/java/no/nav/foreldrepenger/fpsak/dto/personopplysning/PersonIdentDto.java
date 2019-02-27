package no.nav.foreldrepenger.fpsak.dto.personopplysning;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public abstract class PersonIdentDto {

    private String fnr;
    private Long aktoerId;
    private KodeDto diskresjonskode;

    public String getFnr() {
        return fnr;
    }

    public void setFnr(String fnr) {
        this.fnr = fnr;
    }

    public Long getAktoerId() {
        return aktoerId;
    }

    public void setAktoerId(Long aktoerId) {
        this.aktoerId = aktoerId;
    }

    public KodeDto getDiskresjonskode() {
        return diskresjonskode;
    }

    public void setDiskresjonskode(KodeDto diskresjonskode) {
        this.diskresjonskode = diskresjonskode;
    }
}
