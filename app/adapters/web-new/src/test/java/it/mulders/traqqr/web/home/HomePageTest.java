package it.mulders.traqqr.web.home;

import it.mulders.traqqr.web.AbstractMvcPageTest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HomePageTest extends AbstractMvcPageTest {
    private final HomePage page = new HomePage();

    @Test
    void should_return_dashboard_view() {
        // Act
        var response = page.show();

        // Assert
        assertThat(response).hasStatus(200).hasViewName("home.jsp");
    }
}
