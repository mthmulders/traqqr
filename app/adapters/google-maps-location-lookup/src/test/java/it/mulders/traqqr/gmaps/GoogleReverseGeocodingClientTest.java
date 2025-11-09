package it.mulders.traqqr.gmaps;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;

import it.mulders.traqqr.gmaps.dto.GeocodeResponse.ResponseStatus;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GoogleReverseGeocodingClientTest implements WithAssertions {
    private final WireMockServer wireMockServer =
            new WireMockServer(WireMockConfiguration.options().dynamicPort());
    private GoogleReverseGeocodingClient client;

    @BeforeEach
    void setUp() {
        wireMockServer.start();
        var baseUrl = String.format("http://localhost:%d/maps/api/geocode/json", wireMockServer.port());
        client = new GoogleReverseGeocodingClient("test-key", HttpClient.newHttpClient(), baseUrl);
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    private String loadResourceAsString(String resourcePath) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            assertThat(is)
                    .describedAs("Test resource not found: " + resourcePath)
                    .isNotNull();
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @Test
    void reverseGeocode_should_invoke_Google_endpoint_with_API_key_in_query_string() throws Exception {
        // Arrange
        var payload = loadResourceAsString("/google-reverse-geocoding/sample_1600_amphitheatre.json");
        stubFor(get(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("37.4224764,-122.0842499"))
                .withQueryParam("key", equalTo("test-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(payload)));

        // Act
        client.reverseGeocode(37.4224764, -122.0842499);

        // Assert
        verify(getRequestedFor(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("37.4224764,-122.0842499"))
                .withQueryParam("key", equalTo("test-key")));
    }

    @Test
    void reverseGeocode_should_parse_successful_response() throws Exception {
        // Arrange
        var payload = loadResourceAsString("/google-reverse-geocoding/sample_1600_amphitheatre.json");
        stubFor(get(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("37.4224764,-122.0842499"))
                .withQueryParam("key", equalTo("test-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(payload)));

        // Act
        var result = client.reverseGeocode(37.4224764, -122.0842499);

        // Assert
        assertThat(result).isPresent().hasValueSatisfying(response -> {
            assertThat(response.status()).isEqualTo(ResponseStatus.OK);
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).isNotEmpty();
            assertThat(response.results()).anySatisfy(r -> assertThat(r.formattedAddress())
                    .isEqualTo("1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA"));
        });
    }

    @Test
    void reverseGeocode_should_parse_zero_results_response() throws Exception {
        // Arrange
        var payload = loadResourceAsString("/google-reverse-geocoding/sample_zero_results.json");
        stubFor(get(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("0.0,0.0"))
                .withQueryParam("key", equalTo("test-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(payload)));
        // Act
        var result = client.reverseGeocode(0.0, 0.0);

        // Assert
        assertThat(result).isPresent().hasValueSatisfying(response -> {
            assertThat(response.status()).isEqualTo(ResponseStatus.ZERO_RESULTS);
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).isEmpty();
        });
    }

    @Test
    void reverseGeocode_should_parse_partial_match_response() throws Exception {
        // Arrange
        var payload = loadResourceAsString("/google-reverse-geocoding/sample_partial_match.json");
        stubFor(get(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("37.4224764,-122.0842499"))
                .withQueryParam("key", equalTo("test-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(payload)));

        // Act
        var result = client.reverseGeocode(37.4224764, -122.0842499);

        // Assert
        assertThat(result).isPresent().hasValueSatisfying(response -> {
            assertThat(response.status()).isEqualTo(ResponseStatus.OK);
            assertThat(response.results()).isNotNull();
            assertThat(response.results()).isNotEmpty();
            assertThat(response.results().getFirst().partialMatch()).isTrue();
            assertThat(response.results().getFirst().formattedAddress())
                    .isEqualTo("1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA");
        });
    }
}
