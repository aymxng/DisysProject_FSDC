CREATE TABLE IF NOT EXISTS public.station_hub
(
    id
        SERIAL
        PRIMARY
            KEY,
    db_url
        VARCHAR
            (
            255
            ) NOT NULL,
    lat REAL NOT NULL,
    lng REAL NOT NULL
);


INSERT INTO public.station_hub(id, db_url, lat, lng)
VALUES (1, 'localhost:8082', '48.184192', '16.378604'),
       (2, 'localhost:8083', '48.186116', '16.377746'),
       (3, 'localhost:8084', '48.232940', '16.376786');