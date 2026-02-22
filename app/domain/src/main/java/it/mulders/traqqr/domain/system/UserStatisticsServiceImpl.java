package it.mulders.traqqr.domain.system;

import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.system.api.UserStatisticsService;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.spi.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserStatisticsServiceImpl implements UserStatisticsService {
    private static final Logger log = LoggerFactory.getLogger(UserStatisticsServiceImpl.class);

    private final MeasurementRepository measurementRepository;
    private final VehicleRepository vehicleRepository;

    @Inject
    public UserStatisticsServiceImpl(
            final MeasurementRepository measurementRepository, final VehicleRepository vehicleRepository) {
        this.measurementRepository = measurementRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public long countMeasurements(Owner owner) {
        return vehicleRepository.findByOwner(owner).stream()
                .mapToLong(measurementRepository::countByVehicle)
                .sum();
    }
}
