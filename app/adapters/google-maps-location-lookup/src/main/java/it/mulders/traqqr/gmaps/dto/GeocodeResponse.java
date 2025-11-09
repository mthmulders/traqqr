package it.mulders.traqqr.gmaps.dto;

import jakarta.json.bind.annotation.JsonbProperty;
import java.util.List;

/**
 * DTOs for the Google Geocoding API response (reverse geocoding).
 * Matches the fields described in <a href="https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding">the documentation</a>.
 */
public record GeocodeResponse(
        @JsonbProperty("results") List<Result> results,
        @JsonbProperty("status") ResponseStatus status,
        @JsonbProperty("error_message") String errorMessage) {

    public enum ResponseStatus {
        OK,
        ZERO_RESULTS,
        OVER_QUERY_LIMIT,
        REQUEST_DENIED,
        INVALID_REQUEST,
        UNKNOWN_ERROR
    }
}
