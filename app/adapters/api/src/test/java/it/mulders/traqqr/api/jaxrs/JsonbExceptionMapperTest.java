package it.mulders.traqqr.api.jaxrs;

import it.mulders.traqqr.api.jaxrs.dto.InputValidationFailedDto;
import jakarta.json.bind.JsonbException;
import java.time.format.DateTimeParseException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JsonbExceptionMapperTest implements WithAssertions {
    private final JsonbExceptionMapper mapper = new JsonbExceptionMapper();

    @Test
    void should_detect_invalid_datetime_format() {
        // Arrange
        var input = "2025-01-01T10:31:04+0100";
        var cause = new DateTimeParseException("message", input, 0);
        var exception = new JsonbException("error parsing json", new JsonbException("error parsing json", cause));

        // Act
        var result = mapper.toResponse(exception);

        // Assert
        assertThat(result.getEntity())
                .isInstanceOf(InputValidationFailedDto.class)
                .asInstanceOf(InstanceOfAssertFactories.type(InputValidationFailedDto.class))
                .extracting(InputValidationFailedDto::message)
                .asInstanceOf(STRING)
                .contains(input)
                .doesNotContain(cause.getMessage())
                .doesNotContain(cause.getClass().getSimpleName());
    }

    @Test
    void should_provide_generic_message_otherwise() {
        // Arrange
        var cause = new Exception("I don't know what went wrong");
        var exception = new JsonbException("generic error", cause);

        // Act
        var result = mapper.toResponse(exception);

        // Assert
        assertThat(result.getEntity())
                .isInstanceOf(InputValidationFailedDto.class)
                .asInstanceOf(InstanceOfAssertFactories.type(InputValidationFailedDto.class))
                .extracting(InputValidationFailedDto::message)
                .asInstanceOf(STRING)
                .contains("Unknown error processing JSON")
                .doesNotContain(cause.getMessage())
                .doesNotContain(cause.getClass().getSimpleName());
    }
}
