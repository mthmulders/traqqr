package it.mulders.traqqr.gmaps;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import it.mulders.traqqr.gmaps.dto.GeocodeResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.within;

class GoogleReverseGeocodingClientWiremockTest implements WithAssertions {
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void reverseGeocode_parsesGoogleResponse() throws Exception {
        // Load sample response from test resources
        String sampleResponse;
        try (InputStream is = getClass().getResourceAsStream("/google-reverse-geocoding/sample_1600_amphitheatre.json")) {
            assertThat(is).describedAs("Test resource not found").isNotNull();
            sampleResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Stub WireMock for the expected path and query
        stubFor(get(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("37.4224764,-122.0842499"))
                .withQueryParam("key", equalTo("test-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(sampleResponse)));

        String baseUrl = String.format("http://localhost:%d/maps/api/geocode/json", wireMockServer.port());
        GoogleReverseGeocodingClient client = new GoogleReverseGeocodingClient("test-key", HttpClient.newHttpClient(), baseUrl);

        Optional<GeocodeResponse> respOpt = client.reverseGeocode(37.4224764, -122.0842499);
        assertThat(respOpt).isPresent();
        GeocodeResponse resp = respOpt.get();
        assertThat(resp.status).isEqualTo("OK");
        assertThat(resp.results).isNotNull();
        assertThat(resp.results).isNotEmpty();
        assertThat(resp.results.get(0).formattedAddress).isEqualTo("1600 Amphitheatre Parkway, Mountain View, CA 94043, USA");
        assertThat(resp.results.get(0).geometry.location.lat).isCloseTo(37.4224764, within(0.0000001));
        assertThat(resp.results.get(0).geometry.location.lng).isCloseTo(-122.0842499, within(0.0000001));

        verify(getRequestedFor(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("37.4224764,-122.0842499"))
                .withQueryParam("key", equalTo("test-key")));
    }

    @Test
    void reverseGeocode_zeroResults_returnsZeroResultsStatus() throws Exception {
        // Load ZERO_RESULTS fixture
        String sampleResponse;
        try (InputStream is = getClass().getResourceAsStream("/google-reverse-geocoding/sample_zero_results.json")) {
            assertThat(is).describedAs("Test resource not found").isNotNull();
            sampleResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        stubFor(get(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("0.0,0.0"))
                .withQueryParam("key", equalTo("test-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(sampleResponse)));

        String baseUrl = String.format("http://localhost:%d/maps/api/geocode/json", wireMockServer.port());
        GoogleReverseGeocodingClient client = new GoogleReverseGeocodingClient("test-key", HttpClient.newHttpClient(), baseUrl);

        Optional<GeocodeResponse> respOpt = client.reverseGeocode(0.0, 0.0);
        assertThat(respOpt).isPresent();
        GeocodeResponse resp = respOpt.get();
        assertThat(resp.status).isEqualTo("ZERO_RESULTS");
        assertThat(resp.results).isNotNull();
        assertThat(resp.results).isEmpty();

        verify(getRequestedFor(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("0.0,0.0"))
                .withQueryParam("key", equalTo("test-key")));
    }

    @Test
    void reverseGeocode_partialMatch_flagIsSet() throws Exception {
        // Load partial_match fixture
        String sampleResponse;
        try (InputStream is = getClass().getResourceAsStream("/google-reverse-geocoding/sample_partial_match.json")) {
            assertThat(is).describedAs("Test resource not found").isNotNull();
            sampleResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        stubFor(get(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("37.4224764,-122.0842499"))
                .withQueryParam("key", equalTo("test-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(sampleResponse)));

        String baseUrl = String.format("http://localhost:%d/maps/api/geocode/json", wireMockServer.port());
        GoogleReverseGeocodingClient client = new GoogleReverseGeocodingClient("test-key", HttpClient.newHttpClient(), baseUrl);

        Optional<GeocodeResponse> respOpt = client.reverseGeocode(37.4224764, -122.0842499);
        assertThat(respOpt).isPresent();
        GeocodeResponse resp = respOpt.get();
        assertThat(resp.status).isEqualTo("OK");
        assertThat(resp.results).isNotNull();
        assertThat(resp.results).isNotEmpty();
        assertThat(resp.results.get(0).partialMatch).isTrue();
        assertThat(resp.results.get(0).formattedAddress).isEqualTo("1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA");

        verify(getRequestedFor(urlPathEqualTo("/maps/api/geocode/json"))
                .withQueryParam("latlng", equalTo("37.4224764,-122.0842499"))
                .withQueryParam("key", equalTo("test-key")));
    }
}
