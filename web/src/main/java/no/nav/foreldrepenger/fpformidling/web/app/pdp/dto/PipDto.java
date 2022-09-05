package no.nav.foreldrepenger.fpformidling.web.app.pdp.dto;

import java.util.Set;

import javax.validation.Valid;

import no.nav.foreldrepenger.fpformidling.typer.AktørId;

public record PipDto(@Valid Set<AktørId> aktørIder, String fagsakStatus, String behandlingStatus) {
}
