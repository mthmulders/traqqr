package it.mulders.traqqr.gmaps;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.LocationLookup.LocationLookupResult;
import it.mulders.traqqr.gmaps.dto.GeocodeResponse;
import it.mulders.traqqr.gmaps.dto.Result;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.InstanceOfAssertFactories.type;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GoogleMapsLocationLookupImplTest implements WithAssertions {
    private final double KNOWN_LATITUDE = 37.4224764;
    private final double KNOWN_LONGITUDE = -122.0842499;
    private final String KNOWN_DESCRIPTION = "1600 Amphitheatre Parkway, Mountain View, CA 94043, USA";

    private final double NO_STREET_ADDRESS_LATITUDE = 37.4225764;
    private final double NO_STREET_ADDRESS_LONGITUDE = -122.1842499;

    private final double MULTIPLE_STREET_ADDRESSES_LATITUDE = 36.4225764;
    private final double MULTIPLE_STREET_ADDRESSES_LONGITUDE = -124.1842499;
    private final String MULTIPLE_STREET_ADDRESSES_LONGITUDE_FIRST_DESCRIPTION = "1600 Amphitheatre Parkway, Mountain View, CA 94043, USA";

    private final GoogleReverseGeocodingClient client = (latitude, longitude) -> {
        if (latitude == KNOWN_LATITUDE && longitude == KNOWN_LONGITUDE) {
            var results = List.of(new Result(
                    KNOWN_DESCRIPTION,
                    "ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
                    List.of("street_address"),
                    false
            ));
            return Optional.of(new GeocodeResponse(
                    results,
                    GeocodeResponse.ResponseStatus.OK,
                    null
            ));
        } else if (latitude == NO_STREET_ADDRESS_LATITUDE && longitude == NO_STREET_ADDRESS_LONGITUDE) {
            var results = List.of(new Result(
                    "Some Place Without Street Address",
                    "ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
                    List.of("locality"),
                    false
            ));
            return Optional.of(new GeocodeResponse(
                    results,
                    GeocodeResponse.ResponseStatus.OK,
                    null
            ));
        } else if (latitude == MULTIPLE_STREET_ADDRESSES_LATITUDE && longitude == MULTIPLE_STREET_ADDRESSES_LONGITUDE) {
            var results = List.of(
                    new Result(
                            MULTIPLE_STREET_ADDRESSES_LONGITUDE_FIRST_DESCRIPTION,
                            "ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
                            List.of("street_address"),
                            false
                    ),
                    new Result(
                            "456 Sample Ave, Sample Town, SA 67890, USA",
                            "ChIJ2eUgeAK6j4ARbn5u_wAGqWA",
                            List.of("street_address"),
                            false
                    )
            );
            return Optional.of(new GeocodeResponse(
                    results,
                    GeocodeResponse.ResponseStatus.OK,
                    null
            ));
        } else if (latitude == KNOWN_LATITUDE || longitude == KNOWN_LONGITUDE) {
            return Optional.of(new GeocodeResponse(
                    List.of(),
                    GeocodeResponse.ResponseStatus.ZERO_RESULTS,
                    null
            ));
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
        assertThat(result).isInstanceOf(LocationLookupResult.Success.class)
                .asInstanceOf(type(LocationLookupResult.Success.class))
                .extracting(LocationLookupResult.Success::location, as(type(Measurement.Location.class)))
                .satisfies(outcome -> {
                    assertThat(outcome.description()).isEqualTo(KNOWN_DESCRIPTION);
                });
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
        var location = new Measurement.Location(MULTIPLE_STREET_ADDRESSES_LATITUDE, MULTIPLE_STREET_ADDRESSES_LONGITUDE);

        // Act
        var result = locationLookup.lookup(location);

        // Assert
        assertThat(result).isInstanceOf(LocationLookupResult.Success.class)
                .asInstanceOf(type(LocationLookupResult.Success.class))
                .extracting(LocationLookupResult.Success::location, as(type(Measurement.Location.class)))
                .satisfies(outcome -> {
                    assertThat(outcome.description()).isEqualTo(MULTIPLE_STREET_ADDRESSES_LONGITUDE_FIRST_DESCRIPTION);
                });
    }
}
