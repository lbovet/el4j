/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
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

package ch.elca.el4j.services.exceptionhandler;

import org.springframework.aop.target.HotSwappableTargetSource;

/**
 * This exception signals a retry.
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
public class RetryException extends Exception {

    /** The number of retries. */
    private int m_retries;
    
    /** 
     * The hot swappable target source which target has to be used in the
     * next invocation.
     */
    private HotSwappableTargetSource m_swapper;
    
    /**
     * Creates a new retry exception.
     * 
     * @param retries
     *      The number of expected retries. Catchers are free to use another
     *      value.
     */
    public RetryException(int retries) {
        this(retries, null);
    }

    /**
     * Creates a new retry exception with the given number of retires and the
     * target source, which target has to be used in the next invocation.
     * 
     * @param retries
     *      The number of expected retries.
     *      
     * @param swapper
     *      The target source that points to the target which has to be used in
     *      the next invocation.
     */
    public RetryException(int retries, HotSwappableTargetSource swapper) {
        m_retries = retries;
        m_swapper = swapper;
    }
    
    /**
     * @return Returns the number of retries.
     */
    public int getRetries() {
        return m_retries;
    }

    /**
     * @return Returns the target source which target has to be used in the 
     *      next invocation.
     */
    public HotSwappableTargetSource getSwapper() {
        return m_swapper;
    }
}
