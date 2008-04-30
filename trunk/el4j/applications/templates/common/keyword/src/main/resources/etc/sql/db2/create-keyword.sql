-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

CREATE TABLE keywords (
  keyId                     INTEGER        NOT NULL 
    GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)
    PRIMARY KEY,
  name                      VARCHAR(64)    UNIQUE NOT NULL,
  description               VARCHAR(256),
  optimisticLockingVersion  INTEGER        NOT NULL
);

