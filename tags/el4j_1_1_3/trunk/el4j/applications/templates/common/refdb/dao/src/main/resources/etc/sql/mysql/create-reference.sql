-- Version: $Revision: 1860 $
-- URL: $URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/applications/templates/common/refdb/dao/src/main/resources/etc/sql/oracle/create-reference.sql $
-- Date: $Date: 2007-07-22 22:37:17 +0200 (Sun, 22 Jul 2007) $
-- Author: $Author: poser55 $

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
    PRIMARY KEY AUTO_INCREMENT,
  keyToReference            INTEGER        
  	NOT NULL REFERENCES referencesTable(keyId),
  annotator                 VARCHAR(64)    NOT NULL,
  grade                     DECIMAL(2)     NOT NULL,
  content                   LONGBLOB       NOT NULL,
  whenInserted              DATETIME      NOT NULL,
  optimisticLockingVersion  INTEGER        NOT NULL
);

CREATE TABLE files (
  keyId                     INTEGER        NOT NULL
    PRIMARY KEY AUTO_INCREMENT,
  keyToReference            INTEGER        
  	NOT NULL REFERENCES referencesTable(keyId),
  name                      VARCHAR(64)    NOT NULL,
  mimeType                  VARCHAR(32)    NOT NULL,
  contentSize               DOUBLE         NOT NULL,
  content                   LONGBLOB       NOT NULL,
  optimisticLockingVersion  INTEGER        NOT NULL
);
