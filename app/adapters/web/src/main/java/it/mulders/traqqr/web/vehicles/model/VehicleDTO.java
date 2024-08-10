package it.mulders.traqqr.web.vehicles.model;

import java.util.Objects;

public class VehicleDTO {
    private String code;
    private String description;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VehicleDTO that)) return false;
        return Objects.equals(description, that.description) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, code);
    }

    @Override
    public String toString() {
        return "VehicleDTO{" + "code='" + code + '\'' + ", description='" + description + '\'' + '}';
    }
}
