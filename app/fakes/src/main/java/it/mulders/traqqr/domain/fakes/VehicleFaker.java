package it.mulders.traqqr.domain.fakes;

import it.mulders.traqqr.domain.shared.RandomStringUtils;
import it.mulders.traqqr.domain.user.Owner;
import it.mulders.traqqr.domain.vehicles.Vehicle;
import java.math.BigDecimal;
import java.util.ArrayList;

public class VehicleFaker {
    public static Vehicle createVehicle() {
        var code = RandomStringUtils.generateRandomAlphaString(5);
        return createVehicle(code);
    }

    public static Vehicle createVehicle(String code) {
        var ownerId = RandomStringUtils.generateRandomAlphaString(12);
        return createVehicle(code, ownerId);
    }

    public static Vehicle createVehicle(Owner owner) {
        var code = RandomStringUtils.generateRandomAlphaString(5);
        return createVehicle(code, owner.code());
    }

    public static Vehicle createVehicle(String code, String ownerId) {
        return createVehicle(code, ownerId, BigDecimal.valueOf(55));
    }

    public static Vehicle createVehicle(String code, String ownerId, BigDecimal capacity) {
        var description = RandomStringUtils.generateRandomAlphaString(10);
        return createVehicle(code, ownerId, description, capacity);
    }

    public static Vehicle createVehicle(String code, String ownerId, String description, BigDecimal capacity) {
        return new Vehicle(code, description, ownerId, new ArrayList<>(), capacity);
    }
}
