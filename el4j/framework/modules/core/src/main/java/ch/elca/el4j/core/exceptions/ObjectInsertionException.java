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
package ch.elca.el4j.core.exceptions;

/**
 * This exception will be thrown when insertion of an object failed.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Martin Zeltner (MZE)
 * @deprecated Spring data access exception will be used.
 * @see ch.elca.el4j.services.persistence.generic.exceptions.InsertionFailureException
 */
public class ObjectInsertionException extends BaseException {
	/**
	 * Message, which has to be formated.
	 */
	public static final String EXCEPTION_MESSAGE_OBJECTINSERTION
		= "Insertion of {0} failed.";

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
	public ObjectInsertionException(String objectName) {
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
	public ObjectInsertionException(String objectName, Throwable cause) {
		super(EXCEPTION_MESSAGE_OBJECTINSERTION, cause);
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