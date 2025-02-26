package it.mulders.traqqr.web.vehicles;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.faces.MockFacesContext;
import jakarta.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.HashSet;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RegenerateVehicleAuthorisationViewTest implements WithAssertions {
    private static final MockFacesContext facesContext = new MockFacesContext();
    private final InMemoryVehicleRepository vehicleRepository = new InMemoryVehicleRepository();
    private final VehicleMapper vehicleMapper = new VehicleMapperImpl();

    private final RegenerateVehicleAuthorisationView view =
            new RegenerateVehicleAuthorisationView(vehicleMapper, vehicleRepository);

    @BeforeEach
    void prepareRawKeyComponent() {
        view.setRawKey(new org.primefaces.component.inputtext.InputText() {
            @Override
            public String getClientId(FacesContext context) {
                return RandomStringUtils.generateRandomIdentifier(5);
            }
        });
    }

    @AfterAll
    static void cleanupFacesContext() {
        facesContext.unregister();
    }

    @Test
    void should_regenerate_authorisation() {
        // Arrange
        var owner = ownerCreator(1);
        var vehicle = new Vehicle(
                RandomStringUtils.generateRandomIdentifier(5),
                RandomStringUtils.generateRandomIdentifier(30),
                owner.code(),
                new HashSet<>(),
                BigDecimal.valueOf(82));
        vehicleRepository.save(vehicle);

        // Act
        view.setSelectedVehicle(vehicleMapper.vehicleToDto(vehicle));
        view.regenerateApiKey();

        // Assert
        assertThat(view.getGeneratedAuthorisation()).isNotNull();
        assertThat(view.getGeneratedAuthorisation().getHashedKey()).isNotEmpty();
        assertThat(view.getGeneratedAuthorisation().getRawKey()).isNotEmpty();
    }

    @Test
    void should_persist_new_authorisation() {
        // Arrange
        var owner = ownerCreator(1);
        var vehicle = new Vehicle(
                RandomStringUtils.generateRandomIdentifier(5),
                RandomStringUtils.generateRandomIdentifier(30),
                owner.code(),
                new HashSet<>(),
                BigDecimal.valueOf(82));
        vehicleRepository.save(vehicle);

        // Act
        view.setSelectedVehicle(vehicleMapper.vehicleToDto(vehicle));
        view.regenerateApiKey();
        var generatedAuthorisation = view.getGeneratedAuthorisation();

        // Assert
        assertThat(vehicleRepository.findByCode(vehicle.code())).isNotEmpty().hasValueSatisfying(found -> {
            assertThat(found.authorisations()).isNotEmpty().anySatisfy(authorisation -> {
                assertThat(authorisation.getHashedKey()).isEqualTo(generatedAuthorisation.getHashedKey());
                assertThat(authorisation.getInvalidatedAt()).isNull();
            });
        });
    }

    private Owner ownerCreator(final Integer number) {
        return new Owner() {
            @Override
            public String code() {
                return "o" + number;
            }

            @Override
            public String displayName() {
                return "Owner " + number;
            }

            @Override
            public String profilePictureUrl() {
                return "";
            }
        };
    }
}
