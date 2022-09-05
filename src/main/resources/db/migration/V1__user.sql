create extension if not exists "pgcrypto";

create table if not exists users
(
    id       UUID DEFAULT gen_random_uuid(),
    email    text not null,
    username text,
    password text,
    phone    text,
    cell     text
);

create unique index events_id_index
    on users (id);

create unique index events_email_index
    on users (email);

alter table users
    add constraint events_pk
        primary key (id);
