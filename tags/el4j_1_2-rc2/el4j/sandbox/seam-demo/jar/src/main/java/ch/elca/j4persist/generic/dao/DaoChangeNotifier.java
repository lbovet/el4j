/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.j4persist.generic.dao;

/**
 * Notifies registered observers of DAO changes.
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
public interface DaoChangeNotifier {
    /** A fuzzy change object so callers do not have to construct their own. */
    public static final Change FUZZY_CHANGE = new Change();

    /**
     * Sent if something in this DAO view may have changed.
     **/
    public static class Change { }
    
    /**
     * Something about {@link #m_changee} may have changed. 
     */
    public static class EntityChange extends Change {
        /** See class documentation. */
        private Object m_changee;

        /**
         * @return Returns the changee.
         */
        public Object getChangee() {
            return m_changee;
        }

        /**
         * @param changee Is the changee to set.
         */
        public void setChangee(Object changee) {
            m_changee = changee;
        }   
    }
    
    /** The {@link #m_changee} has new state. */
    public static class NewEntityState extends EntityChange { }
    
    /** The {@link #m_changee}'s state has changed. */
    public static class EntityStateChanged extends NewEntityState { }
    
    /**
     * The {@link #m_changee} has been inserted.
     */
    public static class EntityInserted extends NewEntityState { }
    
    /**
     * The {@link #m_changee} has been deleted.
     */
    public static class EntityDeleted extends EntityChange { }

    
    /** 
     * Causes {@code cl} to receive future change notifications.
     * @param cl The DaoChangeListener to subscribe
     */
    public void subscribe(DaoChangeListener cl);
    
    /** 
     * Causes {@code cl} to no longer receive future change notifications.
     * @param cl The DaoChangeListener to unsubscribe
     */
    public void unsubscribe(DaoChangeListener cl);
    
    /**
     * Announces {@code change} to all subscribed observers.
     * @param change The change to announce
     */
    public void announce(Change change);    
}