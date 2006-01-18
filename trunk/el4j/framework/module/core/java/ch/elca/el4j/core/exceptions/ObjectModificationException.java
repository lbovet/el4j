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
package ch.elca.el4j.core.exceptions;

/**
 * This exception will be thrown when the optimistic locking detects a
 * modification.
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
 * @see org.springframework.dao.OptimisticLockingFailureException
 */
public class ObjectModificationException extends BaseException {
    /**
     * Message, which has to be formated.
     */
    public static final String EXCEPTION_MESSAGE_OBJECTMODIFICATION 
        = "{0} was modificated or deleted since meantime.";

    /**
     * Name of the object, where the exception occured.
     */
    protected String m_objectName;

    /**
     * Constructor.
     * 
     * @param objectName
     *            Is the name of the object, where the exception occured.
     */
    public ObjectModificationException(String objectName) {
        this(objectName, (Throwable) null);
    }

    /**
     * Constructor.
     * 
     * @param objectName
     *            Is the name of the object, where the exception occured.
     * @param cause
     *            Is the cause for this exception.
     */
    public ObjectModificationException(String objectName, Throwable cause) {
        super(EXCEPTION_MESSAGE_OBJECTMODIFICATION, cause);
        m_objectName = objectName;
    }

    /**
     * @return Returns the objectName.
     */
    public String getObjectName() {
        return m_objectName;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getFormatParameters() {
        return new Object[] {m_objectName};
    }
}