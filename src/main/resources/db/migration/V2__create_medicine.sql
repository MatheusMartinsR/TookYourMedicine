CREATE TABLE medicine.medicines (
    id UUID PRIMARY KEY,
    medicine_name VARCHAR(255) NOT NULL,
    quantity_per_day INTEGER NOT NULL,
    hour_to_take TIME NOT NULL,
    take BOOLEAN NOT NULL DEFAULT false,
    user_id UUID NOT NULL REFERENCES medicine.users(id)

);
