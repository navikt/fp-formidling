package no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AksjonspunktDto {
    private KodeDto definisjon;
    private KodeDto status;
    private String begrunnelse;
    private KodeDto vilkarType;
    private Boolean toTrinnsBehandling;
    private Boolean toTrinnsBehandlingGodkjent;
    private Set<KodeDto> vurderPaNyttArsaker;
    private String besluttersBegrunnelse;
    private KodeDto KodeDto;
    private Boolean kanLoses;
    private Boolean erAktivt;

    public AksjonspunktDto() {
    }

    public void setDefinisjon(KodeDto definisjon) {
        this.definisjon = definisjon;
    }

    public void setStatus(KodeDto status) {
        this.status = status;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public void setVilkarType(KodeDto vilkarType) {
        this.vilkarType = vilkarType;
    }

    public void setToTrinnsBehandling(Boolean toTrinnsBehandling) {
        this.toTrinnsBehandling = toTrinnsBehandling;
    }

    public void setToTrinnsBehandlingGodkjent(Boolean toTrinnsBehandlingGodkjent) {
        this.toTrinnsBehandlingGodkjent = toTrinnsBehandlingGodkjent;
    }

    public void setVurderPaNyttArsaker(Set<KodeDto> vurderPaNyttArsaker) {
        this.vurderPaNyttArsaker = vurderPaNyttArsaker;
    }

    public void setBesluttersBegrunnelse(String besluttersBegrunnelse) {
        this.besluttersBegrunnelse = besluttersBegrunnelse;
    }

    public void setKodeDto(KodeDto KodeDto) {
        this.KodeDto = KodeDto;
    }

    public void setKanLoses(Boolean kanLoses) {
        this.kanLoses = kanLoses;
    }

    public void setErAktivt(Boolean erAktivt) {
        this.erAktivt = erAktivt;
    }

    public KodeDto getDefinisjon() {
        return definisjon;
    }

    public KodeDto getVilkarType() {
        return vilkarType;
    }

    public KodeDto getStatus() {
        return status;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public Boolean getToTrinnsBehandling() {
        return toTrinnsBehandling;
    }

    public Set<KodeDto> getVurderPaNyttArsaker() {
        return vurderPaNyttArsaker;
    }

    public String getBesluttersBegrunnelse() {
        return besluttersBegrunnelse;
    }

    public Boolean getToTrinnsBehandlingGodkjent() {
        return toTrinnsBehandlingGodkjent;
    }

    public KodeDto getKodeDto() {
        return KodeDto;
    }

    public Boolean getKanLoses() {
        return kanLoses;
    }

    public Boolean getErAktivt() {
        return erAktivt;
    }
}
