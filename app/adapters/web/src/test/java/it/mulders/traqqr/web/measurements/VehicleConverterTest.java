package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import it.mulders.traqqr.web.vehicles.VehicleMapper;
import it.mulders.traqqr.web.vehicles.VehicleMapperImpl;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class VehicleConverterTest implements WithAssertions {
    private final Owner owner1 = new Owner() {
        @Override
        public String code() {
            return "owner1";
        }

        @Override
        public String displayName() {
            return "";
        }

        @Override
        public String profilePictureUrl() {
            return "";
        }
    };
    private final Vehicle vehicle1 = new Vehicle("code1", "description1", "owner1", null);
    private final Set<Vehicle> vehicles = new HashSet<>() {
        {
            add(vehicle1);
            add(new Vehicle("code2", "description2", "owner2", null));
        }
    };

    private final VehicleRepository repository = new VehicleRepository() {
        private final Set<Vehicle> vehicles = VehicleConverterTest.this.vehicles;

        @Override
        public Optional<Vehicle> findByCode(String code) {
            return Optional.empty();
        }

        @Override
        public Collection<Vehicle> findByOwner(Owner owner) {
            return vehicles.stream()
                    .filter(vehicle -> owner.code().equals(vehicle.ownerId()))
                    .collect(Collectors.toSet());
        }

        @Override
        public void save(Vehicle vehicle) {}

        @Override
        public void update(Vehicle vehicle) {}

        @Override
        public void removeVehicle(Vehicle vehicle) {}
    };
    private final VehicleMapper mapper = new VehicleMapperImpl();

    private final VehicleConverter converter = new VehicleConverter(mapper, repository, owner1);

    @Test
    void should_only_allow_lookup_of_vehicles_of_owner() {
        assertThat(converter.getAsObject(null, null, "code1")).isNotNull();
        assertThat(converter.getAsObject(null, null, "code2")).isNull();
    }

    @Test
    void should_allow_lookup_of_vehicles_by_code() {
        assertThat(converter.getAsObject(null, null, "code1")).isEqualTo(mapper.vehicleToDto(vehicle1));
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
        assertThat(converter.getAsString(null, null, mapper.vehicleToDto(vehicle1)))
                .isEqualTo("code1");
    }
}
