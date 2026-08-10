package it.mulders.traqqr.web.krazo;

import it.mulders.traqqr.domain.measurements.Measurement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("gpsCoordinateFormatter")
public class GpsCoordinateFormatter {
    public String format(final Measurement.Location location) {
        if (location == null) return "";
        return "%s, %s".formatted(location.lat(), location.lon());
    }
}
