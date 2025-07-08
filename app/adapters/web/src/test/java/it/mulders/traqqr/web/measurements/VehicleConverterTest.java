package it.mulders.traqqr.web.measurements;

import static it.mulders.traqqr.domain.fakes.OwnerFaker.createOwner;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.VehicleMapperImpl;
import java.util.HashSet;
import java.util.Set;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VehicleConverterTest implements WithAssertions {
    private final Owner owner = createOwner();
    private final Vehicle vehicle = createVehicle(owner);
    private final Set<Vehicle> vehicles = new HashSet<>() {
        {
            add(vehicle);
            add(createVehicle());
        }
    };

    private final VehicleRepository repository = new InMemoryVehicleRepository(vehicles);
    private final VehicleMapper mapper = new VehicleMapperImpl();

    private final VehicleConverter converter = new VehicleConverter(mapper, repository, owner);

    @Test
    void should_only_allow_lookup_of_vehicles_of_owner() {
        assertThat(converter.getAsObject(null, null, vehicle.code())).isNotNull();
        assertThat(converter.getAsObject(null, null, "code2")).isNull();
    }

    @Test
    void should_allow_lookup_of_vehicles_by_code() {
        assertThat(converter.getAsObject(null, null, vehicle.code())).isEqualTo(mapper.vehicleToDto(vehicle));
    }

    @Test
    void should_not_break_on_null_input() {
        assertThat(converter.getAsObject(null, null, null)).isNull();
        assertThat(converter.getAsString(null, null, null)).isNull();
    }

    @Test
    void should_not_break_on_empty_input() {
        assertThat(converter.getAsObject(null, null, "")).isNull();
    }

    @Test
    void should_use_code_as_identifier() {
        assertThat(converter.getAsString(null, null, mapper.vehicleToDto(vehicle)))
                .isEqualTo(vehicle.code());
    }
}
