-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

alter table Offer drop constraint FK4892A3CC739F9F3;
alter table Offer drop constraint FK4892A3CF323CA5B;
alter table Offer_Employee drop constraint FKA3BF157182F9755A;
alter table Offer_Employee drop constraint FKA3BF157172FD7F99;
drop table Client;
drop table Employee;
drop table EntityTest;
drop table Offer;
drop table Offer_Employee;