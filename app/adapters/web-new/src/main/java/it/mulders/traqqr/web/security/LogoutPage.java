package it.mulders.traqqr.web.security;

import jakarta.enterprise.context.RequestScoped;
import jakarta.mvc.Controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
@Path("/secure/logout")
@RequestScoped
public class LogoutPage {
    @GET
    public Response logout(@Context HttpServletRequest request) throws ServletException, URISyntaxException {
        request.logout();
        return Response.seeOther(new URI("/app")).build();
    }
}
