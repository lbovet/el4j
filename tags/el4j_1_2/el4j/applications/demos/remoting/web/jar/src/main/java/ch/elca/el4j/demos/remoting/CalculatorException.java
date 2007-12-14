/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.demos.remoting;

import ch.elca.el4j.core.exceptions.BaseException;

/**
 * This exception is used for testing.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class CalculatorException extends BaseException {
    /**
     * Default constructor.
     */
    public CalculatorException() {
        super("This is a test exception.", (Object[]) null);
    }
    
    /**
     * A setter for the message property is needed as XFire cannot initialize
     * the exception otherwise.
     * @param message The message of this Exception
     */
    public void setMessage(String message) {
        m_message = message;
    }

}