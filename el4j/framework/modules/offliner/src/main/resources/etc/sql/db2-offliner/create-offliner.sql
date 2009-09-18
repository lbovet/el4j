-- Create script for local schema.
-- This file is read and executed by the test starter.

-- The offliner tables.

CREATE TABLE KEYMAP (
	ID INT NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY 
		(START WITH 1, INCREMENT BY 1),    
	LOCALBASEVERSION VARCHAR(128) NOT NULL,
	REMOTEBASEVERSION VARCHAR(128) NOT NULL,
	DELETEVERSION BIGINT NOT NULL,
	SYNCVERSION INT,
    LOCALKEY VARCHAR(128) NOT NULL,
    REMOTEKEY VARCHAR(128) NOT NULL
);

CREATE TABLE OFFLINERPROPERTIES (
	ID INT NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY 
		(START WITH 1, INCREMENT BY 1),
	PROPNAME VARCHAR(30) NOT NULL UNIQUE,
    PROPVALUE VARCHAR(40)
);