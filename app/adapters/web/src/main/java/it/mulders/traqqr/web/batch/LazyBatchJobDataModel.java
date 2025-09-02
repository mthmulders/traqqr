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
        if (filterBy.containsKey("type")) {
            return (int) this.batchJobRepository.count();
        } else {
            return 0;
        }
    }

    @Override
    public List<BatchJob> load(
            int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        var pagination = new Pagination(first, pageSize);
        if (filterBy.containsKey("type")) {
            var batchJobType =
                    BatchJobType.valueOf((String) filterBy.get("type").getFilterValue());
            return List.copyOf(this.batchJobRepository.findPaginated(batchJobType, pagination));
        } else {
            return List.of();
        }
    }

    @Override
    public String getRowKey(BatchJob batchJob) {
        return batchJob.getId().toString();
    }
}
