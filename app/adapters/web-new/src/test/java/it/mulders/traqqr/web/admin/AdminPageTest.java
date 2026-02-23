package it.mulders.traqqr.web.admin;

import it.mulders.traqqr.web.AbstractMvcPageTest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminPageTest extends AbstractMvcPageTest {
    private final AdminPage page = new AdminPage();

    @Test
    void should_return_dashboard_view() {
        // Act
        var response = page.show();

        // Assert
        assertThat(response).hasStatus(200).hasViewName("admin/index.jsp");
    }
}
