package it.mulders.traqqr.gmaps;

import it.mulders.traqqr.gmaps.dto.GeocodeResponse;
import java.util.Optional;

public interface GoogleReverseGeocodingClient {
    Optional<GeocodeResponse> reverseGeocode(double lat, double lon);
}
