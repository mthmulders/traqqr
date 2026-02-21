package it.mulders.traqqr.web.security;

import jakarta.inject.Named;
import java.util.Map;

@Named
public class OidcConfig {
    private final String callbackUrl;
    private final String clientId;
    private final String clientSecret;

    public OidcConfig() {
        this(System.getenv());
    }

    OidcConfig(final Map<String, String> environment) {
        this.callbackUrl = environment.get("OPENID_CALLBACK_URL");
        this.clientId = environment.get("OPENID_CLIENT_ID");
        this.clientSecret = environment.get("OPENID_CLIENT_SECRET");
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
