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
package ch.elca.el4j.services.richclient.context;

/**
 * An awaker is a bean "factory" that merely awakens beans created by someone 
 * else, instead of both creating and initializing them itself.
 * 
 * <p>Awakening an object injects needed dependencies and gives the object the 
 * opportunity to initialize itself. Like Spring's bean factories, the object's
 * dynamic type is queried for marker interfaces stating the needed 
 * initialization steps.  
 *   
 * <p>Objects should validate their configuration and initialize themselves when
 * waking.
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
public interface Awaker {
    /** requests the awakening of <code>o</code>. @param o .*/
    void awaken(Object o);
}
