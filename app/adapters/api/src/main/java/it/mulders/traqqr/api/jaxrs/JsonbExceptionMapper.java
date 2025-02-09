package it.mulders.traqqr.api.jaxrs;

import it.mulders.traqqr.api.jaxrs.dto.InputValidationFailedDto;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.json.bind.JsonbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeParseException;

@Provider
public class JsonbExceptionMapper implements ExceptionMapper<JsonbException> {
    private static final Logger log = LoggerFactory.getLogger(JsonbExceptionMapper.class);

    @Override
    public Response toResponse(final JsonbException exception) {
        log.error("Could not convert to/from JSON", exception);
        var cause = findRootCause(exception);

        var message = switch (cause.getClass().getSimpleName()) {
            case "DateTimeParseException":
                var dtpe = (DateTimeParseException) cause;
                yield "Invalid date/time format '%s' at position %d".formatted(dtpe.getParsedString(), dtpe.getErrorIndex());
            default:
                yield "Unknown error processing JSON";
        };

        var result = new InputValidationFailedDto(message);

        return Response.status(400)
                .entity(result)
                .type("application/json")
                .build();
    }

    private Throwable findRootCause(final Throwable throwable) {
        var root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root;
    }
}
