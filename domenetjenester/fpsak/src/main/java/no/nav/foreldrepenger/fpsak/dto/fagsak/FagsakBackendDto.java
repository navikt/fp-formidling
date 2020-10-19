package no.nav.foreldrepenger.fpsak.dto.fagsak;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class FagsakBackendDto {
    private Long saksnummer;
    private KodeDto sakstype;
    private KodeDto relasjonsRolleType;
    private KodeDto status;
    private Integer dekningsgrad;
    private String aktoerId;

    public FagsakBackendDto() {
        // Injiseres i test
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public KodeDto getSakstype() {
        return sakstype;
    }

    public KodeDto getStatus() {
        return status;
    }

    public KodeDto getRelasjonsRolleType() {
        return relasjonsRolleType;
    }

    public Integer getDekningsgrad() {
        return dekningsgrad;
    }

    public String getAktoerId() {
        return aktoerId;
    }
}
