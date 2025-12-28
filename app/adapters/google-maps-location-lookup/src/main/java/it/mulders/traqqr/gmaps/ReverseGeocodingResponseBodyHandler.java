package it.mulders.traqqr.gmaps;

import it.mulders.traqqr.gmaps.dto.GeocodeResponse;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReverseGeocodingResponseBodyHandler implements HttpResponse.BodyHandler<Optional<GeocodeResponse>> {
    private static final Logger logger = LoggerFactory.getLogger(ReverseGeocodingResponseBodyHandler.class);

    private final Jsonb jsonb = JsonbBuilder.create();

    @Override
    public HttpResponse.BodySubscriber<Optional<GeocodeResponse>> apply(final ResponseInfo responseInfo) {
        var payload = HttpResponse.BodySubscribers.ofInputStream();
        return HttpResponse.BodySubscribers.mapping(payload, this::toGeocodeResponse);
    }

    Optional<GeocodeResponse> toGeocodeResponse(final InputStream inputStream) {
        try (final InputStream input = inputStream) {
            var geocodeResponse = jsonb.fromJson(input, GeocodeResponse.class);
            return Optional.ofNullable(geocodeResponse);
        } catch (IOException e) {
            logger.error("Failed to parse Google Reverse Geocoding API response", e);
            return Optional.empty();
        }
    }
}
