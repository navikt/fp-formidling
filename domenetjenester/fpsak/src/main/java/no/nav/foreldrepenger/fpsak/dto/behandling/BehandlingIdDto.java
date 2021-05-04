package no.nav.foreldrepenger.fpsak.dto.behandling;

import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Referanse til en behandling.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BehandlingIdDto {

    @Min(0)
    @Max(Long.MAX_VALUE)
    private Long saksnummer;

    @NotNull
    @Min(0)
    @Max(Long.MAX_VALUE)
    private Long behandlingId;

    /**
     * Behandling UUID (nytt alternativ til intern behandlingId. Bør brukes av eksterne systemer).
     */
    @Valid
    private UUID behandlingUuid;

    public BehandlingIdDto() {
        behandlingId = null; // NOSONAR
    }

    public BehandlingIdDto(UUID behandlingUuid) {
        this.behandlingUuid = behandlingUuid;
    }

    public BehandlingIdDto(String behandlingId) {
        this.behandlingId = Long.valueOf(behandlingId);
    }

    public BehandlingIdDto(Long saksnummer, Long behandlingId, UUID behandlingUuid) {
        this.saksnummer = saksnummer;
        this.behandlingId = behandlingId;
        this.behandlingUuid = behandlingUuid;
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

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '<' +
                (saksnummer == null ? "" : "saksnummer=" + saksnummer + ", ") +
                "behandlingId=" + behandlingId +
                '>';
    }
}
