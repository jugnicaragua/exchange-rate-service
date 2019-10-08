drop table if exists user_subscription cascade;
drop table if exists email_task;


create table if not exists user_subscription (
    id serial not null constraint user_subscription_pk primary key
    , full_name character varying(250) not null
    , email character varying(100) not null
    , token character varying(100) not null
    , is_active boolean not null default false
    , created_on timestamp(3) without time zone not null default (now())
    , updated_on timestamp(3) without time zone null
    , constraint user_subscription_email_uq unique (email)
);


create table if not exists email_task (
    id serial not null constraint email_task_pk primary key
    , user_subscription_id integer not null constraint email_task_user_subscription_fk references user_subscription (id)
    , date timestamp(3) without time zone not null default(now())
);
