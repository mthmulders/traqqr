package it.mulders.traqqr.api.measurements;

import java.time.OffsetDateTime;

public class MeasurementDto {
    private OffsetDateTime timestamp;
    private int odometer;
    private BatteryDto battery;
    private LocationDto location;

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public BatteryDto getBattery() {
        return battery;
    }

    public void setBattery(BatteryDto battery) {
        this.battery = battery;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public static class BatteryDto {
        private byte soc;

        public byte getSoc() {
            return soc;
        }

        public void setSoc(byte soc) {
            this.soc = soc;
        }
    }

    public static class LocationDto {
        private double lat;
        private double lon;

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }
}
