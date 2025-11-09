package it.mulders.traqqr.gmaps;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GoogleReverseGeocodingClientProviderTest implements WithAssertions {
    @Test
    void should_return_google_reverse_geocoding_client_provider() {
        // Arrange
        var env = Map.of("GOOGLE_REVERSE_GEOCODING_API_KEY", "test-api-key");

        // Act
        var client = new GoogleReverseGeocodingClientProvider(env).createGoogleReverseGeocodingClient();

        // Assert
        assertThat(client).isNotNull();
    }
}