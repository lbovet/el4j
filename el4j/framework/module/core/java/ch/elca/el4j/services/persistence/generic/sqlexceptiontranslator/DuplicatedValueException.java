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
package ch.elca.el4j.services.persistence.generic.sqlexceptiontranslator;

import java.sql.SQLException;

import org.springframework.dao.DataAccessException;

/**
 * This exception will be thrown when a value, which has to be unique, already
 * exists.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 * @deprecated Spring data access exception will be used.
 * @see org.springframework.dao.DataIntegrityViolationException
 */
public class DuplicatedValueException extends DataAccessException {

    /**
     * Default constructor.
     * 
     * @param message Is the exception message.
     * @param e Is the received target exception.
     */
    public DuplicatedValueException(String message, SQLException e) {
        super(message, e);
    }
}