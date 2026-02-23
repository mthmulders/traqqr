package it.mulders.traqqr.web.dashboard;

import it.mulders.traqqr.domain.system.api.UserStatisticsService;
import it.mulders.traqqr.domain.user.Owner;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Controller
@Path("/secure/dashboard")
@RequestScoped
public class DashboardPage {
    // Components
    private UserStatisticsService userStatisticsService;

    // Data
    private Models models;
    private Owner owner;

    public DashboardPage() {}

    @Inject
    public DashboardPage(Models models, Owner owner, UserStatisticsService userStatisticsService) {
        this.models = models;
        this.owner = owner;
        this.userStatisticsService = userStatisticsService;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response show() {
        models.put("numMeasurements", userStatisticsService.countMeasurements(owner));
        return Response.ok("dashboard/index.jsp").build();
    }
}
