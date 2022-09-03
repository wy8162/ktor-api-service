create extension if not exists "pgcrypto";

create table if not exists events
(
    id          UUID DEFAULT gen_random_uuid (),
    response    text    not null
);

create unique index events_id_uindex
    on events (id);

alter table events
    add constraint events_pk
        primary key (id);
