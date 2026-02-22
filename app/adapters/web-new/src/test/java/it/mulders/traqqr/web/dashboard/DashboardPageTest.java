package it.mulders.traqqr.web.dashboard;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.system.api.UserStatisticsService;
import it.mulders.traqqr.domain.user.Owner;
import jakarta.mvc.Models;
import jakarta.ws.rs.core.Response;
import org.assertj.core.api.WithAssertions;
import org.eclipse.krazo.core.ModelsImpl;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DashboardPageTest implements WithAssertions {
    private final Models models = new ModelsImpl();
    private final Owner owner = createOwner();
    private final UserStatisticsService userStatisticsService = new UserStatisticsService() {

        @Override
        public long countMeasurements(Owner o) {
            return o == owner ? 42 : 0;
        }
    };

    private final DashboardPage page = new DashboardPage(models, owner, userStatisticsService);

    @Test
    void should_return_dashboard_view() {
        // Act
        var response = page.show();

        // Assert
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.readEntity(String.class)).isEqualTo("dashboard/index.jsp");
    }

    @Test
    void should_load_owners_statistics() {
        // Arrange

        // Act
        page.show();

        // Assert
        assertThat(models.get("numMeasurements")).asInstanceOf(LONG).isEqualTo(42);
    }

    private Owner createOwner() {
        return new Owner() {
            private final String code = RandomStringUtils.generateRandomAlphaString(18);

            @Override
            public String code() {
                return code;
            }

            @Override
            public String displayName() {
                return "";
            }

            @Override
            public String profilePictureUrl() {
                return "";
            }
        };
    }
}
