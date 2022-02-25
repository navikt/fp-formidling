package no.nav.foreldrepenger.fpsak.dto.fagsak;

import no.nav.foreldrepenger.fpformidling.personopplysning.RelasjonsRolleType;

public class FagsakDto {
    private String saksnummer;
    private RelasjonsRolleType relasjonsRolleType;
    private String aktoerId;

    public FagsakDto() {
        // Injiseres i test
    }

    public String getSaksnummer() {
        return saksnummer;
    }

    public RelasjonsRolleType getRelasjonsRolleType() {
        return relasjonsRolleType;
    }

    public String getAktoerId() {
        return aktoerId;
    }
}
