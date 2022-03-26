create table course(
id bigint not null,
name varchar(255) not null,
last_updated date,
created date,
primary key(id)
);

create table student(
id bigint not null,
name varchar(255) not null,
last_updated date,
created date,
passport_id bigint,
primary key(id)
);

create table passport(
id bigint not null,
passport_number varchar(255) not null,
last_updated date,
created date,
primary key(id)
);

alter table student add constraint FK_passport foreign key (passport_id) references passport;

create table review(
id bigint not null,
description varchar(255),
last_updated date,
created date,
primary key(id)
);