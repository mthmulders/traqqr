package it.mulders.traqqr.jpa.measurements;

import jakarta.persistence.Embeddable;

@Embeddable
public class GpsLocationEntity {
    double latitude;
    double longitude;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
