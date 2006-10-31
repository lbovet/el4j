/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.xmlmerge.factory;

import org.jdom.Element;
import org.jdom.Namespace;

import ch.elca.el4j.services.xmlmerge.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmerge.Operation;
import ch.elca.el4j.services.xmlmerge.OperationFactory;

/**
 * Creates operations by inspecting keywords passed as attributes in patch
 * elements.
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
public class AttributeOperationFactory implements OperationFactory {
    
    /**
     * Default operation.
     */
    private Operation m_defaultOperation;

    /**
     * Namespace describing the operations to apply.
     */
    private Namespace m_namespace;

    /**
     * Keyword.
     */
    private String m_keyword;

    /**
     * Operation resolver.
     */
    private OperationResolver m_resolver;

    /**
     * Creates a new AttributeOperationFactory.
     * 
     * @param defaultOperation
     *            The factory's default operation
     * @param resolver
     *            The factory's operation resolver
     * @param keyword
     *            The name of the attribute representing the factory's operation
     * @param namespace
     *            The namespace describing the operations to apply
     */
    public AttributeOperationFactory(Operation defaultOperation,
        OperationResolver resolver, String keyword, String namespace) {
        this.m_defaultOperation = defaultOperation;
        this.m_keyword = keyword;
        this.m_resolver = resolver;
        this.m_namespace = Namespace.getNamespace(namespace);
    }

    /**
     * {@inheritDoc}
     */
    public Operation getOperation(Element originalElement,
        Element modifiedElement) throws AbstractXmlMergeException {

        if (modifiedElement == null) {
            return m_defaultOperation;
        }

        String operationString = modifiedElement.getAttributeValue(m_keyword,
            m_namespace);

        if (operationString != null) {
            return m_resolver.resolve(operationString);
        } else {
            return m_defaultOperation;
        }

    }
}
