package no.nav.foreldrepenger.melding.web.app.exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.jboss.resteasy.api.validation.ResteasyViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstraintViolationMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger LOG = LoggerFactory.getLogger(ConstraintViolationMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        var constraintViolations = exception.getConstraintViolations();

        if (constraintViolations.isEmpty() && exception instanceof ResteasyViolationException) {
            return håndterFeilKonfigurering((ResteasyViolationException) exception);
        }
        log(exception);
        return lagResponse(exception);
    }

    private void log(ConstraintViolationException exception) {
        LOG.warn("Det oppstod en valideringsfeil: {}", constraints(exception));
    }

    private static Response lagResponse(ConstraintViolationException exception) {
        Collection<FeltFeilDto> feilene = new ArrayList<>();
        for (var constraintViolation : exception.getConstraintViolations()) {
            var feltNavn = getFeltNavn(constraintViolation.getPropertyPath());
            feilene.add(new FeltFeilDto(feltNavn, constraintViolation.getMessage()));
        }
        var feltNavn = feilene.stream().map(FeltFeilDto::getNavn).collect(Collectors.toList());
        var feilmelding = String.format(
                "Det oppstod en valideringsfeil på felt %s. " + "Vennligst kontroller at alle feltverdier er korrekte.",
                feltNavn);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new FeilDto(feilmelding, feilene))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private static Response håndterFeilKonfigurering(ResteasyViolationException exception) {
        var message = exception.getException().getMessage();
        LOG.error(message);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new FeilDto(FeilType.GENERELL_FEIL, "Det oppstod en serverfeil: Validering er feilkonfigurert."))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private static Set<String> constraints(ConstraintViolationException exception) {
        return exception.getConstraintViolations()
                .stream()
                .map(cv -> cv.getRootBeanClass().getSimpleName() + "." + cv.getLeafBean().getClass().getSimpleName()
                        + "." + fieldName(cv) + " - " + cv.getMessage())
                .collect(Collectors.toSet());
    }

    private static String fieldName(ConstraintViolation<?> cv) {
        String field = null;
        for (Path.Node node : cv.getPropertyPath()) {
            field = node.getName();
        }
        return field;
    }

    private static String getFeltNavn(Path propertyPath) {
        return propertyPath instanceof PathImpl ? ((PathImpl) propertyPath).getLeafNode().toString() : null;
    }
}
