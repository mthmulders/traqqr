package it.mulders.traqqr.jpa.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.jpa.vehicles.VehicleEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JpaMeasurementRepository implements MeasurementRepository {
    private static final Logger log = LoggerFactory.getLogger(JpaMeasurementRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private MeasurementMapper mapper;

    public JpaMeasurementRepository() {}

    protected JpaMeasurementRepository(final EntityManager em, final MeasurementMapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void save(Measurement measurement) {
        var vehicle = this.em
                .createQuery("select v from Vehicle v where v.code = :code", VehicleEntity.class)
                .setParameter("code", measurement.vehicle().code())
                .getSingleResult();

        var entity = mapper.measurementToMeasurementEntity(measurement);
        entity.setVehicle(vehicle);

        try {
            em.merge(entity);
            log.info("Measurement stored; id={}", entity.getId());
        } catch (PersistenceException e) {
            log.error("Database error during measurement storage; id={}", entity.getId(), e);
            throw e;
        }
    }

    @Override
    public Collection<Measurement> findByVehicle(Vehicle vehicle) {
        log.debug("Finding measurements; vehicle={}", vehicle.code());
        return this.findByVehicle(vehicle, null);
    }

    @Override
    public Collection<Measurement> findByVehicle(Vehicle vehicle, Pagination pagination) {
        var query = this.em
                .createQuery(
                        "select m from Measurement m where m.vehicle.code = :vehicle_code order by m.measuredAt desc",
                        MeasurementEntity.class)
                .setParameter("vehicle_code", vehicle.code());

        if (pagination != null) {
            log.debug(
                    "Finding measurements; vehicle={}, offset={}, limit={}",
                    vehicle.code(),
                    pagination.offset(),
                    pagination.limit());
            query.setFirstResult(pagination.offset()).setMaxResults(pagination.limit());
        } else {
            log.debug("Finding measurements; vehicle={}", vehicle.code());
        }

        return query.getResultStream()
                .map(mapper::measurementEntityToMeasurement)
                .toList();
    }

    @Override
    public Stream<Measurement> exampleStreamingFindForBatchJob() {
        var query = this.em.createQuery("""
                        select m
                        from Measurement m
                        where not exists (
                            select 1
                            from JobItem ji
                            where ji.itemId = m.id
                        )
                        """, MeasurementEntity.class);
        return query.setMaxResults(100).getResultStream().map(mapper::measurementEntityToMeasurement);
    }

    @Override
    public Collection<Measurement> findOldestMeasurementsWithoutLocationDescription() {
        var query = this.em.createQuery("""
                        select m
                        from Measurement m
                        where m.locationDescription is null
                        order by m.measuredAt asc
                        """, MeasurementEntity.class);
        return query.setMaxResults(100)
                .getResultStream()
                .map(mapper::measurementEntityToMeasurement)
                .toList();
    }

    @Override
    public long countByVehicle(Vehicle vehicle) {
        log.debug("Counting measurements; vehicle={}", vehicle.code());
        return this.em
                .createQuery("select count(m) from Measurement m where m.vehicle.code = :vehicle_code", Long.class)
                .setParameter("vehicle_code", vehicle.code())
                .getSingleResult();
    }

    private Optional<MeasurementEntity> findEntityById(UUID id) {
        return this.em
                .createQuery("select m from Measurement m where m.id = :id", MeasurementEntity.class)
                .setParameter("id", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void removeMeasurement(Measurement measurement) {
        findEntityById(measurement.id()).ifPresent(entity -> {
            try {
                em.remove(entity);
                em.flush();
                log.debug("Measurement removed; id={}", measurement.id());
            } catch (IllegalArgumentException | TransactionRequiredException e) {
                log.error("Error removing measurement; id={}", measurement.id(), e);
                throw e;
            }
        });
    }
}
