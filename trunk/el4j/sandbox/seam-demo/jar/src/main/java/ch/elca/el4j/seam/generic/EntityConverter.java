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
package ch.elca.el4j.seam.generic;

import java.io.Serializable;
import java.util.HashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import static org.jboss.seam.ScopeType.CONVERSATION;

/**
 * Replacement JSF converter for seam's EntityConverter.
 * Seam's EntityConverter cannot be used without a seam managed entityManager.
 * An example usage can be found in entityEdit.xhtml.
 * 
 * @see org.jboss.seam.ui.converter.EntityConverter
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Baeni Christoph (CBA)
 */
@Name("ch.elca.el4j.seam.generic.EntityConverter")
@Scope(CONVERSATION)
@Converter
@BypassInterceptors
public class EntityConverter implements javax.faces.convert.Converter,
    Serializable {
    
    /**
     * The ID-to-object mapping. 
     */
    private HashMap<Long, Object> m_mapping = new HashMap<Long, Object>();
    
    /**
     * The next valid ID.
     */
    private Long m_nextId = new Long(1);

    /** {@inheritDoc} */
    public Object getAsObject(FacesContext context, UIComponent component,
        String value) {
        return m_mapping.get(new Long(value));
    }

    /** {@inheritDoc} */
    public String getAsString(FacesContext contect, UIComponent component,
        Object entity) {
        Long id = allocateId();

        m_mapping.put(id, entity);

        return id.toString();
    }

    /**
     * @return    a valid ID
     */
    private Long allocateId() {
        return m_nextId++;
    }
}
