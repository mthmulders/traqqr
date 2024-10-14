CREATE
    TABLE
        authorisation(
            id uuid PRIMARY KEY NOT NULL,
            vehicle_id uuid NOT NULL,
            generated_at TIMESTAMP WITH TIME ZONE NOT NULL,
            invalidated_at TIMESTAMP WITH TIME ZONE,
            hashed_key VARCHAR NOT NULL
        );

ALTER TABLE
    authorisation ADD CONSTRAINT authorisation_vehicle_fk FOREIGN KEY(vehicle_id) REFERENCES vehicle(id);