package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Iterator;

@Dependent
@Named("exampleReader")
public class ExampleReader extends AbstractItemReader {
    // Components
    private final MeasurementRepository measurementRepository;

    // Data
    private Iterator<Measurement> measurements;

    @Inject
    public ExampleReader(final MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public void open(Serializable checkpoint) {
        this.measurements =
                this.measurementRepository.exampleStreamingFindForBatchJob().iterator();
    }

    @Override
    public Object readItem() {
        if (!this.measurements.hasNext()) {
            return null;
        }
        return this.measurements.next();
    }
}
