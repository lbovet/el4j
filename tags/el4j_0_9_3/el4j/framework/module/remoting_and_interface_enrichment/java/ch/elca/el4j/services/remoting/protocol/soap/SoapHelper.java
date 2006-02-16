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

package ch.elca.el4j.services.remoting.protocol.soap;


/**
 * Helper for soap services.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public final class SoapHelper {
    /**
     * This field contains the last server side stack trace of the current 
     * thread.
     */
    private static ThreadLocal s_lastServerSideStackTrace = new ThreadLocal();
    
    /**
     * Hide default constructor.
     */
    private SoapHelper() { }
    
    /**
     * @return Returns the last server side stack trace of the current thread.
     */
    public static String getLastServerSideStackTrace() {
        return (String) s_lastServerSideStackTrace.get();
    }
    
    /**
     * @param stackTrace Is the stack trace to set.
     */
    public static void setLastServerSideStackTrace(String stackTrace) {
        s_lastServerSideStackTrace.set(stackTrace);
    }
}
