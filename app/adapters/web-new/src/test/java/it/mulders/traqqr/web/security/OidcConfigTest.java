package it.mulders.traqqr.web.security;

import java.util.Map;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OidcConfigTest implements WithAssertions {
    @Test
    void should_read_callbackUrl_from_environment_variable() {
        var environment = Map.of("OPENID_CALLBACK_URL", "http://localhost:8080/openid/callback");
        assertThat(new OidcConfig(environment).getCallbackUrl()).isEqualTo("http://localhost:8080/openid/callback");
    }

    @Test
    void should_read_clientId_from_environment_variable() {
        var environment = Map.of("OPENID_CLIENT_ID", "some-client-id");
        assertThat(new OidcConfig(environment).getClientId()).isEqualTo("some-client-id");
    }

    @Test
    void should_read_clientSecret_from_environment_variable() {
        var environment = Map.of("OPENID_CLIENT_SECRET", "some-client-secret");
        assertThat(new OidcConfig(environment).getClientSecret()).isEqualTo("some-client-secret");
    }
}
