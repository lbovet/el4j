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

package ch.elca.el4j.services.remoting.protocol.soap;

import ch.elca.el4j.services.remoting.RmiEnrichmentDecorator;
import ch.elca.el4j.util.interfaceenrichment.MethodDescriptor;

/**
 * This class is a strong version of the rmi enrichment decorator, which removes
 * all by method thrown business exceptions. Business exceptions must not be 
 * showed in wsdl file, because they will be handled specially.
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
public class SoapRmiEnrichmentDecorator extends RmiEnrichmentDecorator {
    
    /**
     * {@inheritDoc}
     */
    public MethodDescriptor changedMethodSignature(MethodDescriptor method) {
        method.setThrownExceptions(null);
        return super.changedMethodSignature(method);
    }
}
