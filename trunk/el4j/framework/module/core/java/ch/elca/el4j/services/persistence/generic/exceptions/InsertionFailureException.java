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

package ch.elca.el4j.services.persistence.generic.exceptions;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * This data access exception will be thrown in business methods, if data could
 * not be inserted.
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
public class InsertionFailureException
    extends InvalidDataAccessResourceUsageException {

    /**
     * Constructor.
     * 
     * @param msg
     *            Is the exception message.
     */
    public InsertionFailureException(String msg) {
        super(msg);
    }

    /**
     * Constructor.
     * 
     * @param msg
     *            Is the exception message.
     * @param ex
     *            Is the cause for this exception.
     */
    public InsertionFailureException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
