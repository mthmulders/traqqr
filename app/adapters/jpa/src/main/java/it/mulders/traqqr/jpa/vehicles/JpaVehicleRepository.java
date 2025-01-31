package it.mulders.traqqr.jpa.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JpaVehicleRepository implements VehicleRepository {
    private static final Logger log = LoggerFactory.getLogger(JpaVehicleRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private VehicleMapper mapper;

    public JpaVehicleRepository() {}

    protected JpaVehicleRepository(final EntityManager em, final VehicleMapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public Optional<Vehicle> findByCode(final String code) {
        return findEntityByCode(code).map(mapper::vehicleEntityToVehicle);
    }

    @Override
    public Collection<Vehicle> findByOwner(Owner owner) {
        var query = this.em.createQuery("select v from Vehicle v where v.ownerId = :ownerId", VehicleEntity.class);
        return query.setParameter("ownerId", owner.code())
                .getResultStream()
                .map(mapper::vehicleEntityToVehicle)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void save(Vehicle vehicle) {
        var entity = this.mapper.vehicleToVehicleEntity(vehicle);
        try {
            em.persist(entity);
            log.debug("Vehicle saved; code={}", vehicle.code());
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            log.error("Error registering vehicle; code={}", vehicle.code(), e);
            throw e;
        }
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void update(Vehicle vehicle) {
        var entity = this.mapper.vehicleToVehicleEntity(vehicle);
        findEntityByCode(vehicle.code()).ifPresent(found -> {
            entity.setId(found.getId());
            entity.getAuthorisations().forEach(authorisation -> authorisation.setVehicle(found));

            try {
                em.merge(entity);
                log.debug("Vehicle updated; code={}", vehicle.code());
            } catch (PersistenceException e) {
                log.error("Database error during vehicle update; code={}", vehicle.code(), e);
                throw e;
            }
        });
    }

    private Optional<VehicleEntity> findEntityByCode(String code) {
        return this.em
                .createQuery("select v from Vehicle v where v.code = :code", VehicleEntity.class)
                .setParameter("code", code)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void removeVehicle(final Vehicle vehicle) {
        findEntityByCode(vehicle.code())
                .ifPresentOrElse(
                        this::removeVehicleEntity,
                        () -> log.error("Vehicle not removed as it can not be found; code={}", vehicle.code()));
    }

    private void removeVehicleEntity(final VehicleEntity entity) {
        try {
            em.remove(entity);
            log.debug("Vehicle removed; code={}", entity.getCode());
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            log.error("Error removing vehicle; code={}", entity.getCode(), e);
            throw e;
        }
    }
}
