package it.mulders.traqqr.jpa.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.jpa.vehicles.VehicleEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JpaMeasurementRepository implements MeasurementRepository {
    private static final Logger log = LoggerFactory.getLogger(JpaMeasurementRepository.class);
    private final EntityManager em;
    private final MeasurementMapper mapper;

    @Inject
    public JpaMeasurementRepository(final EntityManager em, final MeasurementMapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void save(Measurement measurement) {
        this.em
                .createQuery("select v from VehicleEntity v where v.code = :code", VehicleEntity.class)
                .setParameter("code", measurement.vehicle().code())
                .getResultStream()
                .findAny()
                .ifPresent(vehicleEntity -> {
                    var entity = mapper.measurementToMeasurementEntity(measurement);
                    log.debug("saving measurement {}", entity);
                    entity.setVehicle(vehicleEntity);
                    entity.setId(UUID.randomUUID());

                    try {
                        em.joinTransaction();
                        em.persist(entity);
                        em.flush();
                        log.info("Measurement stored; id={}", entity.getId());
                    } catch (PersistenceException e) {
                        log.error("Database error during measurement storage; id={}", entity.getId(), e);
                        throw e;
                    }
                });
    }

    @Override
    public Collection<Measurement> findByVehicle(Vehicle vehicle) {
        log.debug("Finding measurements; vehicle={}", vehicle.code());
        return this.em
                .createQuery(
                        "select m from MeasurementEntity m where m.vehicle.code = :vehicle_code order by m.registeredAt desc",
                        MeasurementEntity.class)
                .setParameter("vehicle_code", vehicle.code())
                .getResultStream()
                .map(mapper::measurementEntityToMeasurement)
                .toList();
    }
}
