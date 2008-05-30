-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

create table Client (id integer not null generated always as identity, activity varchar(256), address varchar(64), enterprise varchar(32) not null, version integer, primary key (id));

create table Employee (id integer not null generated always as identity, active smallint not null, firstName varchar(32) not null, lastName varchar(32) not null, version integer, visa varchar(3) not null, primary key (id));

create table EntityTest (id integer not null generated always as identity, big integer not null, TEST_DATE date, name varchar(64) not null, primary key (id));

create table Offer (id integer not null generated always as identity, cost bigint not null check (cost>=0), enhancements smallint not null, NR bigint not null check (NR>=1), offer varchar(64) not null, OFFER_STATE integer, version integer, client_id integer not null, responsible_id integer not null, primary key (id));

create table Offer_Employee (Offer_id integer not null, redactors_id integer not null);
alter table Offer add constraint FK4892A3CC739F9F3 foreign key (responsible_id) references Employee;
alter table Offer add constraint FK4892A3CF323CA5B foreign key (client_id) references Client;
alter table Offer_Employee add constraint FKA3BF157182F9755A foreign key (redactors_id) references Employee;
alter table Offer_Employee add constraint FKA3BF157172FD7F99 foreign key (Offer_id) references Offer;
