ALTER TABLE
    measurement ADD COLUMN measured_at TIMESTAMP WITH TIME ZONE;

UPDATE
    measurement
SET
    measured_at = registered_at
WHERE
    measured_at IS NULL;

ALTER TABLE
    measurement ALTER COLUMN measured_at
SET
    NOT NULL;