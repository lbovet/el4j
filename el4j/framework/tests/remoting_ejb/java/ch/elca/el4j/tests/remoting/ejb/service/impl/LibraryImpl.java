/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.tests.remoting.ejb.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.SessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.tests.remoting.ejb.service.Library;

/**
 * Library implementation used for testing.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class LibraryImpl implements Library, Serializable {

    /** An offset to prevent collisions. */
    private static final int OFFSET = 100;
    
    /** The static logger instance. */
    private static Log s_logger = LogFactory.getLog(LibraryImpl.class);
    
    /** The map wit all books. */
    private HashMap m_books = new HashMap();
    
    /**
     * {@inheritDoc}
     */
    public void init(Object[] objs) {
        s_logger.info("create(Object[]) called");
        for (int i = 0; i < objs.length; i++) {
            putBook(i + OFFSET, objs[i].toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean putBook(int id, String bookTitle) {
        Object obj = m_books.put(new Integer(id), bookTitle);
        return (obj == null ? true : false);
    }

    /**
     * {@inheritDoc}
     */
    public String getBookTitle(int id) {
        return (String) m_books.get(new Integer(id));
    }

    /**
     * {@inheritDoc}
     */
    public int getSize() {
        s_logger.info("getSize()");
        return m_books.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getAllBooks() {
        return (String[]) m_books.values().toArray(new String[m_books.size()]);
    }

    /**
     * {@inheritDoc}
     */
    public void putAllBooks(Map books) {
        s_logger.info("add all books");
        for (Iterator iter = books.entrySet().iterator(); iter.hasNext();) {
            Map.Entry next = (Map.Entry) iter.next();
            this.m_books.put(next.getKey(), next.getValue());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void activate() {
        s_logger.info("activate()");
    }
    
    /**
     * {@inheritDoc}
     */
    public void beanAfterBegin() {
        s_logger.info("beanAfterBegin()");
    }

    /**
     * {@inheritDoc}
     */
    public void beanAfterCompletion(boolean commited) {
        s_logger.info("beanAfterCompletion(boolean), with value " + commited);
    }

    /**
     * {@inheritDoc}
     */
    public void beanBeforeCompletion() {
        s_logger.info("beanBeforeCompletion()");
    }

    /**
     * {@inheritDoc}
     */
    public void passivate() {
        s_logger.info("passivate()");
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBeanSessionContext(SessionContext context) {
        s_logger.info("setSessionCotnext(" + context + ")");
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeBean() {
        s_logger.info("removeBean called");
    }
}
