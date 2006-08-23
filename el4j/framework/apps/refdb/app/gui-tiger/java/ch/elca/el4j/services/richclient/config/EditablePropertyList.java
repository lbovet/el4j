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
package ch.elca.el4j.services.richclient.config;

import java.util.List;

import ch.elca.el4j.services.dom.info.EntityType;

/**
 * A {@link DisplayablePropertyList} that permits to conveniently
 * write-protect its elements.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */
public class EditablePropertyList extends DisplayablePropertyList {
    /**
     * creates a new EditablePropertyList featuring the properties of type
     * {@code type}.
     * @param type .
     */
    EditablePropertyList(EntityType type) {
        super(type);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EditableProperty get(String name) {
        return doget(name);
    }
    
    /** 
     * write-protects the supplied properties.
     * @param names .
     */
    public void lock(String... names) {
        for (String n : names) {
            EditableProperty ep = get(n);
            ep.locked = true;
        }        
    }
    
    /**
     * removes write-protection from the supplied properties.
     * @param names .
     */
    // TODO: have it fail if properties are not write-capable.
    public void unlock(String... names) {
        for (String n : names) {
            EditableProperty ep = get(n);
            ep.locked = false;
        }        
    }
    
    /**
     * returns the visible and readonly properties' names. 
     * @return .
     */
    List getReadonly() {
        return (List) m_eprops.filtered(s_visibles)
                              .filtered(s_ineditables)
                              .mapped(s_toName);
    }
}
