-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

create table Client (id number(10,0) not null, activity varchar2(256 char), address varchar2(64 char), enterprise varchar2(32 char) not null, version number(10,0), primary key (id));

create table Employee (id number(10,0) not null, active number(1,0) not null, firstName varchar2(32 char) not null, lastName varchar2(32 char) not null, version number(10,0), visa varchar2(3 char) not null, primary key (id));

create table EntityTest (id number(10,0) not null, big number(10,0) not null, TEST_DATE date, name varchar2(64 char) not null, primary key (id));

create table Offer (id number(10,0) not null, cost number(19,0) not null check (cost>=0), enhancements number(1,0) not null, NR number(19,0) not null check (NR>=1), offer varchar2(64 char) not null, OFFER_STATE number(10,0), version number(10,0), client_id number(10,0) not null, responsible_id number(10,0) not null, primary key (id));

create table Offer_Employee (Offer_id number(10,0) not null, redactors_id number(10,0) not null);

alter table Offer add constraint FK4892A3CC739F9F3 foreign key (responsible_id) references Employee;
alter table Offer add constraint FK4892A3CF323CA5B foreign key (client_id) references Client;
alter table Offer_Employee add constraint FKA3BF157182F9755A foreign key (redactors_id) references Employee;
alter table Offer_Employee add constraint FKA3BF157172FD7F99 foreign key (Offer_id) references Offer;
create sequence hibernate_sequence;
