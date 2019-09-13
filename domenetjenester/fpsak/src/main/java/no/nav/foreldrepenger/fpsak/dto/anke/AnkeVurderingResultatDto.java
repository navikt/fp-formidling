package no.nav.foreldrepenger.fpsak.dto.anke;

public class AnkeVurderingResultatDto {

    private String ankeVurdering;
    private String begrunnelse;
    private String fritekstTilBrev;
    private String ankeOmgjoerArsak;
    private String ankeOmgjoerArsakNavn;
    private String ankeVurderingOmgjoer;
    private boolean godkjentAvMedunderskriver;
    private boolean erAnkerIkkePart;
    private boolean erFristIkkeOverholdt;
    private boolean erIkkeKonkret;
    private boolean erIkkeSignert;
    private boolean erSubsidiartRealitetsbehandles;
    private Long paAnketBehandlingId;

    public AnkeVurderingResultatDto() {
    }

    public String getAnkeVurdering() {
        return ankeVurdering;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public String getAnkeOmgjoerArsak() {
        return ankeOmgjoerArsak;
    }

    public String getAnkeOmgjoerArsakNavn() {
        return ankeOmgjoerArsakNavn;
    }

    public String getAnkeVurderingOmgjoer() {
        return ankeVurderingOmgjoer;
    }

    public boolean isGodkjentAvMedunderskriver() {
        return godkjentAvMedunderskriver;
    }

    public boolean isErAnkerIkkePart() {
        return erAnkerIkkePart;
    }

    public boolean isErFristIkkeOverholdt() {
        return erFristIkkeOverholdt;
    }

    public boolean isErIkkeKonkret() {
        return erIkkeKonkret;
    }

    public boolean isErIkkeSignert() {
        return erIkkeSignert;
    }

    public boolean isErSubsidiartRealitetsbehandles() {
        return erSubsidiartRealitetsbehandles;
    }

    public Long getPaAnketBehandlingId() {
        return paAnketBehandlingId;
    }

    public void setAnkeVurderingOmgjoer(String ankeVurderingOmgjoer) {
        this.ankeVurderingOmgjoer = ankeVurderingOmgjoer;
    }

    void setAnkeVurdering(String ankeVurdering) {
        this.ankeVurdering = ankeVurdering;
    }

    void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public void setFritekstTilBrev(String fritekstTilBrev) {
        this.fritekstTilBrev = fritekstTilBrev;
    }

    void setAnkeOmgjoerArsak(String ankeOmgjoerArsak) {
        this.ankeOmgjoerArsak = ankeOmgjoerArsak;
    }

    void setAnkeOmgjoerArsakNavn(String ankeOmgjoerArsakNavn) {
        this.ankeOmgjoerArsakNavn = ankeOmgjoerArsakNavn;
    }

    public void setGodkjentAvMedunderskriver(boolean godkjentAvMedunderskriver) {
        this.godkjentAvMedunderskriver = godkjentAvMedunderskriver;
    }

    public void setErAnkerIkkePart(boolean erAnkerIkkePart) {
        this.erAnkerIkkePart = erAnkerIkkePart;
    }

    public void setErFristIkkeOverholdt(boolean erFristIkkeOverholdt) {
        this.erFristIkkeOverholdt = erFristIkkeOverholdt;
    }

    public void setErIkkeKonkret(boolean erIkkeKonkret) {
        this.erIkkeKonkret = erIkkeKonkret;
    }

    public void setErIkkeSignert(boolean erIkkeSignert) {
        this.erIkkeSignert = erIkkeSignert;
    }

    public void setErSubsidiartRealitetsbehandles(boolean erSubsidiartRealitetsbehandles) {
        this.erSubsidiartRealitetsbehandles = erSubsidiartRealitetsbehandles;
    }

    public void setPaAnketBehandlingId(Long paAnketBehandlingId) {
        this.paAnketBehandlingId = paAnketBehandlingId;
    }
}
