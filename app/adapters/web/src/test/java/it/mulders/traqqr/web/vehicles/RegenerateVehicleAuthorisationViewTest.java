package it.mulders.traqqr.web.vehicles;

import static it.mulders.traqqr.domain.fakes.OwnerFaker.createOwner;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.mem.vehicles.InMemoryVehicleRepository;
import it.mulders.traqqr.web.faces.DummyUIComponent;
import it.mulders.traqqr.web.faces.FacesContextMock;
import it.mulders.traqqr.web.prime.PrimeFacesMock;
import jakarta.faces.context.FacesContext;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
class RegenerateVehicleAuthorisationViewTest implements WithAssertions {
    private final FacesContext facesContext = new FacesContextMock();
    private final InMemoryVehicleRepository vehicleRepository = new InMemoryVehicleRepository();
    private final VehicleMapper vehicleMapper = new VehicleMapperImpl();

    private final RegenerateVehicleAuthorisationView view = new RegenerateVehicleAuthorisationView(
            facesContext, new PrimeFacesMock(), vehicleMapper, vehicleRepository);

    @BeforeEach
    void prepareRawKeyComponent() {
        view.setRawKey(new DummyUIComponent(RandomStringUtils.generateRandomIdentifier(5)));
    }

    @Test
    void should_regenerate_authorisation() {
        // Arrange
        var owner = createOwner();
        var vehicle = createVehicle(owner);
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
        var owner = createOwner();
        var vehicle = createVehicle(owner);
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
}
