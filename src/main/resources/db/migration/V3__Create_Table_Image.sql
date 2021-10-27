CREATE TABLE IF NOT EXISTS images (
    id SERIAL PRIMARY KEY
    , name text
    , type text
    , url text
    , img bytea
);