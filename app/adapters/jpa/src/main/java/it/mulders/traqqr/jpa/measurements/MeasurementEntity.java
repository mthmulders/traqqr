package it.mulders.traqqr.jpa.measurements;

import it.mulders.traqqr.jpa.vehicles.VehicleEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "measurement")
public class MeasurementEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private VehicleEntity vehicle;

    @Column(name = "registered_at")
    private OffsetDateTime registeredAt;

    @Column(name = "odometer")
    private Integer odometer;

    @AttributeOverrides({@AttributeOverride(name = "soc", column = @Column(name = "battery_soc"))})
    @Embedded
    private BatteryEntity battery;

    @AttributeOverrides({
        @AttributeOverride(name = "latitude", column = @Column(name = "gps_location_lat")),
        @AttributeOverride(name = "longitude", column = @Column(name = "gps_location_long"))
    })
    @Embedded
    private GpsLocationEntity gpsLocation;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public VehicleEntity getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleEntity vehicle) {
        this.vehicle = vehicle;
    }

    public OffsetDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(OffsetDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Integer getOdometer() {
        return odometer;
    }

    public void setOdometer(Integer odometer) {
        this.odometer = odometer;
    }

    public BatteryEntity getBattery() {
        return battery;
    }

    public void setBattery(BatteryEntity battery) {
        this.battery = battery;
    }

    public GpsLocationEntity getGpsLocation() {
        return gpsLocation;
    }

    public void setGpsLocation(GpsLocationEntity gpsLocation) {
        this.gpsLocation = gpsLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MeasurementEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
