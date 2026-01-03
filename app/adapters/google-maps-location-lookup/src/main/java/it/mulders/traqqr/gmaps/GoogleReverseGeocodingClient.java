package it.mulders.traqqr.gmaps;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.gmaps.dto.GeocodeResponse;
import java.util.Optional;

public interface GoogleReverseGeocodingClient {
    /**
     * Call the Google Reverse Geocoding API for the given latitude/longitude.
     * Returns an Optional containing the parsed GeocodeResponse on success, or empty on failure.
     * @param location The location (latitude and longitude) to reverse geocode.
     * @return An optional with a {@link GeocodeResponse} if the call was successful, or empty otherwise.
     * @throws GoogleMapsLocationLookupException if an error occurs during the lookup.
     */
    Optional<GeocodeResponse> reverseGeocode(Measurement.Location location) throws GoogleMapsLocationLookupException;
}
