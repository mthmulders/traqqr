package it.mulders.traqqr.jpa.measurements;

import static jakarta.persistence.EnumType.STRING;

import it.mulders.traqqr.jpa.vehicles.VehicleEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "Measurement")
@Table(name = "measurement")
public class MeasurementEntity {
    enum Source {
        API,
        USER
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "vehicle_id")
    @ManyToOne
    private VehicleEntity vehicle;

    @Column(name = "registered_at")
    private OffsetDateTime registeredAt;

    @Column(name = "measured_at")
    private OffsetDateTime measuredAt;

    @Column(name = "odometer")
    private Integer odometer;

    @AttributeOverrides({@AttributeOverride(name = "soc", column = @Column(name = "battery_soc"))})
    @Embedded
    private BatteryEntity battery;

    @Column(name = "source")
    @Enumerated(STRING)
    private Source source;

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

    public OffsetDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(OffsetDateTime measuredAt) {
        this.measuredAt = measuredAt;
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

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
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
