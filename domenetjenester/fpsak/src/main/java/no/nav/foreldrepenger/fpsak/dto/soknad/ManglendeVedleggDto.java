package no.nav.foreldrepenger.fpsak.dto.soknad;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class ManglendeVedleggDto {
    private KodeDto dokumentType;
    private VirksomhetDto arbeidsgiver;
    private boolean brukerHarSagtAtIkkeKommer = false;

    public KodeDto getDokumentType() {
        return dokumentType;
    }

    public void setDokumentType(KodeDto dokumentType) {
        this.dokumentType = dokumentType;
    }

    public VirksomhetDto getArbeidsgiver() {
        return arbeidsgiver;
    }

    public void setArbeidsgiver(VirksomhetDto arbeidsgiver) {
        this.arbeidsgiver = arbeidsgiver;
    }

    public boolean getBrukerHarSagtAtIkkeKommer() {
        return brukerHarSagtAtIkkeKommer;
    }

    public void setBrukerHarSagtAtIkkeKommer(boolean brukerHarSagtAtIkkeKommer) {
        this.brukerHarSagtAtIkkeKommer = brukerHarSagtAtIkkeKommer;
    }
}
