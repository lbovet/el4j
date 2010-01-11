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
package ch.elca.el4j.util.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * The base class for generic configurations. It is a hierarchical structure of
 * configuration entries, where entries can be added, inherited and overridden.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class GenericConfig {
	
	/**
	 * The configuration entries that are added or override configurations
	 * declared in m_parentConfig.
	 */
	protected Map<String, Object> m_map;
	
	/**
	 * The default constructor, which creates an empty configuration.
	 */
	public GenericConfig() {
		m_map = new HashMap<String, Object>();
	}
	
	/**
	 * @param parent    the parent {@link GenericConfig} which configuration
	 *                  is inherited
	 */
	public void setParent(GenericConfig parent) {
		Iterator<Entry<String, Object>> mapIterator = parent.getMap().entrySet().iterator();
		
		while (mapIterator.hasNext()) {
			Entry<String, Object> entry = mapIterator.next();
			if (!m_map.containsKey(entry.getKey())) {
				add(entry.getKey(), entry.getValue());
			}
		}
	}
	
	/**
	 * @param parents    the parent {@link GenericConfig}s which configuration
	 *                   is inherited
	 */
	public void setParents(List<GenericConfig> parents) {
		for (GenericConfig genericConfig : parents) {
			setParent(genericConfig);
		}
	}
	
	/**
	 * @return    a copy of all configuration entries declared in this config
	 */
	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.putAll(m_map);
		return map;
	}
	
	/**
	 * @param map    the configuration entries to set
	 */
	public void setMap(Map<String, Object> map) {
		m_map = map;
	}
	
	/**
	 * @param properties    the configuration entries to set
	 */
	public void setMap(Properties properties) {
		m_map.clear();
		setOverrideMap(properties);
	}
	
	/**
	 * @param map    the configuration entries to add to the current entries
	 */
	public void setOverrideMap(Map<String, Object> map) {
		if (m_map != null) {
			m_map.putAll(map);
		} else {
			m_map = map;
		}
	}
	
	/**
	 * @param properties    the configuration entries to add
	 */
	public void setOverrideMap(Properties properties) {
		Iterator<Entry<Object, Object>> propertiesIterator = properties.entrySet().iterator();
		while (propertiesIterator.hasNext()) {
			Entry<Object, Object> property = propertiesIterator.next();
			m_map.put((String) property.getKey(), property.getValue());
		}
	}
	
	/**
	 * @param key      the key of the configuration entry to add
	 * @param value    the value of the configuration entry to add
	 */
	public void add(String key, Object value) {
		m_map.put(key, value);
	}
	
	/**
	 * @param key    the key of the configuration entry that should be returned
	 * @return       the corresponding value or <code>null</code> if not found
	 */
	public Object get(String key) {
		return m_map.get(key);
	}


}
