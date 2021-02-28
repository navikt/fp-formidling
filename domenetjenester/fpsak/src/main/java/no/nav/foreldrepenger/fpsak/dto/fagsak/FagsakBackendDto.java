package no.nav.foreldrepenger.fpsak.dto.fagsak;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class FagsakBackendDto {
    //private Long saksnummer;
    private String saksnummerString;
    private KodeDto relasjonsRolleType;
    private String aktoerId;

    public FagsakBackendDto() {
        // Injiseres i test
    }

    public String getSaksnummerString() {
        return saksnummerString;
    }

    public KodeDto getRelasjonsRolleType() {
        return relasjonsRolleType;
    }

    public String getAktoerId() {
        return aktoerId;
    }
}
