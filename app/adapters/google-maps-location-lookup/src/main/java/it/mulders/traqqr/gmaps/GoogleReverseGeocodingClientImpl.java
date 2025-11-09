package it.mulders.traqqr.gmaps;

import it.mulders.traqqr.gmaps.dto.GeocodeResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple client for Google Reverse Geocoding API.
 * <p>
 * This class builds a request to the endpoint described in <a href="https://developers.google.com/maps/documentation/geocoding/requests-reverse-geocoding">the documentation</a>.
 * <p>
 * It maps the JSON response into DTOs under the `it.mulders.traqqr.gmaps.dto` package.
 */
public class GoogleReverseGeocodingClientImpl implements GoogleReverseGeocodingClient {
    private static final Logger logger = LoggerFactory.getLogger(GoogleReverseGeocodingClientImpl.class);
    private static final String DEFAULT_BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    private final HttpClient httpClient;
    private final String apiKey;
    private final String baseUrl;

    public GoogleReverseGeocodingClientImpl(String apiKey) {
        this(apiKey, HttpClient.newHttpClient(), DEFAULT_BASE_URL);
    }

    /**
     * Create client with a custom base URL (useful for testing with WireMock).
     */
    public GoogleReverseGeocodingClientImpl(String apiKey, HttpClient httpClient, String baseUrl) {
        this.apiKey = apiKey;
        this.httpClient = httpClient;
        this.baseUrl = baseUrl;
    }

    /**
     * Call the Google Reverse Geocoding API for the given latitude/longitude.
     * Returns an Optional containing the parsed GeocodeResponse on success, or empty on failure.
     */
    public Optional<GeocodeResponse> reverseGeocode(double lat, double lon) {
        try {
            var coords = lat + "," + lon;
            var url = String.format(
                    "%s?latlng=%s&key=%s",
                    baseUrl,
                    URLEncoder.encode(coords, StandardCharsets.UTF_8),
                    URLEncoder.encode(apiKey, StandardCharsets.UTF_8));

            var request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .header("Accept", "application/json")
                    .build();

            logger.debug("Calling Google Reverse Geocoding: url={}", url);

            var response = httpClient.send(request, new ReverseGeocodingResponseBodyHandler());
            var status = response.statusCode();
            if (status / 100 != 2) {
                logger.error("Google Geocoding API returned non-2xx: {} - body: {}", status, response.body());
                return Optional.empty();
            }

            return response.body();
        } catch (IOException ioe) {
            logger.error("Failed to call Google Reverse Geocoding API", ioe);
            return Optional.empty();
        } catch (InterruptedException ie) {
            // No need to clean up resources, just restore the interrupt flag.
            Thread.currentThread().interrupt();
            return Optional.empty();
        }
    }
}
