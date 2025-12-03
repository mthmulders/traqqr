package it.mulders.traqqr.gmaps;

import static org.assertj.core.api.InstanceOfAssertFactories.type;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup.LocationLookupResult;
import it.mulders.traqqr.gmaps.dto.GeocodeResponse;
import it.mulders.traqqr.gmaps.dto.Result;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GoogleMapsLocationLookupImplTest implements WithAssertions {
    private final double KNOWN_LATITUDE = 37.4224764;
    private final double KNOWN_LONGITUDE = -122.0842499;
    private final String KNOWN_DESCRIPTION = "1600 Amphitheatre Parkway, Mountain View, CA 94043, USA";

    private final double NO_STREET_ADDRESS_LATITUDE = 37.4225764;
    private final double NO_STREET_ADDRESS_LONGITUDE = -122.1842499;

    private final double MULTIPLE_STREET_ADDRESSES_LATITUDE = 36.4225764;
    private final double MULTIPLE_STREET_ADDRESSES_LONGITUDE = -124.1842499;
    private final String MULTIPLE_STREET_ADDRESSES_LONGITUDE_FIRST_DESCRIPTION =
            "1600 Amphitheatre Parkway, Mountain View, CA 94043, USA";

    private final double REQUEST_DENIED_LATITUDE = 35.4225764;
    private final double REQUEST_DENIED_LONGITUDE = -125.1842499;
    private final String REQUEST_DENIED_MESSAGE = "Request to Google Maps API was denied";

    private final double INVALID_REQUEST_LATITUDE = Double.MAX_VALUE;
    private final double INVALID_REQUEST_LONGITUDE = Double.MIN_VALUE;
    private final String INVALID_REQUEST_MESSAGE = "The request to Google Maps API was invalid";

    private final double OVER_QUERY_LATITUDE = 33.4764225;
    private final double OVER_QUERY_LONGITUDE = 122.1849429;
    private final String OVER_QUERY_MESSAGE = "You have exceeded your daily request quota for this API.";

    private final double UNKNOWN_ERROR_LATITUDE = 31.4764225;
    private final double UNKNOWN_ERROR_LONGITUDE = 129.1849429;
    private final String UNKNOWN_ERROR_MESSAGE = "An unknown error occurred.";

    private final GoogleReverseGeocodingClient client = (latitude, longitude) -> {
        if (latitude == KNOWN_LATITUDE && longitude == KNOWN_LONGITUDE) {
            var results = List.of(
                    new Result(KNOWN_DESCRIPTION, "ChIJ2eUgeAK6j4ARbn5u_wAGqWA", List.of("street_address"), false));
            return Optional.of(new GeocodeResponse(results, GeocodeResponse.ResponseStatus.OK, null));
        } else if (latitude == NO_STREET_ADDRESS_LATITUDE && longitude == NO_STREET_ADDRESS_LONGITUDE) {
            var results = List.of(new Result(
                    "Some Place Without Street Address", "ChIJ2eUgeAK6j4ARbn5u_wAGqWA", List.of("locality"), false));
            return Optional.of(new GeocodeResponse(results, GeocodeResponse.ResponseStatus.OK, null));
        } else if (latitude == MULTIPLE_STREET_ADDRESSES_LATITUDE && longitude == MULTIPLE_STREET_ADDRESSES_LONGITUDE) {
            var results = List.of(
                    new Result(
                            MULTIPLE_STREET_ADDRESSES_LONGITUDE_FIRST_DESCRIPTION,
                            "ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
                            List.of("street_address"),
                            false),
                    new Result(
                            "456 Sample Ave, Sample Town, SA 67890, USA",
                            "ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
                            List.of("street_address"),
                            false));
            return Optional.of(new GeocodeResponse(results, GeocodeResponse.ResponseStatus.OK, null));
        } else if (latitude == KNOWN_LATITUDE || longitude == KNOWN_LONGITUDE) {
            return Optional.of(new GeocodeResponse(List.of(), GeocodeResponse.ResponseStatus.ZERO_RESULTS, null));
        } else if (latitude == REQUEST_DENIED_LATITUDE && longitude == REQUEST_DENIED_LONGITUDE) {
            return Optional.of(new GeocodeResponse(
                    List.of(), GeocodeResponse.ResponseStatus.REQUEST_DENIED, "API key is invalid."));
        } else if (latitude == INVALID_REQUEST_LATITUDE && longitude == INVALID_REQUEST_LONGITUDE) {
            return Optional.of(new GeocodeResponse(
                    List.of(), GeocodeResponse.ResponseStatus.INVALID_REQUEST, "Invalid coordinates"));
        } else if (latitude == OVER_QUERY_LATITUDE && longitude == OVER_QUERY_LONGITUDE) {
            return Optional.of(
                    new GeocodeResponse(List.of(), GeocodeResponse.ResponseStatus.OVER_QUERY_LIMIT, "Over quota"));
        } else if (latitude == UNKNOWN_ERROR_LATITUDE && longitude == UNKNOWN_ERROR_LONGITUDE) {
            return Optional.of(new GeocodeResponse(List.of(), GeocodeResponse.ResponseStatus.UNKNOWN_ERROR, null));
        } else {
            return Optional.empty();
        }
    };

    private final GoogleMapsLocationLookupImpl locationLookup = new GoogleMapsLocationLookupImpl(client);

    @Test
    void should_extract_location_from_API_response() {
        // Arrange
        var location = new Measurement.Location(KNOWN_LATITUDE, KNOWN_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Success.class)
                .asInstanceOf(type(LocationLookupResult.Success.class))
                .extracting(LocationLookupResult.Success::location, as(type(Measurement.Location.class)))
                .extracting(Measurement.Location::description, as(type(String.class)))
                .satisfies(description -> assertThat(description).isEqualTo(KNOWN_DESCRIPTION));
    }

    @Test
    void should_handle_no_street_address_in_API_response() {
        // Arrange
        var location = new Measurement.Location(NO_STREET_ADDRESS_LATITUDE, NO_STREET_ADDRESS_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result).isInstanceOf(LocationLookupResult.NotFound.class);
    }

    @Test
    void should_handle_zero_results_in_API_response() {
        // Arrange
        var location = new Measurement.Location(KNOWN_LATITUDE, NO_STREET_ADDRESS_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result).isInstanceOf(LocationLookupResult.NotFound.class);
    }

    @Test
    void should_handle_multiple_street_address_in_API_response() {
        // Arrange
        var location =
                new Measurement.Location(MULTIPLE_STREET_ADDRESSES_LATITUDE, MULTIPLE_STREET_ADDRESSES_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Success.class)
                .asInstanceOf(type(LocationLookupResult.Success.class))
                .extracting(LocationLookupResult.Success::location, as(type(Measurement.Location.class)))
                .extracting(Measurement.Location::description, as(type(String.class)))
                .satisfies(description ->
                        assertThat(description).isEqualTo(MULTIPLE_STREET_ADDRESSES_LONGITUDE_FIRST_DESCRIPTION));
    }

    @Test
    void should_handle_request_denied_in_API_response() {
        // Arrange
        var location = new Measurement.Location(REQUEST_DENIED_LATITUDE, REQUEST_DENIED_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Failure.class)
                .asInstanceOf(type(LocationLookupResult.Failure.class))
                .extracting(LocationLookupResult.Failure::cause, as(type(Throwable.class)))
                .extracting(Throwable::getMessage, as(type(String.class)))
                .satisfies(message -> assertThat(message).isEqualTo(REQUEST_DENIED_MESSAGE));
    }

    @Test
    void should_handle_invalid_request_in_API_response() {
        // Arrange
        var location = new Measurement.Location(INVALID_REQUEST_LATITUDE, INVALID_REQUEST_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Failure.class)
                .asInstanceOf(type(LocationLookupResult.Failure.class))
                .extracting(LocationLookupResult.Failure::cause, as(type(Throwable.class)))
                .extracting(Throwable::getMessage, as(type(String.class)))
                .satisfies(message -> assertThat(message).isEqualTo(INVALID_REQUEST_MESSAGE));
    }

    @Test
    void should_handle_over_query_in_API_response() {
        // Arrange
        var location = new Measurement.Location(OVER_QUERY_LATITUDE, OVER_QUERY_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Failure.class)
                .asInstanceOf(type(LocationLookupResult.Failure.class))
                .extracting(LocationLookupResult.Failure::cause, as(type(Throwable.class)))
                .extracting(Throwable::getMessage, as(type(String.class)))
                .satisfies(message -> assertThat(message).isEqualTo(OVER_QUERY_MESSAGE));
    }

    @Test
    void should_handle_unknown_error_in_API_response() {
        // Arrange
        var location = new Measurement.Location(UNKNOWN_ERROR_LATITUDE, UNKNOWN_ERROR_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Failure.class)
                .asInstanceOf(type(LocationLookupResult.Failure.class))
                .extracting(LocationLookupResult.Failure::cause, as(type(Throwable.class)))
                .extracting(Throwable::getMessage, as(type(String.class)))
                .satisfies(message -> assertThat(message).isEqualTo(UNKNOWN_ERROR_MESSAGE));
    }
}
