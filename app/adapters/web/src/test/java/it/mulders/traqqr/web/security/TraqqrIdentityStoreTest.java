package it.mulders.traqqr.web.security;

import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore.ValidationType;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TraqqrIdentityStoreTest implements WithAssertions {
    private final TraqqrIdentityStore identityStore = new TraqqrIdentityStore();

    @Test
    void should_indicate_it_can_provide_groups() {
        assertThat(identityStore.validationTypes()).containsOnly(ValidationType.PROVIDE_GROUPS);
    }

    @Test
    void should_always_assign_role_user() {
        var input = new CredentialValidationResult("caller principal name");
        assertThat(identityStore.getCallerGroups(input)).containsExactly("user");
    }
}