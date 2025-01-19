package it.mulders.traqqr.web.faces;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@FacesConverter("traqqr.MeasurementTimestampConverter")
public class MeasurementTimestampConverter implements Converter<OffsetDateTime> {
    private static final DateTimeFormatter FORMAT = new DateTimeFormatterBuilder()
            .appendPattern("dd MMM uuuu, HH:mm.ss")
            .toFormatter(Locale.ROOT);

    @Override
    public OffsetDateTime getAsObject(FacesContext context, UIComponent component, String value) {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
        if (value == null) {
            return null;
        }

        return OffsetDateTime.parse(value, FORMAT);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, OffsetDateTime value) {
        if (value == null) {
            return "";
        }

        return value.format(FORMAT);
    }
}
