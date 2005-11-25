-- Version: $Revision$
-- Source: $Source$
-- Date: $Date$
-- Author: $Author$

CREATE SEQUENCE keyword_sequence INCREMENT BY 1 START WITH 1;

CREATE TABLE keywords (
  keyId                     INTEGER        NOT NULL PRIMARY KEY,
  name                      VARCHAR(64)    UNIQUE NOT NULL,
  description               VARCHAR(256),
  optimisticLockingVersion  INTEGER        NOT NULL
);
