package it.mulders.traqqr.domain.measurements.spi;

import it.mulders.traqqr.domain.measurements.Measurement;

/**
 * Service Provider Interface (SPI) for looking up or enriching a {@link Measurement.Location}.
 * Implementations may use reverse geocoding or other mechanisms to resolve a human-readable description
 * for a geographical coordinate.
 */
public interface LocationLookup {
    /**
     * Perform a lookup for the given location.
     *
     * @param location the input location (latitude/longitude and optionally description)
     * @return a {@link LocationLookupResult} indicating success with a possibly enriched location or failure with a cause
     */
    LocationLookupResult lookup(Measurement.Location location);

    /**
     * Sealed result type for a location lookup operation.
     * <p>
     * Implementations are limited to {@link Success} and {@link Failure}.
     */
    sealed interface LocationLookupResult permits LocationLookupResult.Success, LocationLookupResult.NotFound, LocationLookupResult.Failure {
        /** Successful lookup containing the resolved location. */
        record Success(Measurement.Location location) implements LocationLookupResult {}

        /** Successful lookup but no location could be found. */
        record NotFound() implements LocationLookupResult {}

        /** Failed lookup with the underlying cause. */
        record Failure(Throwable cause) implements LocationLookupResult {}
    }
}
