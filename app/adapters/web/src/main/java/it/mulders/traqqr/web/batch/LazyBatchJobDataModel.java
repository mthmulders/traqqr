package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobRepository;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.shared.Pagination;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

public class LazyBatchJobDataModel extends LazyDataModel<BatchJob> {
    private final BatchJobRepository batchJobRepository;

    public LazyBatchJobDataModel(final BatchJobRepository batchJobRepository) {
        this.batchJobRepository = batchJobRepository;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return (int) this.batchJobRepository.count();
    }

    @Override
    public List<BatchJob> load(
            int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        var pagination = new Pagination(first, pageSize);
        var jobs = this.batchJobRepository.findPaginated(pagination);
        return List.copyOf(jobs);
    }

    @Override
    public String getRowKey(BatchJob batchJob) {
        return batchJob.getId().toString();
    }
}
