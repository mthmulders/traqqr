package it.mulders.traqqr.web.user;

import static it.mulders.traqqr.web.user.FakeOpenIdContext.createOpenIdContext;

import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserInfoBeanTest implements WithAssertions {
    private final OpenIdContext openIdContext =
            createOpenIdContext("irrelevant", "John Doe", "http://example.com/picture");
    private final UserInfoBean bean = new UserInfoBean(openIdContext);

    @Test
    void should_expose_users_display_name() {
        assertThat(bean.getUsername()).isEqualTo("John Doe");
    }

    @Test
    void should_expose_users_profile_picture_url() {
        assertThat(bean.getProfilePictureUrl()).isEqualTo("http://example.com/picture");
    }
}
