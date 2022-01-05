package no.nav.foreldrepenger.fpsak.dto.fagsak;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class FagsakDto {
    private String saksnummer;
    private KodeDto relasjonsRolleType;
    private String aktoerId;

    public FagsakDto() {
        // Injiseres i test
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public KodeDto getRelasjonsRolleType() {
        return relasjonsRolleType;
    }

    public String getAktoerId() {
        return aktoerId;
    }
}
