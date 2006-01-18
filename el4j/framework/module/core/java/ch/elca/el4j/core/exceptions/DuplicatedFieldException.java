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
 * This exception will be thrown when a field, which has to be unique, has
 * already the same value as the given.
 * 
 * <script type="text/javascript">printFileStatus 
 * ("$Source$",
 *  "$Revision$",
 *  "$Date$",
 *  "$Author$" );
 * </script>
 *
 * @author Martin Zeltner (MZE)
 * @deprecated Spring data access exception will be used.
 * @see org.springframework.dao.DataIntegrityViolationException
 */
public class DuplicatedFieldException extends BaseException {
    /**
     * Message, which has to be formated.
     */
    public static final String EXCEPTION_MESSAGE_FIELDDUPLICATED 
        = "A {0} with the same {1} already exists in database.";

    /**
     * Name of the object, where the exception occured.
     */
    protected String m_objectName;

    /**
     * Name of the field, which would be duplicated.
     */
    protected String m_fieldName;

    /**
     * Constructor.
     * 
     * @param objectName
     *            Is the name of the object, where the exception occured.
     * @param fieldName
     *            Is the name of the field, which would be duplicated.
     */
    public DuplicatedFieldException(String objectName, String fieldName) {
        this(objectName, fieldName, (Throwable) null);
    }

    /**
     * Constructor.
     * 
     * @param objectName
     *            Is the name of the object, where the exception occured.
     * @param fieldName
     *            Is the name of the field, which would be duplicated.
     * @param cause
     *            Is the cause for this exception.
     */
    public DuplicatedFieldException(String objectName, String fieldName,
            Throwable cause) {
        super(EXCEPTION_MESSAGE_FIELDDUPLICATED, cause);
        m_objectName = objectName;
        m_fieldName = fieldName;
    }

    /**
     * @return Returns the objectName.
     */
    public String getObjectName() {
        return m_objectName;
    }

    /**
     * @return Returns the fieldName.
     */
    public String getFieldName() {
        return m_fieldName;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getFormatParameters() {
        return new Object[] {m_objectName, m_fieldName};
    }
}