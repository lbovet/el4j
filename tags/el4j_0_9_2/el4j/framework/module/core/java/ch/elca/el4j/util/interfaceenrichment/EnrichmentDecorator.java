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

package ch.elca.el4j.util.interfaceenrichment;

/**
 * This interface is used to describe how an interface should be
 * decorated. With <em>decoration</em> we mean that we create a 
 * shadow interface that is slightly different than the original 
 * interface. The shadow interface can additionally implement a new interfaces 
 * and/or one can change any signatures of its methods.
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
public interface EnrichmentDecorator {

    /**
     * Method to change the name of the interface.
     * 
     * @param originalInterfaceName
     *            Is the name of the original interface.
     * @return Returns the newly created interface name.
     */
    public String changedInterfaceName(String originalInterfaceName);

    /**
     * Method to change classes, which are extended by the interface.
     * 
     * @param extendedInterfaces
     *            Are the extended interfaces of the original interface.
     * @return Returns the extended interfaces for the new interface.
     */
    public Class[] changedExtendedInterface(Class[] extendedInterfaces);

    /**
     * Method to change the signature of given method.
     * 
     * @param method
     *            Is the method from the original interface.
     * @return Returns the method signature for the new interface.
     */
    public MethodDescriptor changedMethodSignature(MethodDescriptor method);
}
