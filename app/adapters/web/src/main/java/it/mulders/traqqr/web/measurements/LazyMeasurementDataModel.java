package it.mulders.traqqr.web.measurements;

import it.mulders.traqqr.domain.measurements.Measurement;
import it.mulders.traqqr.domain.measurements.MeasurementRepository;
import it.mulders.traqqr.domain.shared.Pagination;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

public class LazyMeasurementDataModel extends LazyDataModel<Measurement> {
    private final MeasurementRepository measurementRepository;
    private final Vehicle selectedVehicle;

    public LazyMeasurementDataModel(final MeasurementRepository repository, final Vehicle selectedVehicle) {
        this.measurementRepository = repository;
        this.selectedVehicle = selectedVehicle;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) this.measurementRepository.countByVehicle(selectedVehicle);
    }

    @Override
    public List<Measurement> load(
            int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        var pagination = new Pagination(first, pageSize);
        return List.copyOf(this.measurementRepository.findByVehicle(selectedVehicle, pagination));
    }

    @Override
    public String getRowKey(Measurement measurement) {
        return measurement.id().toString();
    }
}
