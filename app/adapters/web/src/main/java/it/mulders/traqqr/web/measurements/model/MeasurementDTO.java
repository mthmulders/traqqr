package it.mulders.traqqr.web.measurements.model;

import it.mulders.traqqr.web.vehicles.model.VehicleDTO;
import java.util.Objects;

public final class MeasurementDTO {
    private Integer odometer;
    private Byte batterySoc;
    private String location;
    private VehicleDTO vehicle;

    public Integer getOdometer() {
        return odometer;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public Byte getBatterySoc() {
        return batterySoc;
    }

    public void setBatterySoc(Byte batterySoc) {
        this.batterySoc = batterySoc;
    }

    public VehicleDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDTO vehicle) {
        this.vehicle = vehicle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MeasurementDTO that)) return false;
        return Objects.equals(odometer, that.odometer)
                && Objects.equals(batterySoc, that.batterySoc)
                && Objects.equals(location, that.location)
                && Objects.equals(vehicle, that.vehicle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(odometer, batterySoc, location, vehicle);
    }
}
