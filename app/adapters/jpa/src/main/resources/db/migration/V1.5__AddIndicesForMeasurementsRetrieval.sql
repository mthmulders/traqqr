CREATE
    INDEX vehicle_code_idx ON
    vehicle(code);

CREATE
    INDEX measurement_vehicle_idx ON
    measurement(vehicle_id);