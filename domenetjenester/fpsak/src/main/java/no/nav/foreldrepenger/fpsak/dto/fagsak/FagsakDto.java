package no.nav.foreldrepenger.fpsak.dto.fagsak;

import java.time.LocalDate;
import java.time.LocalDateTime;

import no.nav.foreldrepenger.fpsak.dto.kodeverk.KodeDto;

public class FagsakDto {
    private Long saksnummer;
    private KodeDto sakstype;
    private KodeDto relasjonsRolleType;
    private KodeDto status;
    private LocalDate barnFodt;
    private PersonDto person;
    private LocalDateTime opprettet;
    private LocalDateTime endret;
    private Integer antallBarn;
    private Boolean kanRevurderingOpprettes;
    private Boolean skalBehandlesAvInfotrygd;

    public FagsakDto() {
        // Injiseres i test
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public KodeDto getSakstype() {
        return sakstype;
    }

    public KodeDto getRelasjonsRolleType() {
        return relasjonsRolleType;
    }

    public KodeDto getStatus() {
        return status;
    }

    public PersonDto getPerson() {
        return person;
    }

    public LocalDate getBarnFodt() {
        return barnFodt;
    }

    public Integer getAntallBarn() {
        return antallBarn;
    }

    public LocalDateTime getOpprettet() {
        return opprettet;
    }

    public LocalDateTime getEndret() {
        return endret;
    }

    public Boolean getKanRevurderingOpprettes() {
        return kanRevurderingOpprettes;
    }

    public Boolean getSkalBehandlesAvInfotrygd() {
        return skalBehandlesAvInfotrygd;
    }

}
