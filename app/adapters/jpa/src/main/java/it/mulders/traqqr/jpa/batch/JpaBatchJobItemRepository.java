package it.mulders.traqqr.jpa.batch;

import static java.util.stream.Collectors.toMap;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.spi.BatchJobItemRepository;
import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.shared.Identifiable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JpaBatchJobItemRepository implements BatchJobItemRepository {
    private static final Logger log = LoggerFactory.getLogger(JpaBatchJobItemRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private BatchJobItemMapper mapper;

    @Inject
    private MeasurementRepository measurementRepository;

    public JpaBatchJobItemRepository() {}

    protected JpaBatchJobItemRepository(
            final EntityManager em,
            final BatchJobItemMapper mapper,
            final MeasurementRepository measurementRepository) {
        this.em = em;
        this.mapper = mapper;
        this.measurementRepository = measurementRepository;
    }

    record BatchJobItemStatusLongTuple(BatchJobItemStatus status, Long number) {
        public static BatchJobItemStatusLongTuple from(StringLongTuple tuple) {
            return new BatchJobItemStatusLongTuple(BatchJobItemStatus.valueOf(tuple.string()), tuple.number());
        }
    }

    @Override
    public Map<BatchJobItemStatus, Long> findItemCountsForJobInstanceAndExecution(Long instanceId, Long executionId) {
        Objects.requireNonNull(instanceId);
        Objects.requireNonNull(executionId);

        var query = this.em
                .createQuery("""
                        select new it.mulders.traqqr.jpa.batch.StringLongTuple(ji.itemStatus, count(ji))
                        from JobItem ji
                        where ji.instanceId = :instanceId
                        and ji.executionId = :executionId
                        group by ji.itemStatus
                        """, StringLongTuple.class)
                .setParameter("instanceId", instanceId)
                .setParameter("executionId", executionId);

        try {
            return query.getResultStream()
                    .map(BatchJobItemStatusLongTuple::from)
                    .collect(toMap(BatchJobItemStatusLongTuple::status, BatchJobItemStatusLongTuple::number));
        } catch (NoResultException nre) {
            log.info("Batch Process not found; instance_id={}, execution_id={}", instanceId, executionId);
            return Map.of();
        }
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void save(BatchJobItem<Identifiable> item) {
        Objects.requireNonNull(item);
        var entity = this.mapper.toEntity(item);
        entity.setItemId(item.item().id());
        storeItemEntity(item.item());

        try {
            em.persist(entity);
        } catch (PersistenceException e) {
            log.error("Database error during Batch Job item storage; id={}", entity.getId(), e);
            throw e;
        }
    }

    private void storeItemEntity(Identifiable item) {
        switch (item) {
            case Measurement measurement -> measurementRepository.save(measurement);
            default -> throw new IllegalStateException("Unexpected type of item in Batch Job: " + item.getClass());
        }
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void saveAll(Collection<BatchJobItem<Identifiable>> items) {
        items.forEach(this::save);
    }
}
