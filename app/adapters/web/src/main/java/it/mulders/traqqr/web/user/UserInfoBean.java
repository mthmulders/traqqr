package it.mulders.traqqr.web.user;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;

import java.io.Serializable;

@Named
@SessionScoped
public class UserInfoBean implements Serializable {
    private final String displayName;
    private final String profilePictureUrl;

    @Inject
    public UserInfoBean(final SecurityContext securityContext,
                        final OpenIdContext openIdContext) {
        var claims = openIdContext.getClaims();
        this.displayName = claims.getName().orElse("unknown user");
        this.profilePictureUrl = claims.getPicture().orElse(null);
    }

    public String getUsername() {// name, picture, email
        return displayName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }
}
