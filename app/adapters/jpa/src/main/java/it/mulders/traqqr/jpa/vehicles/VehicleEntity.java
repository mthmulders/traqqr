package it.mulders.traqqr.jpa.vehicles;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "Vehicle")
@Table(name = "vehicle")
public class VehicleEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "owner_id")
    private String ownerId;

    @Column(name = "net_battery_capacity")
    private BigDecimal netBatteryCapacity;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "vehicle")
    private Collection<AuthorisationEntity> authorisations;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public BigDecimal getNetBatteryCapacity() {
        return netBatteryCapacity;
    }

    public void setNetBatteryCapacity(BigDecimal netBatteryCapacity) {
        this.netBatteryCapacity = netBatteryCapacity;
    }

    public Collection<AuthorisationEntity> getAuthorisations() {
        return authorisations;
    }

    public void setAuthorisations(Collection<AuthorisationEntity> authorisations) {
        this.authorisations = authorisations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
