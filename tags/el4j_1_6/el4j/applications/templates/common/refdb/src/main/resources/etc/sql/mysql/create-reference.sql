-- Version: $Revision$
-- URL: $URL$
-- Date: $Date$
-- Author: $Author$

CREATE TABLE referencesTable (
  keyId                     INTEGER        NOT NULL 
    PRIMARY KEY AUTO_INCREMENT,
  name                      VARCHAR(64)    NOT NULL,
  hashValue                 VARCHAR(64),
  description               VARCHAR(256),
  version                   VARCHAR(64),
  incomplete                DECIMAL(1),
  whenInserted              DATETIME      NOT NULL,
  documentDate              DATE,
  optimisticLockingVersion  INTEGER        NOT NULL
) engine = innoDB;

CREATE TABLE links (
  keyToReference            INTEGER
  	NOT NULL REFERENCES referencesTable(keyId),
  url                       VARCHAR(500)
) engine = innoDB;

CREATE TABLE formalPublications (
  keyToReference            INTEGER
  	NOT NULL REFERENCES referencesTable(keyId),
  authorName                VARCHAR(64),
  publisher                 VARCHAR(64),
  pageNum                   SMALLINT NOT NULL DEFAULT 0
) engine = innoDB;

CREATE TABLE books (
  keyToReference            INTEGER
  	NOT NULL REFERENCES referencesTable(keyId),
  authorName                VARCHAR(64),
  publisher                 VARCHAR(64),
  pageNum                   SMALLINT NOT NULL DEFAULT 0,
  isbnNumber                VARCHAR(20)
) engine = innoDB;

CREATE TABLE referenceKeywordRelationships (
  keyReference              INTEGER
  	NOT NULL REFERENCES referencesTable(keyId),
  keyKeyword                INTEGER
  	NOT NULL REFERENCES keywords(keyId),
  PRIMARY KEY (keyReference, keyKeyword)
) engine = innoDB;

CREATE TABLE annotations (
  keyId                     INTEGER        NOT NULL
    PRIMARY KEY AUTO_INCREMENT,
  keyToReference            INTEGER        
  	NOT NULL REFERENCES referencesTable(keyId),
  annotator                 VARCHAR(64)    NOT NULL,
  grade                     DECIMAL(2)     NOT NULL,
  content                   LONGBLOB       NOT NULL,
  whenInserted              DATETIME      NOT NULL,
  optimisticLockingVersion  INTEGER        NOT NULL
) engine = innoDB;

CREATE TABLE files (
  keyId                     INTEGER        NOT NULL
    PRIMARY KEY AUTO_INCREMENT,
  keyToReference            INTEGER        
  	NOT NULL REFERENCES referencesTable(keyId),
  name                      VARCHAR(64)    NOT NULL,
  mimeType                  VARCHAR(32)    NOT NULL,
  contentSize               DOUBLE         NOT NULL,
  content					LONGBLOB	   NOT NULL,
  optimisticLockingVersion  INTEGER        NOT NULL,
  dtype						VARCHAR(100)
) engine = innoDB;
