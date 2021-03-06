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

package ch.elca.el4j.services.remoting.protocol.ejb.exception;


/**
 * Interface used to internationalize exceptions. It is implemented by
 * {@link BaseRTException}.
 * <p>
 * This interface enables the control of format string and parameters for the
 * use of {@link java.text.MessageFormat}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Yves Martin (YMA)
 **/
public interface InternationalizableException {

    /**
     * Returns the message pattern for <code>MessageFormat</code>.
     *
     * @return the message of this exception (without any parameters
     * substituted in it).
     */
    public String getFormatString();


    /**
     * Sets a new format String. It replaces the default formatString by an
     * internationalized String.
     *
     * @param formatString replaces the message
     */
    public void setFormatString(String formatString);

    /**
     * Gets parameters defined for the message.
     *
     * @return array of arguments for <code>MessageFormat</code>
     */
    public Object[] getFormatParameters();
}
