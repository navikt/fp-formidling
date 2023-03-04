package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto;

public record ErrorResponse(Integer status, String error, String message, String path) {
}
