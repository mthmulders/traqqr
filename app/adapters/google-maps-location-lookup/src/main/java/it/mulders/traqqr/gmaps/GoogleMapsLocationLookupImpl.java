package it.mulders.traqqr.gmaps;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup;
import it.mulders.traqqr.gmaps.dto.GeocodeResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GoogleMapsLocationLookupImpl implements LocationLookup {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final GoogleReverseGeocodingClient client;

    @Inject
    public GoogleMapsLocationLookupImpl(GoogleReverseGeocodingClient client) {
        this.client = client;
    }

    @Override
    public LocationLookupResult lookup(Measurement.Location location) {
        logger.info("Looking up location for latitude={}, longitude={}", location.lat(), location.lon());

        try {
            return client.reverseGeocode(location.lat(), location.lon())
                    .flatMap(response -> this.extractLocationDescription(response, location))
                    .map(description -> new Measurement.Location(location.lat(), location.lon(), description))
                    .map(LocationLookupResult.Success::new)
                    .map(LocationLookupResult.class::cast)
                    .orElse(new LocationLookupResult.NotFound());
        } catch (Exception e) {
            logger.error("Failed to lookup location", e);
            return new LocationLookupResult.Failure(e);
        }
    }

    private Optional<String> extractLocationDescription(GeocodeResponse response, Measurement.Location location) {
        return switch (response.status()) {
            case OK -> {
                // Find the most suitable result, there could be multiple.
                var results = response.results();
                var streetAddresses = results.stream()
                        .filter(result -> result.types().contains("street_address"))
                        .toList();
                if (streetAddresses.isEmpty()) {
                    logger.info(
                            "No street address found in Google Maps for the given location; latitude={}, longitude={}",
                            location.lat(),
                            location.lon());
                    yield Optional.empty();
                } else if (streetAddresses.size() > 1) {
                    logger.info(
                            "Multiple street addresses found in Google Maps for the given location; latitude={}, longitude={}, count={}",
                            location.lat(),
                            location.lon(),
                            streetAddresses.size());
                }
                var description = streetAddresses.getFirst().formattedAddress();
                logger.debug(
                        "Found location description from Google Maps; latitude={}, longitude={}, description={}",
                        location.lat(),
                        location.lon(),
                        description);
                yield Optional.of(description);
            }
            case ZERO_RESULTS -> {
                logger.info(
                        "No results found in Google Maps for the given location; latitude={}, longitude={}",
                        location.lat(),
                        location.lon());
                yield Optional.empty();
            }
            case UNKNOWN_ERROR -> {
                logger.info(
                        "Unknown error looking up location with Google Maps API; latitude={}, longitude={}",
                        location.lat(),
                        location.lon());
                throw new GoogleMapsLocationLookupException("An unknown error occurred.");
            }
            case REQUEST_DENIED -> {
                logger.warn(
                        "Request to Google Maps API was denied; latitude={}, longitude={}; message={}",
                        location.lat(),
                        location.lon(),
                        response.errorMessage());
                throw new GoogleMapsLocationLookupException("Request to Google Maps API was denied");
            }
            case INVALID_REQUEST -> {
                logger.warn(
                        "Invalid request sent to Google Maps API; latitude={}, longitude={}",
                        location.lat(),
                        location.lon());
                throw new GoogleMapsLocationLookupException("The request to Google Maps API was invalid");
            }
            case OVER_QUERY_LIMIT -> {
                logger.warn(
                        "Query limit for Google Maps API exceeded; latitude={}, longitude={}",
                        location.lat(),
                        location.lon());
                throw new GoogleMapsLocationLookupException("You have exceeded your daily request quota for this API.");
            }
        };
    }
}
