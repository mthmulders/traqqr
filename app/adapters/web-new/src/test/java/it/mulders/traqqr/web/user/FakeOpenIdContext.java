package it.mulders.traqqr.web.user;

import jakarta.json.JsonObject;
import jakarta.security.enterprise.identitystore.openid.AccessToken;
import jakarta.security.enterprise.identitystore.openid.Claims;
import jakarta.security.enterprise.identitystore.openid.IdentityToken;
import jakarta.security.enterprise.identitystore.openid.OpenIdClaims;
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.security.enterprise.identitystore.openid.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class FakeOpenIdContext {
    public static OpenIdContext createOpenIdContext(String subject, String nameClaim, String pictureClaim) {
        return new OpenIdContext() {

            @Override
            public String getSubject() {
                return subject;
            }

            @Override
            public String getTokenType() {
                return "";
            }

            @Override
            public AccessToken getAccessToken() {
                return null;
            }

            @Override
            public IdentityToken getIdentityToken() {
                return null;
            }

            @Override
            public Optional<RefreshToken> getRefreshToken() {
                return Optional.empty();
            }

            @Override
            public Optional<Long> getExpiresIn() {
                return Optional.empty();
            }

            @Override
            public JsonObject getClaimsJson() {
                return null;
            }

            @Override
            public OpenIdClaims getClaims() {
                return new OpenIdClaims() {
                    @Override
                    public Optional<String> getStringClaim(String name) {
                        return switch (name) {
                            case "picture" -> Optional.ofNullable(pictureClaim);
                            case "name" -> Optional.ofNullable(nameClaim);
                            default -> Optional.empty();
                        };
                    }

                    @Override
                    public Optional<Instant> getNumericDateClaim(String name) {
                        return Optional.empty();
                    }

                    @Override
                    public List<String> getArrayStringClaim(String name) {
                        return List.of();
                    }

                    @Override
                    public OptionalInt getIntClaim(String name) {
                        return OptionalInt.empty();
                    }

                    @Override
                    public OptionalLong getLongClaim(String name) {
                        return OptionalLong.empty();
                    }

                    @Override
                    public OptionalDouble getDoubleClaim(String name) {
                        return OptionalDouble.empty();
                    }

                    @Override
                    public Optional<Claims> getNested(String name) {
                        return Optional.empty();
                    }
                };
            }

            @Override
            public JsonObject getProviderMetadata() {
                return null;
            }

            @Override
            public <T> Optional<T> getStoredValue(
                    HttpServletRequest request, HttpServletResponse response, String key) {
                return Optional.empty();
            }
        };
    }
}
