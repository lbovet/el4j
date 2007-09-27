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
 * This object receives change notifications from DAO change notifiers.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/modules/core/src/main/java/ch/elca/el4j/services/persistence/generic/dao/DaoChangeListener.java $",
 *    "$Revision: 1436 $",
 *    "$Date: 2006-10-31 15:42:42 +0100 (Tue, 31 Oct 2006) $",
 *    "$Author: swisswheel $"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 * @see DaoChangeNotifier
 */
public interface DaoChangeListener {
    /** 
     * Invoked if a DAO change was detected.
     * @param change The change detected.
     */
    void changed(DaoChangeNotifier.Change change);
}
