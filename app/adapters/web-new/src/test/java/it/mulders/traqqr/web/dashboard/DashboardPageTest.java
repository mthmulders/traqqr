package it.mulders.traqqr.web.dashboard;

import it.mulders.traqqr.domain.system.api.UserStatisticsService;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.web.AbstractMvcPageTest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DashboardPageTest extends AbstractMvcPageTest {
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
        assertThat(response).hasStatus(200).hasViewName("dashboard/index.jsp");
    }

    @Test
    void should_load_owners_statistics() {
        // Arrange

        // Act
        page.show();

        // Assert
        assertThat(models.get("numMeasurements")).asInstanceOf(LONG).isEqualTo(42);
    }
}
