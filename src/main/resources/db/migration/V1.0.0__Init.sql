-- =================================================================================================
-- Definicion de estructuras
-- =================================================================================================
drop table if exists bank cascade;
drop table if exists currency cascade;
drop table if exists ncb_exchange_rate;
drop table if exists cb_exchange_rate;
drop table if exists cookie;


create table if not exists bank (
    id serial not null constraint bank_pk primary key
    , short_description character varying(20) not null
    , description character varying(50) not null
    , url character varying(200) not null
    , is_active boolean not null default true
    , created_on timestamp(3) without time zone not null default (now())
    , updated_on timestamp(3) without time zone null
    , constraint bank_short_description_uq unique (short_description)
    , constraint bank_description_uq unique (description)
);


create table if not exists currency (
    id serial not null constraint currency_pk primary key
    , iso_numeric_code character varying(3) not null
    , iso_string_code character varying(3) not null
    , symbol character varying(5) not null
    , short_description character varying(20) not null
    , description character varying(50) not null
    , is_domestic boolean not null default false
    , is_active boolean not null default true
    , created_on timestamp(3) without time zone not null default (now())
    , updated_on timestamp(3) without time zone null
    , constraint iso_numeric_code_uq unique (iso_numeric_code)
    , constraint iso_string_code_uq unique (iso_string_code)
);


create table if not exists ncb_exchange_rate (
    id integer not null constraint ncb_exchange_rate_pk primary key
    , currency_id integer not null constraint ncb_exchange_rate_currency_fk references currency (id)
    , exchange_rate_date date not null
    , exchange_rate_amount numeric(18, 4) not null
    , created_on timestamp(3) without time zone not null default (now())
    , updated_on timestamp(3) without time zone null
    , constraint ncb_exchange_rate_uq unique (currency_id, exchange_rate_date)
);


create table if not exists cb_exchange_rate (
    id serial not null constraint cb_exchange_rate_pk primary key
    , currency_id integer not null constraint cb_exchange_rate_currency_fk references currency (id)
    , bank_id integer not null constraint cb_exchange_rate_bank_fk references bank (id)
    , exchange_rate_date date not null
    , sell numeric(18, 4) not null
    , buy numeric(18, 4) not null
    , is_best_sell_price boolean not null default false
    , is_best_buy_price boolean not null default false
    , created_on timestamp(3) without time zone not null default (now())
    , updated_on timestamp(3) without time zone null
    , constraint cb_exchange_rate_uq unique (bank_id, currency_id, exchange_rate_date)
);


create table if not exists cookie (
    id serial not null constraint cookie_pk primary key
    , name character varying(50) not null
    , value character varying(150) not null
    , bank_id integer not null constraint cookie_bank_fk references bank (id)
    , created_on timestamp(3) without time zone not null default (now())
    , updated_on timestamp(3) without time zone null
    , constraint cookie_name_uq unique (bank_id, name)
);
