package it.mulders.traqqr.web.vehicles.model;

import jakarta.mvc.binding.MvcBinding;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import java.math.BigDecimal;
import java.util.Objects;

public class VehicleDTO {
    @FormParam("vehicle.code")
    @MvcBinding
    @NotNull
    @NotEmpty
    private String code;

    @FormParam("vehicle.description")
    @MvcBinding
    @NotNull
    @NotEmpty
    private String description;

    @MvcBinding
    @FormParam("vehicle.netBatteryCapacity")
    private BigDecimal netBatteryCapacity;

    public VehicleDTO() {}

    public VehicleDTO(String code, String description, BigDecimal netBatteryCapacity) {
        this.code = code;
        this.description = description;
        this.netBatteryCapacity = netBatteryCapacity;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getNetBatteryCapacity() {
        return netBatteryCapacity;
    }

    public void setNetBatteryCapacity(BigDecimal netBatteryCapacity) {
        this.netBatteryCapacity = netBatteryCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VehicleDTO that)) return false;
        return Objects.equals(code, that.code)
                && Objects.equals(description, that.description)
                && Objects.equals(netBatteryCapacity, that.netBatteryCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, description, netBatteryCapacity);
    }
}
