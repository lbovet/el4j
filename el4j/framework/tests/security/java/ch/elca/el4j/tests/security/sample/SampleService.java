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
package ch.elca.el4j.tests.security.sample;


/**
 * Sample service for access tests.
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *   "$Revision$", "$Date$", "$Author$"
 * );</script>
 * 
 * @author Andreas Pfenninger (APR)
 * 
 * @@net.sf.acegisecurity.SecurityConfig("ROLE_TELLER")
 */
public interface SampleService  {

    /**
     * @param i a numer.
     * @return i + 1
     * 
     * @@net.sf.acegisecurity.SecurityConfig("ROLE_PERMISSION_ADDONE")
     */
    public int addOne(int i);

}
