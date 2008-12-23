INSERT INTO REFERENCESTABLE (KEYID,NAME,HASHVALUE,DESCRIPTION,VERSION,INCOMPLETE,WHENINSERTED,DOCUMENTDATE,OPTIMISTICLOCKINGVERSION) VALUES (1,'The Complete Log4j Manual','lkjh','This book shows you how to use Log4J.','1.0',0,{ts '2008-08-13 14:43:38.692'},null,0);
INSERT INTO REFERENCESTABLE (KEYID,NAME,HASHVALUE,DESCRIPTION,VERSION,INCOMPLETE,WHENINSERTED,DOCUMENTDATE,OPTIMISTICLOCKINGVERSION) VALUES (4,'The LaTeX Companion',null,'Provides expert advice on using Latex''s basic formatting tools for creating all types of publications. Includes in-depth coverage of important extension packages for tabular and technical typesetting.',null,0,{ts '2008-08-13 14:46:44.628'},null,0);
INSERT INTO REFERENCESTABLE (KEYID,NAME,HASHVALUE,DESCRIPTION,VERSION,INCOMPLETE,WHENINSERTED,DOCUMENTDATE,OPTIMISTICLOCKINGVERSION) VALUES (3,'Design Patterns: Elements of Reusable Object-Oriented Software',null,'The classic book for design patterns.',null,0,{ts '2008-08-13 14:45:08.864'},null,0);
INSERT INTO REFERENCESTABLE (KEYID,NAME,HASHVALUE,DESCRIPTION,VERSION,INCOMPLETE,WHENINSERTED,DOCUMENTDATE,OPTIMISTICLOCKINGVERSION) VALUES (5,'Java In A Nutshell',null,'"The" book for Java developers',null,0,{ts '2008-08-13 14:48:55.807'},null,0);

INSERT INTO BOOKS (KEYTOREFERENCE,AUTHORNAME,PUBLISHER,PAGENUM,ISBNNUMBER) VALUES (1,'Ceki Gulcu','QOS.ch',206,'2970036908');
INSERT INTO BOOKS (KEYTOREFERENCE,AUTHORNAME,PUBLISHER,PAGENUM,ISBNNUMBER) VALUES (3,'Erich Gamma',null,0,'0-201-36299-6');
INSERT INTO BOOKS (KEYTOREFERENCE,AUTHORNAME,PUBLISHER,PAGENUM,ISBNNUMBER) VALUES (4,'Mittelbach Frank, Goosens Michel',null,0,null);
INSERT INTO BOOKS (KEYTOREFERENCE,AUTHORNAME,PUBLISHER,PAGENUM,ISBNNUMBER) VALUES (5,'David Flanagan',null,1122,null);

INSERT INTO KEYWORDS (NAME,DESCRIPTION,OPTIMISTICLOCKINGVERSION) VALUES (1,'Java','Java books',0);
INSERT INTO KEYWORDS (NAME,DESCRIPTION,OPTIMISTICLOCKINGVERSION) VALUES (2,'Web','Web development',0);

INSERT INTO REFERENCEKEYWORDRELATIONSHIPS (KEYREFERENCE,KEYKEYWORD) VALUES (1,1);
INSERT INTO REFERENCEKEYWORDRELATIONSHIPS (KEYREFERENCE,KEYKEYWORD) VALUES (1,2);

INSERT INTO ANNOTATIONS (KEYID,KEYTOREFERENCE,ANNOTATOR,GRADE,CONTENT,WHENINSERTED,OPTIMISTICLOCKINGVERSION) VALUES (1,1,'pos',10,'this is a good book',{ts '2008-12-23 17:48:39.818'},0);