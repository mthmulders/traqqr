CREATE
    TABLE
        vehicle(
            id uuid PRIMARY KEY NOT NULL,
            code VARCHAR NOT NULL,
            description VARCHAR NOT NULL,
            owner_id VARCHAR NOT NULL
        );

ALTER TABLE
    vehicle ADD CONSTRAINT vehicle_owner_code UNIQUE(code);