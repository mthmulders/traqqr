package it.mulders.traqqr.domain.shared;

import net.jqwik.api.Example;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RandomStringUtilsTest implements WithAssertions {
    @Property
    void random_identifier_does_not_contain_uppercase_characters(@ForAll @IntRange(max = 1024) final int length) {
        var string = RandomStringUtils.generateRandomIdentifier(length);
        assertThat(string.toLowerCase()).isEqualTo(string);
    }

    @Property
    void random_identifier_generates_string_of_specified_length(@ForAll @IntRange(max = 1024) final int length) {
        assertThat(RandomStringUtils.generateRandomIdentifier(length).length()).isEqualTo(length);
    }

    @Property
    void random_identifier_generates_string_with_sufficient_randomness(
            @ForAll @IntRange(min = 70, max = 255) final int length) {
        var result = RandomStringUtils.generateRandomIdentifier(length);
        var differentCharCount = result.chars().distinct().count();

        assertThat(differentCharCount)
                // Implementation detail: there are 35 characters to choose from.
                // The randomness is not guaranteed to select all of them, but we can
                // assume that at least half of them will be selected when we ask for
                // a random String of 70 chars or more.
                .isBetween(17L, 35L);
    }

    @Example
    void random_identifier_does_not_generate_string_longer_than_1024_chars(
            @ForAll @IntRange(min = 1025) final int length) {
        assertThatThrownBy(() -> RandomStringUtils.generateRandomIdentifier(length))
                .message()
                .contains("1024");
    }

    @Property
    void random_alpha_string_does_not_contain_uppercase_characters(@ForAll @IntRange(max = 1024) final int length) {
        var string = RandomStringUtils.generateRandomAlphaString(length);
        assertThat(string.toLowerCase()).isEqualTo(string);
    }

    @Property
    void random_alpha_string_does_not_contain_digits(@ForAll @IntRange(max = 1024) final int length) {
        var string = RandomStringUtils.generateRandomAlphaString(length);
        assertThat(string).doesNotMatch(".*\\d.*");
    }

    @Property
    void random_alpha_string_generates_string_of_specified_length(@ForAll @IntRange(max = 1024) final int length) {
        assertThat(RandomStringUtils.generateRandomAlphaString(length).length()).isEqualTo(length);
    }

    @Property
    void random_alpha_string_generates_string_with_sufficient_randomness(
            @ForAll @IntRange(min = 70, max = 255) final int length) {
        var result = RandomStringUtils.generateRandomAlphaString(length);
        var differentCharCount = result.chars().distinct().count();

        assertThat(differentCharCount)
                // Implementation detail: there are 26 characters to choose from.
                // The randomness is not guaranteed to select all of them, but we can
                // assume that at least half of them will be selected when we ask for
                // a random String of 70 chars or more.
                .isBetween(13L, 26L);
    }

    @Example
    void random_alpha_string_does_not_generate_string_longer_than_1024_chars(
            @ForAll @IntRange(min = 1025) final int length) {
        assertThatThrownBy(() -> RandomStringUtils.generateRandomAlphaString(length))
                .message()
                .contains("1024");
    }
}
