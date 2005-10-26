/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://EL4J.sf.net
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

package ch.elca.el4j.core.exceptions;

/**
 * General exception used if a misconfiguration occurred. 
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
public class MisconfigurationRTException extends BaseRTException {

    /**
     * Constructor.
     * 
     * @param message Is the message of this exception.
     */
    public MisconfigurationRTException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message Is the message of this exception.
     * @param exception Is the cause of this exception.
     */
    public MisconfigurationRTException(String message, Throwable exception) {
        super(message, exception);
    }
}
