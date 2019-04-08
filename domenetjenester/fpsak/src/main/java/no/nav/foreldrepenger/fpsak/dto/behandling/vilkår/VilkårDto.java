package no.nav.foreldrepenger.fpsak.dto.behandling.vilkår;

import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRawValue;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class VilkårDto {

    private KodeDto vilkarType;
    private KodeDto vilkarStatus;
    private Properties merknadParametere;
    private String avslagKode;
    private String lovReferanse;
    private Boolean overstyrbar;

    @JsonRawValue
    @JsonInclude(Include.NON_NULL)
    private String evaluering;

    @JsonRawValue
    @JsonInclude(Include.NON_NULL)
    private String input;

    public VilkårDto(
            KodeDto vilkårType,
            KodeDto vilkårUtfallType,
            Properties merknadParametere,
            String avslagKode,
            String lovReferanse) {
        this.vilkarType = vilkårType;
        this.vilkarStatus = vilkårUtfallType;
        this.merknadParametere = merknadParametere;
        this.avslagKode = avslagKode;
        this.lovReferanse = lovReferanse;
    }

    public VilkårDto() {
    }

    public KodeDto getVilkarType() {
        return vilkarType;
    }

    public KodeDto getVilkarStatus() {
        return vilkarStatus;
    }

    public Properties getMerknadParametere() {
        return merknadParametere;
    }

    public String getAvslagKode() {
        return avslagKode;
    }

    public String getLovReferanse() {
        return lovReferanse;
    }

    public String getEvaluering() {
        return evaluering;
    }

    public String getInput() {
        return input;
    }

    public void setVilkarType(KodeDto vilkarType) {
        this.vilkarType = vilkarType;
    }

    public void setVilkarStatus(KodeDto vilkarStatus) {
        this.vilkarStatus = vilkarStatus;
    }

    public void setMerknadParametere(Properties merknadParametere) {
        this.merknadParametere = merknadParametere;
    }

    public void setAvslagKode(String avslagKode) {
        this.avslagKode = avslagKode;
    }

    public void setEvaluering(String evaluering) {
        this.evaluering = evaluering;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setLovReferanse(String lovReferanse) {
        this.lovReferanse = lovReferanse;
    }

    public void setOverstyrbar(boolean overstyrbar) {
        this.overstyrbar = overstyrbar;
    }

    public boolean isOverstyrbar() {
        return overstyrbar;
    }
}

