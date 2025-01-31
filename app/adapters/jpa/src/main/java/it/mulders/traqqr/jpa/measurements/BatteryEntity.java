package it.mulders.traqqr.jpa.measurements;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class BatteryEntity {
    @Column(name = "soc")
    private int soc;

    public int getSoc() {
        return soc;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }
}
