package it.mulders.traqqr.web.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@ApplicationScoped
@Named("oidcConfig")
public class OidcConfig {
    private final String clientId;
    private final String clientSecret;
    private final String callbackUrl;

    public OidcConfig() {
        var environment = System.getenv();
        this.clientId = environment.get("OPENID_CLIENT_ID");
        this.clientSecret = environment.get("OPENID_CLIENT_SECRET");
        this.callbackUrl = environment.get("OPENID_CALLBACK_URL");
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }
}
