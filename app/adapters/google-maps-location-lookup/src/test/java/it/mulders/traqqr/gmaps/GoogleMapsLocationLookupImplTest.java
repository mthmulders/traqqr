package it.mulders.traqqr.gmaps;

import static org.assertj.core.api.InstanceOfAssertFactories.type;

import it.mulders.traqqr.domain.measurements.Measurement.Location;
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
    private static final String KNOWN_DESCRIPTION = "1600 Amphitheatre Parkway, Mountain View, CA 94043, USA";
    private static final Location KNOWN = new Location(37.4224764, -122.0842499);

    private static final Location NO_STREET_ADDRESS = new Location(37.4225764, -122.1842499);

    private static final Location MULTIPLE_STREET_ADDRESSES = new Location(36.4225764, -124.1842499);
    private static final String MULTIPLE_STREET_ADDRESSES_LONGITUDE_FIRST_DESCRIPTION =
            "1600 Amphitheatre Parkway, Mountain View, CA 94043, USA";

    private static final Location ZERO_RESULTS = new Location(Double.MIN_VALUE, Double.MAX_VALUE);

    private static final Location REQUEST_DENIED = new Location(35.4225764, -125.1842499);
    private static final String REQUEST_DENIED_MESSAGE = "Request to Google Maps API was denied";

    private static final Location INVALID_REQUEST = new Location(Double.MAX_VALUE, Double.MIN_VALUE);
    private static final String INVALID_REQUEST_MESSAGE = "The request to Google Maps API was invalid";

    private static final Location OVER_QUERY = new Location(33.4764225, 122.1849429);
    private static final String OVER_QUERY_MESSAGE = "You have exceeded your daily request quota for this API.";

    private static final Location UNKNOWN_ERROR = new Location(31.4764225, 129.1849429);
    private static final String UNKNOWN_ERROR_MESSAGE = "An unknown error occurred.";

    private final GoogleReverseGeocodingClient client = location -> {
        if (KNOWN.equals(location)) {
            var results = List.of(
                    new Result(KNOWN_DESCRIPTION, "ChIJ2eUgeAK6j4ARbn5u_wAGqWA", List.of("street_address"), false));
            return Optional.of(new GeocodeResponse(results, GeocodeResponse.ResponseStatus.OK, null));
        } else if (NO_STREET_ADDRESS.equals(location)) {
            var results = List.of(new Result(
                    "Some Place Without Street Address", "ChIJ2eUgeAK6j4ARbn5u_wAGqWA", List.of("locality"), false));
            return Optional.of(new GeocodeResponse(results, GeocodeResponse.ResponseStatus.OK, null));
        } else if (MULTIPLE_STREET_ADDRESSES.equals(location)) {
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
        } else if (ZERO_RESULTS.equals(location)) {
            return Optional.of(new GeocodeResponse(List.of(), GeocodeResponse.ResponseStatus.ZERO_RESULTS, null));
        } else if (REQUEST_DENIED.equals(location)) {
            return Optional.of(new GeocodeResponse(
                    List.of(), GeocodeResponse.ResponseStatus.REQUEST_DENIED, "API key is invalid."));
        } else if (INVALID_REQUEST.equals(location)) {
            return Optional.of(new GeocodeResponse(
                    List.of(), GeocodeResponse.ResponseStatus.INVALID_REQUEST, "Invalid coordinates"));
        } else if (OVER_QUERY.equals(location)) {
            return Optional.of(
                    new GeocodeResponse(List.of(), GeocodeResponse.ResponseStatus.OVER_QUERY_LIMIT, "Over quota"));
        } else if (UNKNOWN_ERROR.equals(location)) {
            return Optional.of(new GeocodeResponse(List.of(), GeocodeResponse.ResponseStatus.UNKNOWN_ERROR, null));
        } else {
            return Optional.empty();
        }
    };

    private final GoogleMapsLocationLookupImpl locationLookup = new GoogleMapsLocationLookupImpl(client);

    @Test
    void should_extract_location_from_API_response() {
        // Act
        var result = locationLookup.lookup(KNOWN);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Success.class)
                .asInstanceOf(type(LocationLookupResult.Success.class))
                .extracting(LocationLookupResult.Success::location, as(type(Location.class)))
                .extracting(Location::description, as(type(String.class)))
                .satisfies(description -> assertThat(description).isEqualTo(KNOWN_DESCRIPTION));
    }

    @Test
    void should_handle_no_street_address_in_API_response() {
        // Act
        var result = locationLookup.lookup(NO_STREET_ADDRESS);

        // Assert
        assertThat(result).isInstanceOf(LocationLookupResult.NotFound.class);
    }

    @Test
    void should_handle_zero_results_in_API_response() {
        // Act
        var result = locationLookup.lookup(ZERO_RESULTS);

        // Assert
        assertThat(result).isInstanceOf(LocationLookupResult.NotFound.class);
    }

    @Test
    void should_handle_multiple_street_address_in_API_response() {
        // Act
        var result = locationLookup.lookup(MULTIPLE_STREET_ADDRESSES);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Success.class)
                .asInstanceOf(type(LocationLookupResult.Success.class))
                .extracting(LocationLookupResult.Success::location, as(type(Location.class)))
                .extracting(Location::description, as(type(String.class)))
                .satisfies(description ->
                        assertThat(description).isEqualTo(MULTIPLE_STREET_ADDRESSES_LONGITUDE_FIRST_DESCRIPTION));
    }

    @Test
    void should_handle_request_denied_in_API_response() {
        // Act
        var result = locationLookup.lookup(REQUEST_DENIED);

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
        // Act
        var result = locationLookup.lookup(INVALID_REQUEST);

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
        // Act
        var result = locationLookup.lookup(OVER_QUERY);

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
        // Act
        var result = locationLookup.lookup(UNKNOWN_ERROR);

        // Assert
        assertThat(result)
                .isInstanceOf(LocationLookupResult.Failure.class)
                .asInstanceOf(type(LocationLookupResult.Failure.class))
                .extracting(LocationLookupResult.Failure::cause, as(type(Throwable.class)))
                .extracting(Throwable::getMessage, as(type(String.class)))
                .satisfies(message -> assertThat(message).isEqualTo(UNKNOWN_ERROR_MESSAGE));
    }
}
