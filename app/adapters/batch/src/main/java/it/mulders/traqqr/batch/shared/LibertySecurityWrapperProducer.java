package it.mulders.traqqr.batch.shared;

import it.mulders.traqqr.libertysecurity.LibertySecurityWrapper;
import it.mulders.traqqr.libertysecurity.SecurityWrapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class LibertySecurityWrapperProducer {
    @ApplicationScoped
    @Produces
    public SecurityWrapper wrapper() {
        return new LibertySecurityWrapper("scheduler", "correct-horse-battery-staple");
    }
}
