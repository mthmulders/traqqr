package it.mulders.traqqr.web.user;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@Named
@SessionScoped
public class SessionBean implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(SessionBean.class);

    private final OpenIdContext openIdContext;

    @Inject
    public SessionBean(final OpenIdContext openIdContext) {
        this.openIdContext = openIdContext;
    }

    public void logout(HttpServletRequest request) throws ServletException {
        log.info("Logging out user; subject={}", openIdContext.getSubject());
        request.logout();
    }
}
