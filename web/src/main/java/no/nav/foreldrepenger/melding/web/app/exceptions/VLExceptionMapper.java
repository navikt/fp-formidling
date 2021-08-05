package no.nav.foreldrepenger.melding.web.app.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import no.nav.vedtak.exception.FunksjonellException;
import no.nav.vedtak.exception.ManglerTilgangException;
import no.nav.vedtak.exception.VLException;
import no.nav.vedtak.felles.jpa.TomtResultatException;
import no.nav.vedtak.log.mdc.MDCOperations;
import no.nav.vedtak.log.util.LoggerUtils;

@Provider
public class VLExceptionMapper implements ExceptionMapper<VLException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VLExceptionMapper.class);

    @Override
    public Response toResponse(VLException exception) {
        // Ikke logg disse
        if (exception instanceof ManglerTilgangException) {
            return ikkeTilgang(exception);
        } else if (exception instanceof TomtResultatException) {
            return handleTomtResultatFeil((TomtResultatException) exception);
        }

        loggTilApplikasjonslogg(exception);
        String callId = MDCOperations.getCallId();
        return serverError(callId, exception);
    }

    private Response handleTomtResultatFeil(TomtResultatException tomtResultatException) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(new FeilDto(tomtResultatException.getMessage(), FeilType.TOMT_RESULTAT_FEIL))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response serverError(String callId, VLException vlException) {
        var feilmelding = getVLExceptionFeilmelding(callId, vlException);
        var feilType = FeilType.GENERELL_FEIL;
        return Response.serverError()
                .entity(new FeilDto(feilmelding, feilType))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response ikkeTilgang(VLException exception) {
        FeilType feilType = FeilType.MANGLER_TILGANG_FEIL;
        return Response.status(Response.Status.FORBIDDEN)
                .entity(new FeilDto(exception.getMessage(), feilType))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String getVLExceptionFeilmelding(String callId, VLException vlException) {
        String feilbeskrivelse = vlException.getKode() + ": " + vlException.getMessage();
        if (vlException instanceof FunksjonellException) {
            String løsningsforslag = ((FunksjonellException) vlException).getLøsningsforslag();
            return "Det oppstod en feil: " //$NON-NLS-1$
                    + avsluttMedPunktum(feilbeskrivelse)
                    + avsluttMedPunktum(løsningsforslag)
                    + ". Referanse-id: " + callId; //$NON-NLS-1$
        } else {
            return "Det oppstod en serverfeil: " //$NON-NLS-1$
                    + avsluttMedPunktum(feilbeskrivelse)
                    + ". Meld til support med referanse-id: " + callId;
        }
    }

    private String avsluttMedPunktum(String tekst) {
        return tekst + (tekst.endsWith(".") ? " " : ". "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private void loggTilApplikasjonslogg(VLException exception) {
        var rawmessage = exception.getFeilmelding();
        String message = rawmessage != null ? LoggerUtils.removeLineBreaks(rawmessage) : "";
        LOGGER.warn("Fikk uventet feil:" + message, exception); // NOSONAR //$NON-NLS-1$
        // key for å tracke prosess -- nullstill denne
        MDC.remove("prosess"); //$NON-NLS-1$
    }

}
