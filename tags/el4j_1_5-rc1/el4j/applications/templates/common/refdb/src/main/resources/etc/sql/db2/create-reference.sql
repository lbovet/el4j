-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

CREATE TABLE referencesTable (
  keyId                     INTEGER        NOT NULL
    GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) 
    PRIMARY KEY,
  name                      VARCHAR(64)    NOT NULL,
  hashValue                 VARCHAR(64),
  description               VARCHAR(256),
  version                   VARCHAR(64),
  incomplete                DECIMAL(1),
  whenInserted              TIMESTAMP      NOT NULL,
  documentDate              DATE,
  optimisticLockingVersion  INTEGER        NOT NULL
);

CREATE TABLE links (
  keyToReference            INTEGER
  	NOT NULL REFERENCES referencesTable(keyId),
  url                       VARCHAR(500)
);

CREATE TABLE formalPublications (
  keyToReference            INTEGER
  	NOT NULL REFERENCES referencesTable(keyId),
  authorName                VARCHAR(64),
  publisher                 VARCHAR(64),
  pageNum                   SMALLINT
);

CREATE TABLE books (
  keyToReference            INTEGER
  	NOT NULL REFERENCES referencesTable(keyId),
  authorName                VARCHAR(64),
  publisher                 VARCHAR(64),
  pageNum                   SMALLINT,
  isbnNumber                VARCHAR(20)
);

CREATE TABLE referenceKeywordRelationships (
  keyReference              INTEGER
  	NOT NULL REFERENCES referencesTable(keyId),
  keyKeyword                INTEGER
  	NOT NULL REFERENCES keywords(keyId),
  PRIMARY KEY (keyReference, keyKeyword)
);

CREATE TABLE annotations (
  keyId                     INTEGER        NOT NULL
    GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) 
    PRIMARY KEY,
  keyToReference            INTEGER        
  	NOT NULL REFERENCES referencesTable(keyId),
  annotator                 VARCHAR(64)    NOT NULL,
  grade                     DECIMAL(2)     NOT NULL,
  content                   CLOB           NOT NULL,
  whenInserted              TIMESTAMP      NOT NULL,
  optimisticLockingVersion  INTEGER        NOT NULL
);

CREATE TABLE files (
  keyId                     INTEGER        NOT NULL
    GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) 
    PRIMARY KEY,
  keyToReference            INTEGER        
  	NOT NULL REFERENCES referencesTable(keyId),
  name                      VARCHAR(64)    NOT NULL,
  mimeType                  VARCHAR(32)    NOT NULL,
  contentSize               DOUBLE         NOT NULL,
  content                   BLOB           NOT NULL,
  optimisticLockingVersion  INTEGER        NOT NULL,
  dtype						VARCHAR(100)
);
