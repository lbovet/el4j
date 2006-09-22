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
package ch.elca.el4j.tests.services.remoting.loadbalancing.common;

/**
 * 
 * This interface specifies the methods that can be called on a
 * business object used exclusively for testing purposes. By deliberate
 * choice, this object does not represent a meaningful application.
 * Rather, its methods highlight the properties of the tested module.
 *
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Pleisch (SPL)
 */
public interface BusinessObject {
    
    /** Triggers server suicide. */
    public static final String COMMIT_SUICIDE = "commit_suicide";
    
    /**
     * Stores the name-value pair toto-toto in the DB and returns toto.
     */
    public String call(String toto);
  
} // Interface BusinessObject
