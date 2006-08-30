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

import java.util.Arrays;
import java.util.NoSuchElementException;

import ch.elca.el4j.util.collections.TransformedList;
import ch.elca.el4j.util.collections.helpers.Filter;
import ch.elca.el4j.util.collections.helpers.Function;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;
import ch.elca.el4j.util.dom.reflect.EntityType;
import ch.elca.el4j.util.dom.reflect.Property;


/**
 * A list of proxies (of type {@link DisplayableProperty}) to properties of
 * a specific entity type that permits to conveniently hide and reorder its
 * elements.
 * 
 * <p>Refering to a non-existing property in any method causes a 
 * NoSuchElementException to be thrown.
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
public abstract class DisplayablePropertyList {
    /****/
    static Filter<DisplayableProperty> s_visibles 
        = new Filter<DisplayableProperty>() {        
            public boolean accepts(DisplayableProperty ep) {
                return ep.isVisible();
            }
        };
    
    /****/
    static Filter<EditableProperty> s_ineditables 
        = new Filter<EditableProperty>() {        
            public boolean accepts(EditableProperty ep) {
                return !ep.isEditable();
            }
        };      
    
    /****/
    static Function<DisplayableProperty, String> s_toName 
        = new Function<DisplayableProperty, String>() {
            public String apply(DisplayableProperty ep) {
                return ep.prop.name;
            }      
        };
    
    /** the property proxies this list contains. */
    protected ExtendedArrayList<EditableProperty> m_eprops
        = new ExtendedArrayList<EditableProperty>();
    
    /***/
    protected TransformedList<?, String> m_propNames 
        = m_eprops.mapped(s_toName);
    
    
    /** Constructor.
     * @param type the entity type whose properties should be proxied. */
    protected DisplayablePropertyList(EntityType type) {
        for (Property p : type.props) {
            m_eprops.add(new EditableProperty(p));
        }
    }
        
        
    /**
     * looks up and returns a property proxy by name.
     * @param name the property's name
     * @return the proxy for the property
     * @throws NoSuchElementException if the property does not exist 
     */
    EditableProperty doget(String name) throws NoSuchElementException {
        return m_eprops.get(
            m_propNames.indexOf(name)
        );        
    }
    
    /**
     * returns the property's proxy that describes the property's appearance.
     * @param name the property's name
     * @return the proxy
     */
    public DisplayableProperty get(String name) {
        return doget(name);
    }
    

    /**
     * Hides properties.
     * @param names the names of the properties to be hidden
     */
    public void hide(String... names) {
        for (String n : names) {
            DisplayableProperty dp = get(n);
            dp.hidden = true;
        }
    }
    
    /**
     * Reveal (unhide) properties.
     * @param names the names of the properties to be revealed
     */
    public void reveal(String... names) {
        for (String n : names) {
            DisplayableProperty dp = get(n);
            dp.hidden = false;
        }
    }
    
    /**
     * shows exactly the given properties.
     * @param names the names of the properties to be displayed, in the order
     * they should appear in
     */
    public void showExactly(String... names) {
        m_propNames.orderLike(Arrays.asList(names));
        for (int i = 0; i < m_eprops.size(); i++) {
            DisplayableProperty dp = m_eprops.get(i);
            dp.hidden = i >= names.length;
        }
    }    
}
