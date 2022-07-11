package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist.dto;

public record ErrorResponse(Integer status, String error, String message, String path) {}
