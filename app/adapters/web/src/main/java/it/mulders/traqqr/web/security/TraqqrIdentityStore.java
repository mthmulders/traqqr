package it.mulders.traqqr.web.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import java.util.EnumSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class TraqqrIdentityStore implements IdentityStore {
    private static final Logger log = LoggerFactory.getLogger(TraqqrIdentityStore.class);

    @Override
    public Set<String> getCallerGroups(final CredentialValidationResult validationResult) {
        log.info("Assigning groups; caller_unique_id={}", validationResult.getCallerUniqueId());
        return Set.of("user");
    }

    @Override
    public Set<ValidationType> validationTypes() {
        return EnumSet.of(ValidationType.PROVIDE_GROUPS);
    }
}
