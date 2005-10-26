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

package ch.elca.el4j.tests.remoting.ejb.service;

import java.util.Map;

import javax.ejb.SessionContext;

/**
 * Example class for testing EJB integration.
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
public interface Library {
    
    /**
     * Adds a book with the given id to the library.
     * 
     * @param id
     *      The book's id.
     *      
     * @param bookTitle
     *      The book's title.
     *      
     * @return Returns whether the method ended successfully.
     */
    public boolean putBook(int id, String bookTitle);

    /**
     * Returns the book's title with the given id.
     * 
     * @param id
     *      The id of the book which name is requested.
     * 
     * @return Returns the book title.
     */
    public String getBookTitle(int id);
    
    /**
     * @return Returns the number of books hold in this library.
     */
    public int getSize();
    
    /**
     * @return Returns all books stored in the library.
     */
    public String[] getAllBooks();
    
    /**
     * Puts a number of &lt;id, title&gt; pairs into the library.
     * 
     * @param books
     *      The books to add.
     */
    public void putAllBooks(Map books);
    
    /**
     * Method where <code>ejbCreate(Object[])</code> calls are delegated to. It
     * initializes the library and adds the book titles given as string array
     * to the library.
     *  
     * @param objs
     *      An array of strings holding the books' names.
     */
    public void init(Object[] objs);
    
    /**
     * Method where <code>ejbActivate()</code> calls are delegated to.
     */
    public void activate();
    
    /**
     * Method where <code>ejbPassivate()</code> calls are delegated to.
     */
    public void passivate();
    
    /**
     * Method where <code>afterBegin()</code> calls are delegated to.
     */
    public void beanAfterBegin();
    
    /**
     * Method where <code>beforeCompletion()</code> calls are delegated to.
     */
    public void beanBeforeCompletion();
    
    /**
     * Method where <code>afterCompletion(boolean)</code> calls are delegated
     * to.
     * 
     * @param committed
     *      <code>true</code> if the transaction was committed,
     *      <code>false</code> otherwise.
     */
    public void beanAfterCompletion(boolean committed);
    
    /**
     * Method where <code>setSessionContext(SessionContext)</code> calls are
     * delegated to.
     * 
     * @param context
     *      The session context.
     */
    public void setBeanSessionContext(SessionContext context);
    
    /**
     * Method where <code>ejbRemove()</code> calls are delegated to.
     */
    public void removeBean();
}
