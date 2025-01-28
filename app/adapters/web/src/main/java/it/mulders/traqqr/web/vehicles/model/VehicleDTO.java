package it.mulders.traqqr.web.vehicles.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class VehicleDTO {
    private String code;
    private String description;
    private AuthorisationDTO authorisation;
    private BigDecimal netBatteryCapacity;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public AuthorisationDTO getAuthorisation() {
        return authorisation;
    }

    public void setAuthorisation(AuthorisationDTO authorisation) {
        this.authorisation = authorisation;
    }

    public BigDecimal getNetBatteryCapacity() {
        return netBatteryCapacity;
    }

    public void setNetBatteryCapacity(BigDecimal netBatteryCapacity) {
        this.netBatteryCapacity = netBatteryCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleDTO that)) return false;
        return Objects.equals(description, that.description)
                && Objects.equals(code, that.code)
                && Objects.equals(authorisation, that.authorisation)
                && Objects.equals(netBatteryCapacity, that.netBatteryCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, code, authorisation, netBatteryCapacity);
    }

    @Override
    public String toString() {
        return "VehicleDTO{" + "code='" + code + '\'' + ", description='" + description + '\'' + '}';
    }
}
