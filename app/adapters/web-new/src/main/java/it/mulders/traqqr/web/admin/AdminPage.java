package it.mulders.traqqr.web.admin;

import jakarta.enterprise.context.RequestScoped;
import jakarta.mvc.Controller;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Controller
@Path("/secure/admin")
@RequestScoped
public class AdminPage {
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response show() {
        return Response.ok("admin/index.jsp").build();
    }
}
