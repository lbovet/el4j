/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.tests.refdb.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import ch.elca.el4j.apps.keyword.dto.KeywordDto;
import ch.elca.el4j.apps.refdb.dto.AnnotationDto;
import ch.elca.el4j.apps.refdb.dto.BookDto;
import ch.elca.el4j.apps.refdb.dto.FileDescriptorView;
import ch.elca.el4j.apps.refdb.dto.FileDto;
import ch.elca.el4j.apps.refdb.dto.FormalPublicationDto;
import ch.elca.el4j.apps.refdb.dto.LinkDto;
import ch.elca.el4j.apps.refdb.dto.ReferenceDto;
import ch.elca.el4j.apps.refdb.service.ReferenceService;
import ch.elca.el4j.services.search.QueryObject;
import ch.elca.el4j.services.search.criterias.ComparisonCriteria;
import ch.elca.el4j.services.search.criterias.IncludeCriteria;
import ch.elca.el4j.services.search.criterias.LikeCriteria;
import ch.elca.el4j.tests.refdb.AbstractTestCaseBase;

// Checkstyle: MagicNumber off

/**
 * Abstract test case for <code>DefaultReferenceService</code>.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Martin Zeltner (MZE)
 */
public abstract class AbstractReferenceServiceTest extends AbstractTestCaseBase {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory
            .getLog(AbstractReferenceServiceTest.class);

    /**
     * Hide default constructor.
     */
    protected AbstractReferenceServiceTest() { }
    
    /**
     * This test inserts a link and three keywords.
     */
    public void testInsertLink() {
        ReferenceService service = getReferenceService();
        LinkDto link = new LinkDto();
        link.setName("iBatis Data Mapper Developer Guide");
        link.setHashValue("xyz");
        link.setDescription("This page shows you how to develop an "
            + "application with iBatis.");
        link.setVersion("2.0");
        link.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.OCTOBER, 25);
        link.setDate(new Date(c.getTimeInMillis()));
        link.setUrl("http://ibatisnet.sourceforge.net/DevGuide.html");

        KeywordDto keyword = new KeywordDto();
        keyword.setName("iBatis");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("SqlMap 2.0");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Data mapper");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        link.setKeywords(listKeywords);

        service.saveReference(link);
    }

    /**
     * This test adds a link and keywords. Some of the keywords are related with
     * the link some are not. After that the link will be get by primary key and
     * compared with the added one. At the end the link will be removed.
     */
    public void testInsertGetRemoveLink() {
        ReferenceService service = getReferenceService();
        LinkDto link = new LinkDto();
        link.setName("iBatis Data Mapper Developer Guide");
        link.setHashValue("xyz");
        link.setDescription("This page shows you how to develop an "
            + "application with iBatis.");
        link.setVersion("2.0");
        link.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.OCTOBER, 25);
        link.setDate(new Date(c.getTimeInMillis()));
        link.setUrl("http://ibatisnet.sourceforge.net/DevGuide.html");

        KeywordDto keyword = new KeywordDto();
        keyword.setName("iBatis");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("SqlMap 2.0");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Data mapper");
        KeywordDto keyword4 = new KeywordDto();
        keyword4.setName("Dummy");
        keyword4.setDescription("This keyword is not "
            + "assigned to any reference.");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);
        service.saveKeyword(keyword4);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        link.setKeywords(listKeywords);

        link = (LinkDto) service.saveReference(link);

        LinkDto link2 = (LinkDto) service.getReferenceByKey(link.getKey());
        assertTrue("Links are not equal.", link.equals(link2));

        Set listKeywords2 = link2.getKeywords();
        assertEquals("There are not three keywords related with the link.", 
            3, listKeywords2.size());
        Iterator it = listKeywords2.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword2) 
                || k.equals(keyword3))) {
                fail("There was an unexpected keyword related with link.");
            }
        }

        service.removeReference(link2.getKey());
        List list = service.getReferencesByName(
            "iBatis Data Mapper Developer Guide");
        assertEquals("There is still a link with "
            + "name 'iBatis Data Mapper Developer Guide'.", list.size(), 0);
    }

    /**
     * This test adds a link with three keywords. After saving the link the same
     * link will be get from database. This link get a new name and one keyword
     * will be replaced by another. Afterwards changes will be saved to
     * database.
     */
    public void testInsertChangeLink() {
        ReferenceService service = getReferenceService();
        LinkDto link = new LinkDto();
        link.setName("iBatis Data Mapper Developer Guide");
        link.setHashValue("xyz");
        link.setDescription("This page shows you how to develop an "
            + "application with iBatis.");
        link.setVersion("2.0");
        link.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.OCTOBER, 25);
        link.setDate(new Date(c.getTimeInMillis()));
        link.setUrl("http://ibatisnet.sourceforge.net/DevGuide.html");

        KeywordDto keyword = new KeywordDto();
        keyword.setName("iBatis");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("SqlMap 2.0");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Data mapper");
        KeywordDto keyword4 = new KeywordDto();
        keyword4.setName("Dummy");
        keyword4.setDescription("This keyword is not "
            + "assigned to any reference.");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);
        service.saveKeyword(keyword4);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        link.setKeywords(listKeywords);

        link = (LinkDto) service.saveReference(link);

        List list = service.getReferencesByName("iBatis Data Mapper "
            + "Developer Guide");
        assertEquals("Not one link with name 'iBatis Data Mapper "
            + "Developer Guide'.", 1, list.size());
        LinkDto link2 = (LinkDto) list.get(0);
        assertTrue("Links are not equal.", link.equals(link2));

        Set listKeywords2 = link2.getKeywords();
        assertEquals("There are not three keywords related with the link.", 
            3, listKeywords2.size());
        Iterator it = listKeywords2.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword2) 
                || k.equals(keyword3))) {
                fail("There was an unexpected keyword related with link.");
            }
        }
        listKeywords2.remove(keyword2);
        KeywordDto keyword5 = new KeywordDto();
        keyword5.setName("New");
        keyword5.setDescription("A brand new keyword.");
        keyword5 = service.saveKeyword(keyword5);
        listKeywords2.add(keyword5);

        link2.setName("iBatis SqlMap 2.0 Developer Guide");
        link2.setKeywords(listKeywords2);
        link2 = (LinkDto) service.saveReference(link2);

        List emptyList = service.getReferencesByName(
            "iBatis Data Mapper Developer Guide");
        assertEquals("Link with name 'iBatis Data Mapper Developer Guide' "
            + "still exists.", emptyList.size(), 0);

        List list2 = service.getReferencesByName("iBatis SqlMap 2.0 "
            + "Developer Guide");
        assertEquals("Not one link with name 'iBatis SqlMap 2.0 "
            + "Developer Guide'.", 1, list2.size());
        LinkDto link3 = (LinkDto) list.get(0);
        assertTrue("Links are not equal.", link2.equals(link3));

        Set listKeywords3 = link3.getKeywords();
        assertEquals("There are not three keywords related with the link.",
            3, listKeywords3.size());
        Iterator it2 = listKeywords3.iterator();
        while (it2.hasNext()) {
            KeywordDto k = (KeywordDto) it2.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword3) 
                || k.equals(keyword5))) {
                fail("There was an unexpected keyword related with link.");
            }
        }
    }

    /**
     * This test adds two links and gets them.
     */
    public void testGetAllLinks() {
        ReferenceService service = getReferenceService();
        LinkDto link = new LinkDto();
        link.setName("iBatis Data Mapper Developer Guide");
        link.setHashValue("xyz");
        link.setDescription("This page shows you how to develop an "
            + "application with iBatis.");
        link.setVersion("2.0");
        link.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.OCTOBER, 25);
        link.setDate(new Date(c.getTimeInMillis()));
        link.setUrl("http://ibatisnet.sourceforge.net/DevGuide.html");
        link = (LinkDto) service.saveReference(link);

        LinkDto link2 = new LinkDto();
        link2.setName("LEO Dictionary English-German");
        link2.setHashValue("leo-en-de");
        link2.setDescription("This is the best online dictionary "
            + "for English and German.");
        link2.setVersion("2004");
        link2.setIncomplete(false);
        Calendar c2 = Calendar.getInstance();
        c2.set(2004, Calendar.NOVEMBER, 8);
        link2.setDate(new Date(c.getTimeInMillis()));
        link2.setUrl("http://dict.leo.org/");
        link2 = (LinkDto) service.saveReference(link2);

        List list = service.getAllReferences();
        assertEquals("There are not two links.", 2, list.size());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            LinkDto link3 = (LinkDto) it.next();
            if (!(link3.equals(link) || link3.equals(link2))) {
                fail("There is an unexpected link.");
            }
        }

        service.removeReference(link.getKey());

        list = service.getAllReferences();
        assertEquals("There is not one link.", 1, list.size());
        LinkDto link4 = (LinkDto) list.get(0);
        assertTrue("There is an unexpected link.", link4.equals(link2));
    }

    /**
     * This test inserts a formal publication and three keywords.
     */
    public void testInsertFormalPublication() {
        ReferenceService service = getReferenceService();
        FormalPublicationDto formalPublication = new FormalPublicationDto();
        formalPublication.setName("iBatis Data Mapper Developer Guide");
        formalPublication.setHashValue("xyz");
        formalPublication.setDescription(
            "This manual shows you how to develop an application with iBatis.");
        formalPublication.setVersion("2.0");
        formalPublication.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.OCTOBER, 25);
        formalPublication.setDate(new Date(c.getTimeInMillis()));
        formalPublication.setAuthorName("Clinton Begin");
        formalPublication.setPublisher("iBatis");
        formalPublication.setPageNum(53);

        KeywordDto keyword = new KeywordDto();
        keyword.setName("iBatis");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("SqlMap 2.0");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Data mapper");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        formalPublication.setKeywords(listKeywords);

        service.saveReference(formalPublication);
    }

    /**
     * This test adds a formal publication and keywords. Some of the keywords
     * are related with the formal publication some are not. After that the
     * formal publication will be get by primary key and compared with the added
     * one. At the end the formal publication will be removed.
     */
    public void testInsertGetRemoveFormalPublication() {
        ReferenceService service = getReferenceService();
        FormalPublicationDto formalPublication = new FormalPublicationDto();
        formalPublication.setName("iBatis Data Mapper Developer Guide");
        formalPublication.setHashValue("xyz");
        formalPublication.setDescription(
            "This manual shows you how to develop an application with iBatis.");
        formalPublication.setVersion("2.0");
        formalPublication.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.OCTOBER, 25);
        formalPublication.setDate(new Date(c.getTimeInMillis()));
        formalPublication.setAuthorName("Clinton Begin");
        formalPublication.setPublisher("iBatis");
        formalPublication.setPageNum(53);

        KeywordDto keyword = new KeywordDto();
        keyword.setName("iBatis");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("SqlMap 2.0");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Data mapper");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        formalPublication.setKeywords(listKeywords);

        formalPublication 
            = (FormalPublicationDto) service.saveReference(formalPublication);

        FormalPublicationDto formalPublication2 
            = (FormalPublicationDto) service.getReferenceByKey(
                formalPublication.getKey());
        assertTrue("Formal publications are not equal.", 
            formalPublication.equals(formalPublication2));

        Set listKeywords2 = formalPublication2.getKeywords();
        assertEquals("There are not three keywords related with the formal "
            + "publication.", 3, listKeywords2.size());
        Iterator it = listKeywords2.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword2) 
                || k.equals(keyword3))) {
                fail("There was an unexpected keyword related with "
                    + "formal publication.");
            }
        }

        service.removeReference(formalPublication2.getKey());
        List list = service.getReferencesByName(
            "iBatis Data Mapper Developer Guide");
        assertEquals("There is still a formal publication with "
            + "name 'iBatis Data Mapper Developer Guide'.", list.size(), 0);
    }

    /**
     * This test adds a formal publication with three keywords. After saving the
     * formal publication the same formal publication will be get from database.
     * This formal publication get a new name and one keyword will be replaced
     * by another. Afterwards changes will be saved to database.
     */
    public void testInsertChangeFormalPublication() {
        ReferenceService service = getReferenceService();
        FormalPublicationDto formalPublication = new FormalPublicationDto();
        formalPublication.setName("iBatis Data Mapper Developer Guide");
        formalPublication.setHashValue("xyz");
        formalPublication.setDescription(
            "This manual shows you how to develop an application with iBatis.");
        formalPublication.setVersion("2.0");
        formalPublication.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.OCTOBER, 25);
        formalPublication.setDate(new Date(c.getTimeInMillis()));
        formalPublication.setAuthorName("Clinton Begin");
        formalPublication.setPublisher("iBatis");
        formalPublication.setPageNum(53);

        KeywordDto keyword = new KeywordDto();
        keyword.setName("iBatis");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("SqlMap 2.0");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("Data mapper");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        formalPublication.setKeywords(listKeywords);

        formalPublication = (FormalPublicationDto) service.saveReference(
            formalPublication);

        List list = service.getReferencesByName(
            "iBatis Data Mapper Developer Guide");
        assertEquals("Not one formal publication with name 'iBatis Data Mapper "
            + "Developer Guide'.", 1, list.size());
        FormalPublicationDto formalPublication2 
            = (FormalPublicationDto) list.get(0);
        assertTrue("Formal publications are not equal.", 
            formalPublication.equals(formalPublication2));

        Set listKeywords2 = formalPublication2.getKeywords();
        assertEquals("There are not three keywords related with the formal "
            + "publication.", 3, listKeywords2.size());
        Iterator it = listKeywords2.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword2) 
                || k.equals(keyword3))) {
                fail("There was an unexpected keyword related with "
                    + "formal publication.");
            }
        }
        listKeywords2.remove(keyword2);
        KeywordDto keyword5 = new KeywordDto();
        keyword5.setName("New");
        keyword5.setDescription("A brand new keyword.");
        keyword5 = service.saveKeyword(keyword5);
        listKeywords2.add(keyword5);

        formalPublication2.setName("iBatis SqlMap 2.0 Developer Guide");
        formalPublication2.setKeywords(listKeywords2);
        formalPublication2 = (FormalPublicationDto) service.saveReference(
            formalPublication2);

        List emptyList = service.getReferencesByName(
            "iBatis Data Mapper Developer Guide");
        assertEquals("Formal publication with name 'iBatis Data Mapper "
            + "Developer Guide' still exists.", emptyList.size(), 0);

        List list2 = service.getReferencesByName(
            "iBatis SqlMap 2.0 Developer Guide");
        assertEquals("Not one formal publication with name 'iBatis SqlMap 2.0 "
            + "Developer Guide'.", 1, list2.size());
        FormalPublicationDto formalPublication3 
            = (FormalPublicationDto) list.get(0);
        assertTrue("Formal publications are not equal.", 
                formalPublication2.equals(formalPublication3));

        Set listKeywords3 = formalPublication3.getKeywords();
        assertEquals("There are not three keywords related with the formal "
            + "publication.", 3, listKeywords3.size());
        Iterator it2 = listKeywords3.iterator();
        while (it2.hasNext()) {
            KeywordDto k = (KeywordDto) it2.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword3) 
                || k.equals(keyword5))) {
                fail("There was an unexpected keyword related with "
                    + "formal publication.");
            }
        }
    }

    /**
     * This test adds two formal publications and gets them.
     */
    public void testGetAllFormalPublications() {
        ReferenceService service = getReferenceService();
        FormalPublicationDto formalPublication = new FormalPublicationDto();
        formalPublication.setName("iBatis Data Mapper Developer Guide");
        formalPublication.setHashValue("xyz");
        formalPublication.setDescription(
            "This manual shows you how to develop an application with iBatis.");
        formalPublication.setVersion("2.0");
        formalPublication.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.OCTOBER, 25);
        formalPublication.setDate(new Date(c.getTimeInMillis()));
        formalPublication.setAuthorName("Clinton Begin");
        formalPublication.setPublisher("iBatis");
        formalPublication.setPageNum(53);
        formalPublication = (FormalPublicationDto) service.saveReference(
            formalPublication);

        FormalPublicationDto formalPublication2 = new FormalPublicationDto();
        formalPublication2.setName("Spring Reference Documentation");
        formalPublication2.setHashValue("asdf");
        formalPublication2.setDescription("This document shows you how to "
            + "use the spring framework.");
        formalPublication2.setVersion("1.1.1");
        formalPublication2.setIncomplete(false);
        Calendar c2 = Calendar.getInstance();
        c2.set(2004, Calendar.SEPTEMBER, 12);
        formalPublication2.setDate(new Date(c2.getTimeInMillis()));
        formalPublication2.setAuthorName("Rod Johnson");
        formalPublication2.setPublisher("Spring");
        formalPublication2.setPageNum(187);
        formalPublication2 = (FormalPublicationDto) service.saveReference(
            formalPublication2);

        List list = service.getAllReferences();
        assertEquals("There are not two formal publications.", 2, list.size());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            FormalPublicationDto formalPublication3 
                = (FormalPublicationDto) it.next();
            if (!(formalPublication3.equals(formalPublication) 
                || formalPublication3.equals(formalPublication2))) {
                fail("There is an unexpected formal publication.");
            }
        }

        service.removeReference(formalPublication.getKey());

        list = service.getAllReferences();
        assertEquals("There is not one formal publication.", 1, list.size());
        FormalPublicationDto formalPublication4 
            = (FormalPublicationDto) list.get(0);
        assertTrue("There is an unexpected formal publication.",
            formalPublication4.equals(formalPublication2));
    }

    /**
     * This test inserts a book and three keywords.
     */
    public void testInsertBook() {
        ReferenceService service = getReferenceService();
        BookDto book = new BookDto();
        book.setName("Expert One-on-One J2EE Development without EJB");
        book.setHashValue("xyz");
        book.setDescription("This book shows you how to develop with "
            + "the spring framework.");
        book.setVersion("1.0");
        book.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.JUNE, 21);
        book.setDate(new Date(c.getTimeInMillis()));
        book.setAuthorName("Rod Johnson, Juergen Hoeller");
        book.setPublisher("Wrox");
        book.setPageNum(576);
        book.setIsbnNumber("0764558315");

        KeywordDto keyword = new KeywordDto();
        keyword.setName("spring");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("j2ee");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("framework");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        book.setKeywords(listKeywords);

        service.saveReference(book);
    }

    /**
     * This test adds a book and keywords. Some of the keywords are related with
     * the book some are not. After that the book will be get by primary key and
     * compared with the added one. At the end the book will be removed.
     */
    public void testInsertGetRemoveBook() {
        ReferenceService service = getReferenceService();
        BookDto book = new BookDto();
        book.setName("Expert One-on-One J2EE Development without EJB");
        book.setHashValue("xyz");
        book.setDescription(
            "This book shows you how to develop with the spring framework.");
        book.setVersion("1.0");
        book.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.JUNE, 21);
        book.setDate(new Date(c.getTimeInMillis()));
        book.setAuthorName("Rod Johnson, Juergen Hoeller");
        book.setPublisher("Wrox");
        book.setPageNum(576);
        book.setIsbnNumber("0764558315");

        KeywordDto keyword = new KeywordDto();
        keyword.setName("spring");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("j2ee");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("framework");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        book.setKeywords(listKeywords);

        book = (BookDto) service.saveReference(book);

        BookDto book2 = (BookDto) service.getReferenceByKey(book.getKey());
        assertTrue("Books are not equal.", book.equals(book2));

        Set listKeywords2 = book2.getKeywords();
        assertEquals("There are not three keywords related with the book.", 
            3, listKeywords2.size());
        Iterator it = listKeywords2.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword2) 
                || k.equals(keyword3))) {
                fail("There was an unexpected keyword related with book.");
            }
        }

        service.removeReference(book2.getKey());
        List list = service.getReferencesByName(
            "Expert One-on-One J2EE Development without EJB");
        assertEquals("There is still a formal publication with name 'Expert "
            + "One-on-One J2EE Development without EJB'.", list.size(), 0);
    }

    /**
     * This test adds a book with three keywords. After saving the book the same
     * book will be get from database. This book get a new name and one keyword
     * will be replaced by another. Afterwards changes will be saved to
     * database.
     */
    public void testInsertChangeBook() {
        ReferenceService service = getReferenceService();
        BookDto book = new BookDto();
        book.setName("Expert One-on-One J2EE Development without EJB");
        book.setHashValue("xyz");
        book.setDescription(
            "This book shows you how to develop with the spring framework.");
        book.setVersion("1.0");
        book.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.JUNE, 21);
        book.setDate(new Date(c.getTimeInMillis()));
        book.setAuthorName("Rod Johnson, Juergen Hoeller");
        book.setPublisher("Wrox");
        book.setPageNum(576);
        book.setIsbnNumber("0764558315");

        KeywordDto keyword = new KeywordDto();
        keyword.setName("spring");
        KeywordDto keyword2 = new KeywordDto();
        keyword2.setName("j2ee");
        KeywordDto keyword3 = new KeywordDto();
        keyword3.setName("framework");
        service.saveKeyword(keyword);
        service.saveKeyword(keyword2);
        service.saveKeyword(keyword3);

        Set listKeywords = new HashSet();
        listKeywords.add(keyword);
        listKeywords.add(keyword2);
        listKeywords.add(keyword3);
        book.setKeywords(listKeywords);

        book = (BookDto) service.saveReference(book);

        List list = service.getReferencesByName(
            "Expert One-on-One J2EE Development without EJB");
        assertEquals("Not one book with name 'Expert One-on-One "
            + "J2EE Development without EJB'.", 1, list.size());
        BookDto book2 = (BookDto) list.get(0);
        assertTrue("Books are not equal.", book.equals(book2));

        Set listKeywords2 = book2.getKeywords();
        assertEquals("There are not three keywords related with the book.", 
            3, listKeywords2.size());
        Iterator it = listKeywords2.iterator();
        while (it.hasNext()) {
            KeywordDto k = (KeywordDto) it.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword2) 
                || k.equals(keyword3))) {
                fail("There was an unexpected keyword related with book.");
            }
        }
        listKeywords2.remove(keyword2);
        KeywordDto keyword5 = new KeywordDto();
        keyword5.setName("New");
        keyword5.setDescription("A brand new keyword.");
        keyword5 = service.saveKeyword(keyword5);
        listKeywords2.add(keyword5);

        book2.setName("Springframework - J2EE Development without EJB");
        book2.setKeywords(listKeywords2);
        book2 = (BookDto) service.saveReference(book2);

        List emptyList = service.getReferencesByName("Expert One-on-One J2EE "
            + "Development without EJB");
        assertEquals("Formal publication with name 'Expert One-on-One J2EE "
            + "Development without EJB' still exists.", emptyList.size(), 0);

        List list2 = service.getReferencesByName("Springframework - J2EE "
            + "Development without EJB");
        assertEquals("Not one book with name 'Springframework - J2EE "
            + "Development without EJB'.", 1, list2.size());
        BookDto book3 = (BookDto) list.get(0);
        assertTrue("Books are not equal.", book2.equals(book3));

        Set listKeywords3 = book3.getKeywords();
        assertEquals("There are not three keywords related with the book.", 
            3, listKeywords3.size());
        Iterator it2 = listKeywords3.iterator();
        while (it2.hasNext()) {
            KeywordDto k = (KeywordDto) it2.next();
            if (!(k.equals(keyword) 
                || k.equals(keyword3) 
                || k.equals(keyword5))) {
                fail("There was an unexpected keyword related with book.");
            }
        }
    }

    /**
     * This test adds two books and gets them.
     */
    public void testGetAllBooks() {
        ReferenceService service = getReferenceService();
        BookDto book = new BookDto();
        book.setName("Expert One-on-One J2EE Development without EJB");
        book.setHashValue("xyz");
        book.setDescription(
            "This book shows you how to develop with the spring framework.");
        book.setVersion("1.0");
        book.setIncomplete(false);
        Calendar c = Calendar.getInstance();
        c.set(2004, Calendar.JUNE, 21);
        book.setDate(new Date(c.getTimeInMillis()));
        book.setAuthorName("Rod Johnson, Juergen Hoeller");
        book.setPublisher("Wrox");
        book.setPageNum(576);
        book.setIsbnNumber("0764558315");
        book = (BookDto) service.saveReference(book);

        BookDto book2 = new BookDto();
        book2.setName("The Complete Log4j Manual");
        book2.setHashValue("lkjh");
        book2.setDescription("This book shows you how to use Log4J.");
        book2.setVersion("1.0");
        book2.setIncomplete(false);
        Calendar c2 = Calendar.getInstance();
        c2.set(2003, Calendar.MAY, 7);
        book2.setDate(new Date(c2.getTimeInMillis()));
        book2.setAuthorName("Ceki Gulcu");
        book2.setPublisher("QOS.ch");
        book2.setPageNum(206);
        book2.setIsbnNumber("2970036908");
        book2 = (BookDto) service.saveReference(book2);

        List list = service.getAllReferences();
        assertEquals("There are not two books.", 2, list.size());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            BookDto book3 = (BookDto) it.next();
            if (!(book3.equals(book) || book3.equals(book2))) {
                fail("There is an unexpected book.");
            }
        }

        service.removeReference(book.getKey());

        list = service.getAllReferences();
        assertEquals("There is not one book.", 1, list.size());
        BookDto book4 = (BookDto) list.get(0);
        assertTrue("There is an unexpected book.", book4.equals(book2));
    }

    /**
     * This test tries out many possible combinations of searching on
     * references.
     */
    public void testSearchReferences() {
        ReferenceService service = getReferenceService();

        KeywordDto kUml = createAndSaveKeyword(
            "uml", "Unified modeling language.", service);
        KeywordDto kLanguage = createAndSaveKeyword(
            "language", "Program or design language.", service);
        KeywordDto kJsp = createAndSaveKeyword(
            "jsp", "Java server pages.", service);
        KeywordDto kJava = createAndSaveKeyword(
            "java", "Java program language.", service);
        KeywordDto kStruts = createAndSaveKeyword(
            "struts", "Web MVC product.", service);
        KeywordDto kDictionary = createAndSaveKeyword(
            "dictionary", "Dictionary.", service);
        KeywordDto kJ2ee = createAndSaveKeyword(
            "j2ee", "Java enterprise library.", service);
        KeywordDto kIbatis = createAndSaveKeyword(
            "ibatis", "Apache IBatis software.", service);
        KeywordDto kDeveloperguide = createAndSaveKeyword(
            "developerguide", "Guide for developers.", service);
        KeywordDto kBarcode = createAndSaveKeyword(
            "barcode", "Machine readable string.", service);
        KeywordDto k2d = createAndSaveKeyword(
            "2d", "Two dimensions.", service);
        KeywordDto kEnglish = createAndSaveKeyword(
            "english", "English language.", service);
        KeywordDto kGerman = createAndSaveKeyword(
            "german", "German language.", service);
        createAndSaveKeyword("zombie", "", service);
        
        
        BookDto bUmlDistilled = new BookDto();
        bUmlDistilled.setName("UML Distilled");
        bUmlDistilled.setDescription(
            "A brief guide to the standard object modeling language.");
        bUmlDistilled.setIncomplete(false);
        Set keywordsUmlDistilled = new HashSet();
        keywordsUmlDistilled.add(kUml);
        keywordsUmlDistilled.add(kLanguage);
        keywordsUmlDistilled.add(kEnglish);
        bUmlDistilled.setKeywords(keywordsUmlDistilled);

        BookDto bBeginningJsp2 = new BookDto();
        bBeginningJsp2.setName("Beginning Jsp 2.0");
        bBeginningJsp2.setDescription(
            "Build Web Applications Using Jsp, Java, and Struts.");
        bBeginningJsp2.setIncomplete(true);
        Set keywordsBeginningJsp2 = new HashSet();
        keywordsBeginningJsp2.add(kJ2ee);
        keywordsBeginningJsp2.add(kJava);
        keywordsBeginningJsp2.add(kJsp);
        keywordsBeginningJsp2.add(kStruts);
        keywordsBeginningJsp2.add(kEnglish);
        bBeginningJsp2.setKeywords(keywordsBeginningJsp2);

        BookDto bZombie = new BookDto();
        bZombie.setName("Zombie");

        LinkDto lLeoEngGer = new LinkDto();
        lLeoEngGer.setName("LEO Dictionary English-German");
        lLeoEngGer.setDescription(
            "An online dictionary to translate German to English and reverse.");
        lLeoEngGer.setIncomplete(false);
        Set keywordsLeoEngGer = new HashSet();
        keywordsLeoEngGer.add(kEnglish);
        keywordsLeoEngGer.add(kGerman);
        keywordsLeoEngGer.add(kDictionary);
        keywordsLeoEngGer.add(kLanguage);
        lLeoEngGer.setKeywords(keywordsLeoEngGer);

        LinkDto lJ2eeJsp = new LinkDto();
        lJ2eeJsp.setName("J2EE - JavaServer Pages Technology");
        lJ2eeJsp.setDescription("JavaServer Pages (JSP) technology provides "
            + "a simplified, fast way to create dynamic web content. "
            + "JSP technology enables rapid development of web-based "
            + "applications that are server- and platform-independent.");
        lJ2eeJsp.setIncomplete(true);
        Set keywordsJ2eeJsp = new HashSet();
        keywordsJ2eeJsp.add(kJ2ee);
        keywordsJ2eeJsp.add(kJsp);
        keywordsJ2eeJsp.add(kJava);
        keywordsJ2eeJsp.add(kEnglish);
        lJ2eeJsp.setKeywords(keywordsJ2eeJsp);

        FormalPublicationDto fSqlMaps2DevGuide = new FormalPublicationDto();
        fSqlMaps2DevGuide.setName("iBatis SqlMap 2.0 Developer Guide");
        fSqlMaps2DevGuide.setDescription("This guide shows you how to "
            + "develop an application using SqlMap 2.0.");
        fSqlMaps2DevGuide.setIncomplete(true);
        Set keywordsSqlMaps2DevGuide = new HashSet();
        keywordsSqlMaps2DevGuide.add(kJava);
        keywordsSqlMaps2DevGuide.add(kIbatis);
        keywordsSqlMaps2DevGuide.add(kDeveloperguide);
        keywordsSqlMaps2DevGuide.add(kEnglish);
        fSqlMaps2DevGuide.setKeywords(keywordsSqlMaps2DevGuide);

        FormalPublicationDto f2dBarcodePdf417 = new FormalPublicationDto();
        f2dBarcodePdf417.setName("2D barcode PDF417");
        f2dBarcodePdf417.setDescription("Describse who a "
            + "PDF417 2D barcode is built-up.");
        f2dBarcodePdf417.setIncomplete(false);
        Set keywords2dBarcodePdf417 = new HashSet();
        keywords2dBarcodePdf417.add(k2d);
        keywords2dBarcodePdf417.add(kBarcode);
        keywords2dBarcodePdf417.add(kEnglish);
        f2dBarcodePdf417.setKeywords(keywords2dBarcodePdf417);

        service.saveReference(bUmlDistilled);
        service.saveReference(bBeginningJsp2);
        service.saveReference(bZombie);
        service.saveReference(lLeoEngGer);
        service.saveReference(lJ2eeJsp);
        service.saveReference(fSqlMaps2DevGuide);
        service.saveReference(f2dBarcodePdf417);

        List list;
        Iterator it;

        QueryObject query = new QueryObject();
        list = service.searchReferences(query);
        assertEquals("There are not seven references for "
            + "query name='' and description=''.", 7, list.size());

        query = new QueryObject();
        query.addCriteria(ComparisonCriteria.equals("incomplete", false));
        list = service.searchReferences(query);
        assertEquals("There are not three references for query name='' and "
            + "description='' and incomplete=false.", 3, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            ReferenceDto r = (ReferenceDto) it.next();
            if (!(r.equals(bUmlDistilled) 
                || r.equals(lLeoEngGer) 
                || r.equals(f2dBarcodePdf417))) {
                fail("There was an unexpected reference.");
            }
        }

        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive("name", "%2%"));
        list = service.searchReferences(query);
        assertEquals("There are not four references for "
            + "query name='2' and description=''.", 4, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            ReferenceDto r = (ReferenceDto) it.next();
            if (!(r.equals(bBeginningJsp2) || r.equals(lJ2eeJsp)
                || r.equals(fSqlMaps2DevGuide) 
                || r.equals(f2dBarcodePdf417))) {
                fail("There was an unexpected reference.");
            }
        }

        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive("name", "%2%"));
        query.addCriteria(ComparisonCriteria.equals("incomplete", true));
        list = service.searchReferences(query);
        assertEquals("There are not three references for query name='2' and "
            + "description='' and incomplete=true.", 3, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            ReferenceDto r = (ReferenceDto) it.next();
            if (!(r.equals(bBeginningJsp2) 
                || r.equals(lJ2eeJsp) 
                || r.equals(fSqlMaps2DevGuide))) {
                fail("There was an unexpected reference.");
            }
        }

        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive(
            "description", "%deVeLop%"));
        list = service.searchReferences(query);
        assertEquals("There are not two references for query name='' and " 
            + "description='deVeLop'.", 2, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            ReferenceDto r = (ReferenceDto) it.next();
            if (!(r.equals(lJ2eeJsp) || r.equals(fSqlMaps2DevGuide))) {
                fail("There was an unexpected reference.");
            }
        }

        query = new QueryObject();
        query.addCriteria(LikeCriteria.caseInsensitive("name", "%JAVA%"));
        query.addCriteria(LikeCriteria.caseInsensitive("description", "%WEB%"));
        list = service.searchReferences(query);
        assertEquals("There was not one reference for "
            + "query name='JAVA' and description='WEB'.", 1, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            ReferenceDto r = (ReferenceDto) it.next();
            if (!r.equals(lJ2eeJsp)) {
                fail("There was an unexpected reference.");
            }
        }
        
        query = new QueryObject();
        
        query.addCriteria(new IncludeCriteria("keywords",
            kJava.getKeyAsObject()));
        
        list = service.searchReferences(query);
        assertEquals("Unexpected number of references with keyword java.", 
            3, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            ReferenceDto r = (ReferenceDto) it.next();
            if (!(r.equals(bBeginningJsp2) || r.equals(lJ2eeJsp) 
                || r.equals(fSqlMaps2DevGuide))) {
                fail("There was an unexpected reference.");
            }
        }
        
        query = new QueryObject();
        
        query.addCriteria(new IncludeCriteria("keywords", 
            kJava.getKeyAsObject()));
        
        query.addCriteria(LikeCriteria.caseInsensitive("description", "%WEB%"));
        list = service.searchReferences(query);
        assertEquals("Unexpected number of references with keyword java and "
            + "description='WEB'.", 
            2, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            ReferenceDto r = (ReferenceDto) it.next();
            if (!(r.equals(bBeginningJsp2) || r.equals(lJ2eeJsp))) {
                fail("There was an unexpected reference.");
            }
        }
        
        query = new QueryObject();
        
        query.addCriteria(new IncludeCriteria("keywords",
            kJava.getKeyAsObject()));
        query.addCriteria(new IncludeCriteria("keywords", 
            kStruts.getKeyAsObject()));
        
        query.addCriteria(LikeCriteria.caseInsensitive("description", "%WEB%"));
        list = service.searchReferences(query);
        assertEquals("Unexpected number of references with keyword java, "
            + "keyword struts and description='WEB'.", 
            1, list.size());
        it = list.iterator();
        while (it.hasNext()) {
            ReferenceDto r = (ReferenceDto) it.next();
            if (!r.equals(bBeginningJsp2)) {
                fail("There was an unexpected reference.");
            }
        }
    }

    /**
     * Creates, saves and returns a keyword by using given parameters.
     * 
     * @param name Is the name of the keyword to create.
     * @param description Is the description of the keyword to create.
     * @param service Is the service where to save the created keyword.
     * @return Returns the created and saved keyword.
     */
    protected KeywordDto createAndSaveKeyword(String name, String description, 
        ReferenceService service) {
        KeywordDto keyword = new KeywordDto();
        keyword.setName(name);
        keyword.setDescription(description);
        return service.saveKeyword(keyword);
    }
    
    /**
     * This test adds a file and gets a FileDescriptorView. This view will be
     * modified and saved. The file will be get as normal file and as
     * FileDescriptorView.
     */
    public void testInsertModifyFileWithFileDescriptorView() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        ReferenceService service = getReferenceService();
        FileDto file = new FileDto();
        file.setKeyToReference(fakeReferenceKey);
        file.setName("iBatis Developer Guide");
        file.setMimeType("text/plain");
        byte[] content = "This is only a test content.".getBytes();
        file.setContent(content);
        FileDescriptorView fileView 
            = service.saveFileAndReturnFileDescriptorView(file);

        fileView.setName("iBatis SqlMap 2.0 Developer Guide");
        service.modifyFileDescriptorView(fileView);

        FileDto file2 = (FileDto) service.getFilesByName(
            "iBatis SqlMap 2.0 Developer Guide").get(0);
        file.setName("iBatis SqlMap 2.0 Developer Guide");
        assertTrue("Files are not equals.", file.equals(file2));

        FileDescriptorView fileView2 
            = (FileDescriptorView) service.getFileDescriptorViewsByReference(
                fakeReferenceKey).get(0);
        assertTrue("FileDescriptorViews are not equals.", 
            fileView.equals(fileView2));
    }

    /**
     * This test tries to insert an annotation without a content. Because the
     * annotation and its content are written in two steps, the first step
     * should be redone if the second step fails.
     * 
     * This test will be executed on service layer level, because transaction
     * attributes are defined on service layer.
     */
    public void testIncompleteAnnotationInsertion() {
        int fakeReferenceKey = addDefaultFakeReference();
        
        ReferenceService service = getReferenceService();
        AnnotationDto annotation = new AnnotationDto();
        annotation.setKeyToReference(fakeReferenceKey);
        annotation.setAnnotator("Mister Lazy");
        annotation.setGrade(1);
        // Extra do not add a content.
        annotation.setContent(null);
        try {
            annotation = service.saveAnnotation(annotation);
            fail("An annotation with no content is allowed.");
        } catch (DataAccessException e) {
            s_logger.debug("Expected exception because content of an "
                + "annotation can not be null.", e);
        }

        List list = service.getAnnotationsByAnnotator("Mister Lazy");
        assertEquals("There is still an annotation "
            + "with annotator 'Mister Lazy' in database.", list.size(), 0);
    }
}
//Checkstyle: MagicNumber on
