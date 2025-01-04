package it.mulders.traqqr.domain.vehicles;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AuthorisationTest implements WithAssertions {

    @Test
    void should_expose_raw_key_after_creation() {
        var authorisation = Authorisation.generate();
        assertThat(authorisation.getRawKey()).isNotNull();
    }

    @Test
    void should_allow_verification() {
        var originalAuthorisation = Authorisation.generate();

        var verification = Authorisation.fromInput(originalAuthorisation.getRawKey());

        assertThat(verification.getHashedKey()).isEqualTo(originalAuthorisation.getHashedKey());
    }
}
