CREATE TABLE IF NOT EXISTS public.customer (
    id serial PRIMARY KEY,
    first_name VARCHAR (255) NOT NULL,
    last_name VARCHAR (255) NOT NULL
    );

INSERT INTO public.customer(id, first_name, last_name)
VALUES (1, 'Alex', 'Marphay'),
       (2, 'Kriyan', 'Roy'),
       (3, 'Ketty', 'Muni');


