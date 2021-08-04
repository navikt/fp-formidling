package no.nav.foreldrepenger.melding.web.app.exceptions;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.WebApplicationException;
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
public class GeneralRestExceptionMapper implements ExceptionMapper<WebApplicationException> {

    // FIXME Humle, ikke bruk feilkoder her, håndter som valideringsfeil, eller legg
    // til egen samtidig-oppdatering-feil Feil-rammeverket
    static final String BEHANDLING_ENDRET_FEIL = "FPFORMIDLING-837578";
    // FIXME Humle, dette er Valideringsfeil, bruk valideringsfeil.
    static final String FRITEKST_TOM_FEIL = "FPFORMIDLING-290952";
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralRestExceptionMapper.class);

    @Override
    public Response toResponse(WebApplicationException exception) {
        Throwable cause = exception.getCause();

        if (cause instanceof Valideringsfeil) {
            return handleValideringsfeil((Valideringsfeil) cause);
        } else if (cause instanceof TomtResultatException) {
            return handleTomtResultatFeil((TomtResultatException) cause);
        }

        loggTilApplikasjonslogg(cause);
        String callId = MDCOperations.getCallId();

        if (cause instanceof VLException) {
            return handleVLException((VLException) cause, callId);
        }

        return handleGenerellFeil(cause, callId);
    }

    private Response handleTomtResultatFeil(TomtResultatException tomtResultatException) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(new FeilDto(tomtResultatException.getMessage(), FeilType.TOMT_RESULTAT_FEIL))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response handleValideringsfeil(Valideringsfeil valideringsfeil) {
        List<String> feltNavn = valideringsfeil.getFeltFeil().stream().map(FeltFeilDto::navn).collect(Collectors.toList());
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new FeilDto(
                        new FunksjonellException("FPFORMIDLING-328673", String.format("Det oppstod en valideringsfeil på felt %s. Vennligst kontroller at alle feltverdier er korrekte.", feltNavn), "Kontroller at alle feltverdier er korrekte").getMessage(),
                        valideringsfeil.getFeltFeil()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private Response handleVLException(VLException vlException, String callId) {

        if (vlException instanceof ManglerTilgangException) {
            return ikkeTilgang(vlException);
        } else if (FRITEKST_TOM_FEIL.equals(vlException.getKode())) {
            return handleValideringsfeil(new Valideringsfeil(Collections.singleton(new FeltFeilDto("fritekst",
                    vlException.getKode() + " " + vlException.getMessage()))));
        } else if (BEHANDLING_ENDRET_FEIL.equals(vlException.getKode())) {
            return behandlingEndret(vlException);
        } else {
            return serverError(callId, vlException);
        }
    }

    private Response serverError(String callId, VLException vlException) {
        String feilmelding = getVLExceptionFeilmelding(callId, vlException);
        FeilType feilType = FeilType.GENERELL_FEIL;
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

    private Response behandlingEndret(VLException vlException) {
        FeilType feilType = FeilType.BEHANDLING_ENDRET_FEIL;
        return Response.status(Response.Status.CONFLICT)
                .entity(new FeilDto(vlException.getMessage(), feilType))
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

    private Response handleGenerellFeil(Throwable cause, String callId) {
        String generellFeilmelding = "Det oppstod en serverfeil: " + cause.getMessage() + ". Meld til support med referanse-id: " + callId; //$NON-NLS-1$ //$NON-NLS-2$
        return Response.serverError()
                .entity(new FeilDto(generellFeilmelding, FeilType.GENERELL_FEIL))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private String avsluttMedPunktum(String tekst) {
        return tekst + (tekst.endsWith(".") ? " " : ". "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private void loggTilApplikasjonslogg(Throwable cause) {
        String message = cause.getMessage() != null ? LoggerUtils.removeLineBreaks(cause.getMessage()) : "";
        if (cause instanceof VLException) {
            LOGGER.warn("Fikk uventet feil:" + message, cause); // NOSONAR //$NON-NLS-1$
        } else {
            LOGGER.error("Fikk uventet feil:" + message, cause); // NOSONAR //$NON-NLS-1$
        }
        // key for å tracke prosess -- nullstill denne
        MDC.remove("prosess"); //$NON-NLS-1$
    }

}
