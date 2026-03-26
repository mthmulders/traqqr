package it.mulders.traqqr.domain.shared;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MutationsTest implements WithAssertions {

    @Test
    void should_overwrite_if_update_is_not_null() {
        var result = Mutations.overwrite("original").with("updated");
        assertThat(result).isEqualTo("updated");
    }

    @Test
    void should_keep_original_if_update_is_null() {
        var result = Mutations.overwrite("original").with(null);
        assertThat(result).isEqualTo("original");
    }

    @Test
    void should_return_null_if_both_value_and_fallback_are_null() {
        var result = Mutations.overwrite((Object) null).with(null);
        assertThat(result).isNull();
    }

    @Test
    void should_not_work_for_collections() {
        assertThatThrownBy(() -> Mutations.overwrite(emptyList())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Mutations.overwrite(emptySet())).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Mutations.overwrite(emptyMap())).isInstanceOf(IllegalArgumentException.class);
    }
}
