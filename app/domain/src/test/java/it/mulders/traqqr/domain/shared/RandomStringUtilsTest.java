package it.mulders.traqqr.domain.shared;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.assertj.core.api.WithAssertions;

class RandomStringUtilsTest implements WithAssertions {
    @Property
    void does_not_contain_uppercase_characters(@ForAll @IntRange(max = 1024) final int length) {
        var string = RandomStringUtils.generateRandomIdentifier(length);
        assertThat(string.toLowerCase()).isEqualTo(string);
    }

    @Property
    void generates_string_of_specified_length(@ForAll @IntRange(max = 1024) final int length) {
        assertThat(RandomStringUtils.generateRandomIdentifier(length).length()).isEqualTo(length);
    }

    @Example
    void does_not_generate_string_longer_than_1024_chars(@ForAll @IntRange(min = 1025) final int length) {
        assertThatThrownBy(() -> RandomStringUtils.generateRandomIdentifier(length))
                .message()
                .contains("1024");
    }
}
