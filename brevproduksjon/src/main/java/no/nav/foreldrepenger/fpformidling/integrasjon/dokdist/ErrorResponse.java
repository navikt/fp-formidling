package no.nav.foreldrepenger.fpformidling.integrasjon.dokdist;

record ErrorResponse(Integer status, String error, String message, String path) {}
