package it.mulders.traqqr.web.user;

import static it.mulders.traqqr.web.user.FakeOpenIdContext.createOpenIdContext;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OwnerProducerTest implements WithAssertions {
    private final OwnerProducer producer = new OwnerProducer();

    @Test
    void should_map_subject_to_code() {
        var context = createOpenIdContext("openid-issued-subject", null, null);
        var owner = producer.create(context);
        assertThat(owner.code()).isEqualTo("openid-issued-subject");
    }

    @Test
    void should_map_name_claim_to_displayName() {
        var context = createOpenIdContext(null, "John Doe", null);
        var owner = producer.create(context);
        assertThat(owner.displayName()).isEqualTo("John Doe");
    }

    @Test
    void should_map_picture_claim_to_profilePictureUrl() {
        var context = createOpenIdContext(null, null, "http://example.com/picture");
        var owner = producer.create(context);
        assertThat(owner.profilePictureUrl()).isEqualTo("http://example.com/picture");
    }
}
