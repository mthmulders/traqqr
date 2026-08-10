package it.mulders.traqqr.web.krazo;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.mvc.MvcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

@RequestScoped
@Named("instantFormatter")
public class InstantFormatter implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(InstantFormatter.class);

    private static final DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder()
            .appendPattern("dd MMM uuuu, HH:mm:ss (O)");

    // Components
    private DateTimeFormatter formatter;

    public InstantFormatter() {
        this(Locale.ROOT);
    }

    @Inject
    public InstantFormatter(final MvcContext context) {
        this(context.getLocale());
    }

    private InstantFormatter(final Locale locale) {
        log.info("Instant formatter instantiated; locale={}", locale);
        formatter = formatterBuilder.toFormatter(locale);
    }

    public String format(OffsetDateTime value) {
        if (value == null) return "";
        return formatter.format(value);
    }
}
