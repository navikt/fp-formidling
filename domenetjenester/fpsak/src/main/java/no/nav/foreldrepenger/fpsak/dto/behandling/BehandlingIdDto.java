package no.nav.foreldrepenger.fpsak.dto.behandling;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

/**
 * Referanse til en behandling.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BehandlingIdDto implements AbacDto {

    @Min(0)
    @Max(Long.MAX_VALUE)
    private Long saksnummer;

    @NotNull
    @Min(0)
    @Max(Long.MAX_VALUE)
    private Long behandlingId;

    public BehandlingIdDto() {
        behandlingId = null; // NOSONAR
    }

    public BehandlingIdDto(String behandlingId) {
        this.behandlingId = Long.valueOf(behandlingId);
    }

    public BehandlingIdDto(Long saksnummer, Long behandlingId) {
        this.saksnummer = saksnummer;
        this.behandlingId = behandlingId;
    }

    public BehandlingIdDto(Long behandlingId) {
        this.behandlingId = behandlingId;
    }

    public Long getBehandlingId() {
        return behandlingId;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        AbacDataAttributter abac = AbacDataAttributter.opprett();
        return abac.leggTilBehandlingsId(getBehandlingId());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '<' +
                (saksnummer == null ? "" : "saksnummer=" + saksnummer + ", ") +
                "behandlingId=" + behandlingId +
                '>';
    }
}
