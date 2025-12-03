package it.mulders.traqqr.gmaps;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import java.util.Map;

@ApplicationScoped
public class GoogleReverseGeocodingClientProvider {
    private final Map<String, String> environment;

    public GoogleReverseGeocodingClientProvider() {
        this(System.getenv());
    }

    public GoogleReverseGeocodingClientProvider(Map<String, String> environment) {
        this.environment = environment;
    }

    @Produces
    public GoogleReverseGeocodingClient createGoogleReverseGeocodingClient() {
        var apiKey = this.environment.get("GOOGLE_REVERSE_GEOCODING_API_KEY");
        return new GoogleReverseGeocodingClientImpl(apiKey);
    }
}
