CREATE
    TABLE
        measurement(
            id UUID PRIMARY KEY NOT NULL,
            vehicle_id UUID NOT NULL,
            registered_at TIMESTAMP WITH TIME ZONE NOT NULL,
            odometer INT NOT NULL,
            battery_soc SMALLINT NOT NULL,
            gps_location_lat SMALLINT NOT NULL,
            gps_location_long SMALLINT NOT NULL
        );

ALTER TABLE
    measurement ADD CONSTRAINT measurement_vehicle_fk FOREIGN KEY(vehicle_id) REFERENCES vehicle(id);