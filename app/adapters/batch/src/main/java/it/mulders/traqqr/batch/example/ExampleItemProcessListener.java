package it.mulders.traqqr.batch.example;

import it.mulders.traqqr.batch.shared.TraqqrItemProcessListener;
import it.mulders.traqqr.domain.measurements.Measurement;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;

@Dependent
@Named("exampleItemProcessListener")
public class ExampleItemProcessListener extends TraqqrItemProcessListener<Measurement> {
    public ExampleItemProcessListener() {
        super("measurement_id");
    }

    @Override
    protected String extractId(Measurement item) {
        return item.id().toString();
    }
}
