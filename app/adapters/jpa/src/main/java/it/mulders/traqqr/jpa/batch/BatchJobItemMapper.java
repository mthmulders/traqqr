package it.mulders.traqqr.jpa.batch;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.NullValueMappingStrategy.RETURN_DEFAULT;

import it.mulders.traqqr.domain.batch.BatchJobItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.JAKARTA_CDI,
        nullValueIterableMappingStrategy = RETURN_DEFAULT,
        injectionStrategy = CONSTRUCTOR)
public interface BatchJobItemMapper {
    @Mapping(target = "batchJobType", source = "batchJob.type")
    @Mapping(target = "itemStatus", source = "status")
    @Mapping(target = "executionId", source = "batchJob.executionId")
    @Mapping(target = "instanceId", source = "batchJob.instanceId")
    JobItemEntity toEntity(final BatchJobItem<?> batchJobItem);
}
