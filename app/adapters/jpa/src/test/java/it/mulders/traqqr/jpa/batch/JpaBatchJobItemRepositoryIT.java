package it.mulders.traqqr.jpa.batch;

import static it.mulders.traqqr.domain.fakes.MeasurementFaker.createMeasurement;
import static it.mulders.traqqr.domain.fakes.VehicleFaker.createVehicle;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobItem;
import it.mulders.traqqr.domain.batch.BatchJobItemStatus;
import it.mulders.traqqr.domain.batch.BatchJobStatus;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.spi.BatchJobItemRepository;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import it.mulders.traqqr.domain.shared.Identifiable;
import it.mulders.traqqr.jpa.AbstractJpaRepositoryTest;
import it.mulders.traqqr.mem.measurements.InMemoryMeasurementRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.IntStream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class JpaBatchJobItemRepositoryIT extends AbstractJpaRepositoryTest<BatchJobItemRepository, JpaBatchJobItemRepository>
        implements WithAssertions {
    private final MeasurementRepository measurementRepository = new InMemoryMeasurementRepository();

    @BeforeEach
    void prepare() {
        prepare(em -> new JpaBatchJobItemRepository(em, batchJobItemMapper, measurementRepository));
    }

    @Test
    void should_find_BatchJobItem_counts_for_JobInstance_and_JobExecution() {
        var now = OffsetDateTime.now();
        var instanceId = 1L;
        var executionId = 1L;

        var vehicle = createVehicle();

        runTransactional(() -> {
            var vehicleEntity = vehicleMapper.vehicleToVehicleEntity(vehicle);
            entityManager.persist(vehicleEntity);

            var batchJob = new BatchJob(
                    now.minusMinutes(1),
                    now,
                    now,
                    BatchJobType.EXAMPLE,
                    BatchJobStatus.COMPLETED,
                    Map.of(
                            BatchJobItemStatus.PROCESSED, 10L,
                            BatchJobItemStatus.NO_PROCESSING_NECESSARY, 3L,
                            BatchJobItemStatus.FAILED, 2L),
                    instanceId,
                    executionId);

            Collection<BatchJobItem<Identifiable>> items = new ArrayList<>();

            IntStream.range(0, 10).mapToObj(i -> createMeasurement(vehicle)).forEach(measurement -> {
                var measurementEntity = measurementMapper.measurementToMeasurementEntity(measurement);
                measurementEntity.setVehicle(vehicleEntity);
                entityManager.persist(measurementEntity);

                items.add(new BatchJobItem<>(batchJob, BatchJobItemStatus.PROCESSED, measurement));
            });

            IntStream.range(0, 3).mapToObj(i -> createMeasurement(vehicle)).forEach(measurement -> {
                var measurementEntity = measurementMapper.measurementToMeasurementEntity(measurement);
                measurementEntity.setVehicle(vehicleEntity);
                entityManager.persist(measurementEntity);

                items.add(new BatchJobItem<>(batchJob, BatchJobItemStatus.NO_PROCESSING_NECESSARY, measurement));
            });

            IntStream.range(0, 2).mapToObj(i -> createMeasurement(vehicle)).forEach(measurement -> {
                var measurementEntity = measurementMapper.measurementToMeasurementEntity(measurement);
                measurementEntity.setVehicle(vehicleEntity);
                entityManager.persist(measurementEntity);

                items.add(new BatchJobItem<>(batchJob, BatchJobItemStatus.FAILED, measurement));
            });

            repository.saveAll(items);
        });

        var itemCountsForJobInstanceAndExecution =
                repository.findItemCountsForJobInstanceAndExecution(instanceId, executionId);

        assertThat(itemCountsForJobInstanceAndExecution)
                .hasSize(3)
                .containsEntry(BatchJobItemStatus.PROCESSED, 10L)
                .containsEntry(BatchJobItemStatus.NO_PROCESSING_NECESSARY, 3L)
                .containsEntry(BatchJobItemStatus.FAILED, 2L);
    }

    @Test
    void should_store_batch_job_item_with_related_item() {
        // Arrange
        var instanceId = 48932L;
        var vehicle = createVehicle();
        var batchJob = new BatchJob(
                OffsetDateTime.now().minusMinutes(1),
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                BatchJobType.LOCATION_LOOKUP,
                BatchJobStatus.COMPLETED,
                Map.of(BatchJobItemStatus.PROCESSED, 1L),
                instanceId,
                0L);
        var item = new BatchJobItem<Identifiable>(batchJob, BatchJobItemStatus.PROCESSED, createMeasurement(vehicle));

        // Act
        runTransactional(() -> repository.save(item));

        // Assert
        var allItems = entityManager
                .createQuery("select ji from JobItem ji", JobItemEntity.class)
                .getResultList();
        var measurements = measurementRepository.findByVehicle(vehicle);

        assertThat(allItems)
                .anySatisfy(jobItem -> assertThat(jobItem.getInstanceId()).isEqualTo(instanceId));
        assertThat(measurements)
                .anySatisfy(measurement -> assertThat(measurement.vehicle().equals(vehicle)));
    }
}
