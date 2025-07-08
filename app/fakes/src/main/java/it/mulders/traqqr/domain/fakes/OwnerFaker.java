package it.mulders.traqqr.domain.fakes;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;

public class OwnerFaker {
    public static Owner createOwner() {
        var ownerId = RandomStringUtils.generateRandomAlphaString(12);
        return createOwner(ownerId);
    }

    public static Owner createOwner(String ownerId) {
        return new Owner() {

            @Override
            public String code() {
                return ownerId;
            }

            @Override
            public String displayName() {
                return "Fake Owner";
            }

            @Override
            public String profilePictureUrl() {
                return "n/a";
            }
        };
    }
}
