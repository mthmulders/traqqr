package it.mulders.traqqr.web.batch;

import it.mulders.traqqr.domain.batch.BatchJobType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@ApplicationScoped
@FacesConverter(value = "batchJobTypeConverter", managed = true)
@Named
public class BatchJobTypeConverter implements Converter<BatchJobType>, Serializable {
    @Inject
    public BatchJobTypeConverter() {}

    @Override
    public BatchJobType getAsObject(FacesContext facesContext, UIComponent uiComponent, String input) {
        if (input == null || input.trim().isEmpty()) {
            return null;
        }

        return BatchJobType.valueOf(input.trim().toUpperCase());
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, BatchJobType batchJobType) {
        return batchJobType == null ? null : batchJobType.name();
    }
}
