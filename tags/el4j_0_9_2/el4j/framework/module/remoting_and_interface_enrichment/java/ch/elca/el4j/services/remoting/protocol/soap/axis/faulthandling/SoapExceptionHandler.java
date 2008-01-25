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

package ch.elca.el4j.services.remoting.protocol.soap.axis.faulthandling;

import javax.xml.rpc.soap.SOAPFaultException;

import org.apache.axis.AxisFault;

/**
 * Handler to translate exceptions.
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
public interface SoapExceptionHandler {
    /**
     * Method to translate a given business exception into a soap fault
     * exception.
     * 
     * @param businessException
     *            Is the given business exception which must be translated.
     * @return Returns the translated business exception.
     */
    public SOAPFaultException translateToSoapFaultException(
        Exception businessException);
    
    /**
     * Method to translate a given axis fault into its real business exception.
     * 
     * @param axisFault Is the given axis fault which must be translated.
     * @return Returns the created business exception.
     */
    public Exception translateToBusinessException(AxisFault axisFault);
}