package it.mulders.traqqr.jpa.batch;

import static java.util.stream.Collectors.toMap;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.spi.BatchJobItemRepository;
import it.mulders.traqqr.domain.measurements.Measurement;
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
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class JpaBatchJobItemRepository implements BatchJobItemRepository {
    private static final Logger log = LoggerFactory.getLogger(JpaBatchJobItemRepository.class);

    @PersistenceContext
    private EntityManager em;

    @Inject
    private BatchJobItemMapper mapper;

    public JpaBatchJobItemRepository() {}

    protected JpaBatchJobItemRepository(final EntityManager em, final BatchJobItemMapper mapper) {
        this.em = em;
        this.mapper = mapper;
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
                .createQuery(
                        """
                        select new it.mulders.traqqr.jpa.batch.StringLongTuple(ji.itemStatus, count(ji))
                        from JobItem ji
                        where ji.instanceId = :instanceId
                        and ji.executionId = :executionId
                        group by ji.itemStatus
                        """,
                        StringLongTuple.class)
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
    public void save(BatchJobItem<?> item) {
        Objects.requireNonNull(item);
        var entity = this.mapper.toEntity(item);
        entity.setItemId(extractItemId(item.item()));

        try {
            em.persist(entity);
        } catch (PersistenceException e) {
            log.error("Database error during Batch Job item storage; id={}", entity.getId(), e);
            throw e;
        }
    }

    @Override
    @Transactional(Transactional.TxType.MANDATORY)
    public void saveAll(Collection<BatchJobItem<?>> items) {
        items.forEach(this::save);
    }

    private UUID extractItemId(Object item) {
        return switch (item) {
            case Measurement measurement -> measurement.id();
            default -> throw new IllegalStateException(
                    "Unexpected item type: " + item.getClass().getName());
        };
    }
}
