CREATE TABLE TESTCHILDA(
  name    VARCHAR(64)    UNIQUE NOT NULL
);

-- check dependencies
SELECT * FROM TESTCHILDTWO;