-- Best used for learning purposes. Original developer also has an ER diagram available at https://dbseminar.r61.net/node/32
-- create tables
-- Found this from Github.
-- populate tables

create table if not exists regions
(
    region_id   serial primary key,
    region_name varchar(25)
);

create table if not exists countries
(
    country_id   char(2) not null primary key,
    country_name varchar(40),
    region_id    integer references regions (region_id)
);

create table if not exists locations
(
    location_id    serial primary key,
    street_address varchar(40),
    postal_code    varchar(12),
    city           varchar(30) not null,
    state_province varchar(25),
    country_id     char(2) references countries (country_id)
);

create table if not exists jobs
(
    job_id     varchar(10) primary key,
    job_title  varchar(35) not null,
    min_salary numeric(6),
    max_salary numeric(6)
);

create table if not exists departments
(
    department_id   serial primary key,
    department_name varchar(30) not null,
    manager_id      integer,
    location_id     integer references locations (location_id)
);

create table if not exists employees
(
    employee_id    serial primary key,
    first_name     varchar(20),
    last_name      varchar(25) not null,
    email          varchar(25) not null,
    phone_number   varchar(20),
    hire_date      timestamp   not null,
    job_id         varchar(10) not null references jobs (job_id),
    salary         numeric(8, 2),
    commission_pct numeric(2, 2),
    manager_id     integer references employees (employee_id),
    department_id  integer references departments (department_id),
    constraint emp_salary_min
        check (salary > 0),
    constraint emp_email_uk
        unique (email)
);

create table if not exists job_history
(
    employee_id   integer     not null references employees (employee_id),
    start_date    timestamp   not null,
    end_date      timestamp   not null,
    job_id        varchar(10) not null references jobs (job_id),
    department_id integer references departments (department_id),
    constraint jhist_date_interval
        check (end_date > start_date),
    primary key (employee_id, start_date)
);

alter table departments
    add constraint dept_mgr_fk
        foreign key (manager_id)
            references employees (employee_id);