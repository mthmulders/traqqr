package it.mulders.traqqr.web.security;

import it.mulders.traqqr.web.AbstractMvcPageTest;
import jakarta.servlet.ServletException;
import java.net.URISyntaxException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LogoutPageTest extends AbstractMvcPageTest {
    private final LogoutPage page = new LogoutPage();

    @Test
    void should_return_dashboard_view() throws ServletException, URISyntaxException {
        // Arrange
        var request = new MockHttpServletRequest();

        // Act
        var response = page.logout(request);

        // Assert
        assertThat(response).hasStatus(303).hasHeader("Location", "/app");
    }
}
