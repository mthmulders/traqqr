package it.mulders.traqqr.jpa.measurements;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class GpsLocationEntity {
    BigDecimal latitude;
    BigDecimal longitude;

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
}
