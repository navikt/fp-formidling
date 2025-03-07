package no.nav.foreldrepenger.fpformidling.tjenester;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ForhåndsvisDokumentHTMLDto(@Valid @NotNull String saksnummer,
                                         @Valid @NotNull UUID behandlingUuid,
                                         @Valid @NotNull String html) {
}