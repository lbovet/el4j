/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.xmlmerge.factory;

import org.jdom.Element;

import ch.elca.el4j.services.xmlmerge.Operation;
import ch.elca.el4j.services.xmlmerge.OperationFactory;

/**
 * An operation factory returning always the same operation whatever the
 * specified elements.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class StaticOperationFactory implements OperationFactory {

    /**
     * The operation operation returned by this factory.
     */
    Operation m_operation;
    
    /**
     * Creates a StaticOperationFactory returning the given operation.
     * @param operation The operation operation returned by this factory.
     */
    public StaticOperationFactory(Operation operation) {
        this.m_operation = operation;
    }
    
    /**
     * {@inheritDoc}
     */
    public Operation getOperation(Element originalElement,
        Element modifiedElement) {
        return m_operation;
    }

}
