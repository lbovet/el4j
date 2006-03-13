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

package ch.elca.el4j.services.remoting;

import java.rmi.Remote;
import java.rmi.RemoteException;

import ch.elca.el4j.util.interfaceenrichment.EnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.MethodDescriptor;


/**
 * This interface decorator adds to a given interface rmi needs.
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
public class RmiEnrichmentDecorator implements EnrichmentDecorator {
    /**
     * Interface which the given interface has to extend.
     */
    private static final Class REMOTE_CLASS = Remote.class;
    
    /**
     * Exception which every method must be able to throw.
     */
    private static final Class REMOTE_EXCEPTION_CLASS = RemoteException.class;
    

    /**
     * {@inheritDoc}
     */
    public String changedInterfaceName(String originalInterfaceName) {
        return originalInterfaceName + "Rmi";
    }

    /**
     * {@inheritDoc}
     */
    public Class[] changedExtendedInterface(Class[] extendedInterfaces) {
        Class[] extendedInterfacesNew;
        if (extendedInterfaces == null || extendedInterfaces.length == 0) {
            extendedInterfacesNew = new Class[1];
            extendedInterfacesNew[0] = REMOTE_CLASS;
        } else {
            extendedInterfacesNew = new Class[extendedInterfaces.length + 1];
            for (int i = 0; i < extendedInterfaces.length; i++) {
                if (REMOTE_CLASS.isAssignableFrom(extendedInterfaces[i])) {
                    /**
                     * If the needed interface does already exists, return.
                     */
                    return extendedInterfaces;
                }
                extendedInterfacesNew[i] = extendedInterfaces[i];
            }
            extendedInterfacesNew[extendedInterfacesNew.length - 1] 
                = REMOTE_CLASS;
        }
        return extendedInterfacesNew;
    }

    /**
     * {@inheritDoc}
     */
    public MethodDescriptor changedMethodSignature(MethodDescriptor method) {
        Class[] thrownExceptions = method.getThrownExceptions();
        
        Class[] thrownExceptionsNew;
        if (thrownExceptions == null || thrownExceptions.length == 0) {
            thrownExceptionsNew = new Class[1];
            thrownExceptionsNew[0] = REMOTE_EXCEPTION_CLASS;
        } else {
            thrownExceptionsNew = new Class[thrownExceptions.length + 1];
            for (int i = 0; i < thrownExceptions.length; i++) {
                if (thrownExceptions[i].isAssignableFrom(
                    REMOTE_EXCEPTION_CLASS)) {
                    /**
                     * If the needed exception does already exists, return.
                     */
                    return method;
                }
                thrownExceptionsNew[i] = thrownExceptions[i];
            }
            thrownExceptionsNew[thrownExceptionsNew.length - 1] 
                = REMOTE_EXCEPTION_CLASS;
        }
        
        method.setThrownExceptions(thrownExceptionsNew);
        return method;
    }
}