ALTER TABLE vehicle
ADD COLUMN net_battery_capacity DECIMAL;

UPDATE vehicle
SET net_battery_capacity = 0.0
WHERE net_battery_capacity IS NULL;

ALTER TABLE vehicle
ALTER COLUMN net_battery_capacity SET NOT NULL;
