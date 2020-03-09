create table users
(
    user_id  serial not null
        constraint users_pk
            primary key,
    name     varchar(100),
    password varchar(100),
    login    varchar(100)
);
