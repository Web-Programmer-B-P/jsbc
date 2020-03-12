create table users
(
    id       serial not null
        constraint user_pk
            primary key,
    name     varchar(100),
    login    varchar(100),
    password varchar(200)
);