/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.persistence.generic.dao;

/**
 * Notifies registered observers of repository changes.
 * 
 * The notifications sent are consistent.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public interface RepositoryChangeNotifier {
    /** A fuzzy change object so callers do not have to construct their own. */
    public static final Change FUZZY_CHANGE = new Change();

    /**
     * Sent if something in this repository view may have changed.
     **/
    public static class Change { }
    
    /**
     * Something about {@link #changee} may have changed. 
     */
    public static class EntityChange extends Change {
        /** See class documentation. */
        public Object changee;
    }
    
    /** The {@link #changee} has new state. */
    public static class NewEntityState extends EntityChange { }
    
    /** The {@link #changee}'s state has changed. */
    public static class EntityStateChanged extends NewEntityState { }
    
    /**
     * The {@link #changee} has been inserted.
     */
    public static class EntityInserted extends NewEntityState { }
    
    /**
     * The {@link #changee} has been deleted.
     */
    public static class EntityDeleted extends EntityChange { }

    
    /** 
     * Causes {@code cl} to receive future change notifications.
     */
    public void subscribe(RepositoryChangeListener cl);
    
    /** 
     * Causes {@code cl} to no longer receive future change notifications.
     */
    public void unsubscribe(RepositoryChangeListener cl);
    
    /**
     * Announces {@code change} to all subscribed observers.
     **/
    public void announce(Change change);    
}