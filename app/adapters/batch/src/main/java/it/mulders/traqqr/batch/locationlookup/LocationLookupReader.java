package it.mulders.traqqr.batch.locationlookup;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.spi.MeasurementRepository;
import jakarta.batch.api.chunk.AbstractItemReader;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Iterator;

@Dependent
@Named("locationLookupReader")
public class LocationLookupReader extends AbstractItemReader {
    // Components
    private final MeasurementRepository measurementRepository;

    // Data
    private Iterator<Measurement> measurements;

    @Inject
    public LocationLookupReader(final MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    public void open(Serializable checkpoint) throws Exception {
        this.measurements = measurementRepository
                .findOldestMeasurementsWithoutLocationDescription()
                .iterator();
    }

    @Override
    public Object readItem() throws Exception {
        if (!this.measurements.hasNext()) {
            return null;
        }
        return this.measurements.next();
    }
}
