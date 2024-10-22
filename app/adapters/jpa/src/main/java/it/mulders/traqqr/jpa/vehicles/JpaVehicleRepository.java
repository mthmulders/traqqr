package it.mulders.traqqr.jpa.vehicles;

import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import it.mulders.traqqr.domain.vehicles.VehicleRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TransactionRequiredException;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JpaVehicleRepository implements VehicleRepository {
    private static final Logger log = LoggerFactory.getLogger(JpaVehicleRepository.class);
    private final EntityManager em;
    private final VehicleMapper mapper;

    @Inject
    public JpaVehicleRepository(final EntityManager em, final VehicleMapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public Optional<Vehicle> findByCode(final String code) {
        var query = this.em.createQuery("select v from VehicleEntity v where v.code = :code", VehicleEntity.class);
        return query.setParameter("code", code)
                .getResultStream()
                .map(mapper::vehicleEntityToVehicle)
                .findAny();
    }

    @Override
    public Collection<Vehicle> findByOwner(Owner owner) {
        var query =
                this.em.createQuery("select v from VehicleEntity v where v.ownerId = :ownerId", VehicleEntity.class);
        return query.setParameter("ownerId", owner.code())
                .getResultStream()
                .map(mapper::vehicleEntityToVehicle)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void save(Vehicle vehicle) {
        var entity = this.mapper.vehicleToVehicleEntity(vehicle);
        entity.setId(UUID.randomUUID());
        try {
            em.joinTransaction();
            em.persist(entity);
            em.flush();
            log.debug("Vehicle saved; code={}", vehicle.code());
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            log.error("Error registering vehicle; code={}", vehicle.code(), e);
            throw e;
        }
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void update(Vehicle vehicle) {
        this.em
                .createQuery("select v from VehicleEntity v where v.code = :code", VehicleEntity.class)
                .setParameter("code", vehicle.code())
                .getResultStream()
                .findAny()
                .ifPresent(vehicleEntity -> {
                    vehicleEntity.setDescription(vehicle.description());

                    for (var authorisation : vehicle.authorisations()) {
                        vehicleEntity.getAuthorisations().stream()
                                .filter(it -> it.getHashedKey().equals(authorisation.getHashedKey()))
                                .findAny()
                                .ifPresentOrElse(
                                        existingAuthorisationEntity -> {
                                            existingAuthorisationEntity.setInvalidatedAt(
                                                    authorisation.getInvalidatedAt());
                                        },
                                        () -> {
                                            var authorisationEntity =
                                                    mapper.authorisationToAuthorisationEntity(authorisation);
                                            authorisationEntity.setVehicle(vehicleEntity);
                                            vehicleEntity.getAuthorisations().add(authorisationEntity);
                                        });
                    }

                    try {
                        em.joinTransaction();
                        em.merge(vehicleEntity);
                        em.flush();
                        log.debug("Vehicle updated; code={}", vehicle.code());
                    } catch (PersistenceException e) {
                        log.error("Database error during vehicle update; code={}", vehicle.code(), e);
                        throw e;
                    }
                });
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void removeVehicle(final Vehicle vehicle) {
        try {
            em.joinTransaction();
            var itemsUpdated = this.em
                    .createQuery("delete from VehicleEntity v where v.code = :code", VehicleEntity.class)
                    .setParameter("code", vehicle.code())
                    .executeUpdate();
            if (itemsUpdated == 0) {
                log.warn("Removing vehicle failed; code={}", vehicle.code());
            } else if (itemsUpdated > 1) {
                log.warn("Removing vehicle touched too many items; code={}, count={}", vehicle.code(), itemsUpdated);
            }
            em.flush();
            log.debug("Vehicle removed; code={}", vehicle.code());
        } catch (IllegalArgumentException | TransactionRequiredException e) {
            log.error("Error removing vehicle; code={}", vehicle.code(), e);
            throw e;
        }
    }
}
