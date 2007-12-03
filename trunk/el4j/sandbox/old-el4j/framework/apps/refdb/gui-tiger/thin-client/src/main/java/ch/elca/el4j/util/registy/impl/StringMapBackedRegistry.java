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
package ch.elca.el4j.util.registy.impl;

import java.util.HashMap;
import java.util.Map;

import ch.elca.el4j.util.registy.Registry;

/**
 * A registry backed by a map keyed with class names.
 * 
 * A StringMapBackedRegistry is stateless except for {@code m_backing}.
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
public class StringMapBackedRegistry implements Registry {
    /** The backing map. */
    public Map<String, Object> m_backing;
    
    /** Constructor. */
    public StringMapBackedRegistry() { }
    
    /**
     * Constructor.
     * @param backing the backing map
     */
    public StringMapBackedRegistry(Map<String, Object> backing) {
        m_backing = backing;
    }

    /** {@inheritDoc} */
    public <T> T get(Class<T> c) {
        return m_backing != null
             ? c.cast(m_backing.get(c.getName()))
             : null;
    }
    
    /**
     * Associates a class with an instance.
     * @param c .
     * @param instance . 
     */
    public <T> void set(Class<T> c, T instance) {
        if (m_backing == null) {
            m_backing = new HashMap<String, Object>();
        }
        m_backing.put(c.getName(), instance);
    }
}