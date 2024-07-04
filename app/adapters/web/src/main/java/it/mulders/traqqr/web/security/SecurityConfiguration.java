package it.mulders.traqqr.web.security;

import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.ClaimsDefinition;
import jakarta.security.enterprise.authentication.mechanism.http.openid.LogoutDefinition;

@OpenIdAuthenticationMechanismDefinition(
        claimsDefinition = @ClaimsDefinition(
                callerNameClaim = "sub"
        ),
        clientId = "${oidcConfig.clientId}",
        clientSecret = "${oidcConfig.clientSecret}",
        logout = @LogoutDefinition(
                redirectURI = "${baseURL}"
        ),
        providerURI = "https://accounts.google.com/.well-known/openid-configuration",
        redirectURI = "${oidcConfig.callbackUrl}",
        redirectToOriginalResource = true,
        useNonce = true,
        useSession = true
)
public class SecurityConfiguration {
}
