package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJob;
import it.mulders.traqqr.domain.batch.BatchJobType;
import it.mulders.traqqr.domain.batch.spi.BatchJobRepository;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

public class LazyLatestBatchJobRunsDataModel extends LazyDataModel<BatchJob> {
    private final BatchJobRepository batchJobRepository;

    public LazyLatestBatchJobRunsDataModel(final BatchJobRepository batchJobRepository) {
        this.batchJobRepository = batchJobRepository;
    }

    @Override
    public int count(Map<String, FilterMeta> map) {
        return BatchJobType.values().length;
    }

    @Override
    public List<BatchJob> load(
            int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        return batchJobRepository.findLatestRunsPerBatchJobType();
    }
}
