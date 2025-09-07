package it.mulders.traqqr.batch.shared;

import it.mulders.traqqr.libertysecurity.LibertySecurityWrapper;
import it.mulders.traqqr.libertysecurity.SecurityWrapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class LibertySecurityWrapperProducer {
    private static final Logger logger = LoggerFactory.getLogger(LibertySecurityWrapperProducer.class);

    @ApplicationScoped
    @Produces
    public SecurityWrapper wrapper() {
        var password = System.getenv("SCHEDULER_USER_PASSWORD");
        if (password == null || password.isEmpty()) {
            logger.warn("Configuring LibertySecurityWrapper; environment variable SCHEDULER_USER_PASSWORD not set");
        } else {
            logger.info("Configuring LibertySecurityWrapper; using password from environment variable");
        }

        return new LibertySecurityWrapper("scheduler", password);
    }
}
