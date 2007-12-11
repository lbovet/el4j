-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

CREATE SEQUENCE name_sequence INCREMENT BY 1 START WITH 1;

CREATE TABLE names (
  nameId                     INTEGER        NOT NULL PRIMARY KEY,
  name                      VARCHAR(64)    UNIQUE NOT NULL,
  optimisticLockingVersion  INTEGER        NOT NULL
);

