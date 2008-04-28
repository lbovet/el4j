-- Version: $Revision: 1860 $
-- URL: $URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/applications/templates/common/keyword/dao/src/main/resources/etc/sql/db2/create-keyword.sql $
-- Date: $Date: 2007-07-22 22:37:17 +0200 (Sun, 22 Jul 2007) $
-- Author: $Author: poser55 $

CREATE TABLE keywords (
  keyId                     INTEGER        NOT NULL    PRIMARY KEY AUTO_INCREMENT,
  name                      VARCHAR(64)    UNIQUE NOT NULL,
  description               VARCHAR(256),
  optimisticLockingVersion  INTEGER        NOT NULL
);
