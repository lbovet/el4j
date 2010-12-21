-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

CREATE TABLE aspects_tests (
  keyid                       NUMBER(10) NOT NULL PRIMARY KEY,
  name                        VARCHAR2(64 CHAR) UNIQUE NOT NULL,
  description                 VARCHAR2(256 CHAR),
  optimisticLockingVersion    NUMBER(10) DEFAULT 0 NOT NULL
);

CREATE SEQUENCE aspects_tests_seq
INCREMENT BY 1
START WITH 1
NOMAXVALUE
MINVALUE 1
NOCYCLE
NOCACHE
NOORDER;

CREATE OR REPLACE TRIGGER set_aspects_tests_keyid
BEFORE INSERT
ON aspects_tests
FOR EACH ROW
BEGIN
  SELECT aspects_tests_seq.NEXTVAL
  INTO :NEW.keyid
  FROM DUAL;
END;
/
