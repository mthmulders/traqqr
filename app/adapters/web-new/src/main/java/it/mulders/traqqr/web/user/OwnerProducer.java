package it.mulders.traqqr.web.user;

import it.mulders.traqqr.domain.user.Owner;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import java.io.Serializable;

@ApplicationScoped
public class OwnerProducer {
    public record OpenIdClaimsBackedOwner(String code, String displayName, String profilePictureUrl)
            implements Owner, Serializable {}

    @Produces
    @SessionScoped
    public OpenIdClaimsBackedOwner create(final OpenIdContext openIdContext) {
        var code = openIdContext.getSubject();
        var claims = openIdContext.getClaims();
        var displayName = claims.getName().orElse("unknown user");
        var profilePictureUrl = claims.getPicture().orElse(null);

        return new OpenIdClaimsBackedOwner(code, displayName, profilePictureUrl);
    }
}
