-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

CREATE TABLE keywords (
  keyId                     INTEGER        NOT NULL    PRIMARY KEY AUTO_INCREMENT,
  name                      VARCHAR(64)    UNIQUE NOT NULL,
  description               VARCHAR(256),
  optimisticLockingVersion  INTEGER        NOT NULL
);
