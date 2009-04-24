INSERT INTO REFERENCESTABLE (KEYID,NAME,HASHVALUE,DESCRIPTION,VERSION,INCOMPLETE,WHENINSERTED,DOCUMENTDATE,OPTIMISTICLOCKINGVERSION) VALUES (reference_sequence.nextVal,'The Complete Log4j Manual','lkjh','This book shows you how to use Log4J.','1.0',0,{ts '2008-08-13 14:43:38.692'},null,0);
INSERT INTO BOOKS (KEYTOREFERENCE,AUTHORNAME,PUBLISHER,PAGENUM,ISBNNUMBER) VALUES (reference_sequence.currVal,'Ceki Gulcu','QOS.ch',206,'2970036908');
INSERT INTO KEYWORDS (KEYID,NAME,DESCRIPTION,OPTIMISTICLOCKINGVERSION) VALUES (keyword_sequence.nextVal,'Java','Java books',0);
INSERT INTO REFERENCEKEYWORDRELATIONSHIPS (KEYREFERENCE,KEYKEYWORD) VALUES (reference_sequence.currVal,keyword_sequence.currVal);
INSERT INTO KEYWORDS (KEYID,NAME,DESCRIPTION,OPTIMISTICLOCKINGVERSION) VALUES (keyword_sequence.nextVal,'Web','Web development',0);
INSERT INTO REFERENCEKEYWORDRELATIONSHIPS (KEYREFERENCE,KEYKEYWORD) VALUES (reference_sequence.currVal,keyword_sequence.currVal);
INSERT INTO ANNOTATIONS (KEYID,KEYTOREFERENCE,ANNOTATOR,GRADE,CONTENT,WHENINSERTED,OPTIMISTICLOCKINGVERSION) VALUES (annotation_sequence.nextVal,reference_sequence.currVal,'pos',10,'this is a good book',{ts '2008-12-23 17:48:39.818'},0);

INSERT INTO REFERENCESTABLE (KEYID,NAME,HASHVALUE,DESCRIPTION,VERSION,INCOMPLETE,WHENINSERTED,DOCUMENTDATE,OPTIMISTICLOCKINGVERSION) VALUES (reference_sequence.nextVal,'The LaTeX Companion',null,'Provides expert advice on using Latex''s basic formatting tools for creating all types of publications. Includes in-depth coverage of important extension packages for tabular and technical typesetting.',null,0,{ts '2008-08-13 14:46:44.628'},null,0);
INSERT INTO BOOKS (KEYTOREFERENCE,AUTHORNAME,PUBLISHER,PAGENUM,ISBNNUMBER) VALUES (reference_sequence.currVal,'Mittelbach Frank, Goosens Michel',null,0,'0-201-36299-6');

INSERT INTO REFERENCESTABLE (KEYID,NAME,HASHVALUE,DESCRIPTION,VERSION,INCOMPLETE,WHENINSERTED,DOCUMENTDATE,OPTIMISTICLOCKINGVERSION) VALUES (reference_sequence.nextVal,'Design Patterns: Elements of Reusable Object-Oriented Software',null,'The classic book for design patterns.',null,0,{ts '2008-08-13 14:45:08.864'},null,0);
INSERT INTO BOOKS (KEYTOREFERENCE,AUTHORNAME,PUBLISHER,PAGENUM,ISBNNUMBER) VALUES (reference_sequence.currVal,'Erich Gamma',null,0,null);

INSERT INTO REFERENCESTABLE (KEYID,NAME,HASHVALUE,DESCRIPTION,VERSION,INCOMPLETE,WHENINSERTED,DOCUMENTDATE,OPTIMISTICLOCKINGVERSION) VALUES (reference_sequence.nextVal,'Java In A Nutshell',null,'"The" book for Java developers',null,0,{ts '2008-08-13 14:48:55.807'},null,0);
INSERT INTO BOOKS (KEYTOREFERENCE,AUTHORNAME,PUBLISHER,PAGENUM,ISBNNUMBER) VALUES (reference_sequence.currVal,'David Flanagan',null,1122,null);
