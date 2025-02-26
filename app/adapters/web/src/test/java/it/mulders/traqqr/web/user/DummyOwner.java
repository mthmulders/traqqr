package it.mulders.traqqr.web.user;

import it.mulders.traqqr.domain.user.Owner;

public class DummyOwner implements Owner {
    private final String code;
    private final String displayName;
    private final String profilePictureUrl;

    public DummyOwner(String code, String displayName, String profilePictureUrl) {
        this.code = code;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public String profilePictureUrl() {
        return profilePictureUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String code;
        private String displayName;
        private String profilePictureUrl;

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder profilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
            return this;
        }

        public DummyOwner build() {
            return new DummyOwner(code, displayName, profilePictureUrl);
        }
    }
}
