package it.mulders.traqqr.gmaps;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GoogleMapsLocationLookupImpl implements LocationLookup {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public LocationLookupResult lookup(Measurement.Location location) {
        // Placeholder implementation; real implementation would call Google Maps API
        logger.info("Looking up location for latitude={}, longitude={}", location.lat(), location.lon());

        try {
            // Simulate a successful lookup with a dummy description
            var description = "Dummy Location Description";
            Measurement.Location enrichedLocation = new Measurement.Location(location.lat(), location.lon(), description);
            return new LocationLookupResult.Success(enrichedLocation);
        } catch (Exception e) {
            logger.error("Failed to lookup location", e);
            return new LocationLookupResult.Failure(e);
        }
    }
}
