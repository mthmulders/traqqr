package it.mulders.traqqr.web.servlet;

import jakarta.servlet.ServletException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletConfig;

import java.io.IOException;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WebjarsServletTest implements WithAssertions {
    private final WebjarsServlet servlet = new WebjarsServlet();

    @BeforeEach
    void prepareServletConfig() throws ServletException {
        var servletConfig = new MockServletConfig();
        servlet.init(servletConfig);
    }

    @Test
    void should_return_Forbidden_when_requesting_directory() throws ServletException, IOException {
        // Arrange
        var request = new MockHttpServletRequest("GET", "/webjars/");
        var response = new MockHttpServletResponse();

        // Act
        servlet.doGet(request, response);

        // Assert
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void should_return_NotFound_when_requesting_non_existing_resource() throws ServletException, IOException {
        // Arrange
        var request = new MockHttpServletRequest("GET", "/webjars/non-existing.txt");
        var response = new MockHttpServletResponse();

        // Act
        servlet.doGet(request, response);

        // Assert
        assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    void should_return_contents_when_requesting_existing_resource() throws ServletException, IOException {
        // Arrange
        var request = new MockHttpServletRequest("GET", "/webjars/example.txt");
        var response = new MockHttpServletResponse();

        // Act
        servlet.doGet(request, response);

        // Assert
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEqualTo("Hello, world");
    }
}