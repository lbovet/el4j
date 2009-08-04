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
import java.util.Map;
import java.util.Properties;

/**
 * The base class for generic configurations. It is a hierarchical structure of
 * configuration entries, where entries can be added, inherited and overridden.
 * In addition to {@link GenericConfig} it is possible to get all configs
 * starting with a specified prefix.
 *
 * This can be useful for configs like
 * ch.elca.el4j.a = aaa
 * ch.elca.el4j.b = bbb
 * where you can use <code>getSubConfig("ch.elca.el4j")</code>.
 *
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class HierarchicalGenericConfig extends GenericConfig {
	/**
	 * The prefix for this config (used in subconfigs).
	 */
	protected String m_prefix = "";
	
	/** {@inheritDoc} */
	@Override
	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		for (String key : m_map.keySet()) {
			if (key.startsWith("#")) {
				HierarchicalGenericConfig child = (HierarchicalGenericConfig)
					m_map.get(key);
				map.putAll(child.getMap());
			} else {
				map.put(m_prefix + key, m_map.get(key));
			}
		}
		return map;
	}
	
	/** {@inheritDoc} */
	@Override
	public void setMap(Map<String, Object> map) {
		m_map.clear();
		setOverrideMap(map);
	}
	
	/** {@inheritDoc} */
	@Override
	public void setOverrideMap(Map<String, Object> map) {
		for (String key : map.keySet()) {
			add(key, map.get(key));
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void setOverrideMap(Properties properties) {
		for (Object key : properties.keySet()) {
			add((String) key, properties.get(key));
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public void add(String key, Object value) {
		if (key.contains(".")) {
			String childKey = key.substring(0, key.indexOf("."));
			HierarchicalGenericConfig child = getSubConfig(childKey);
			
			// create child if it doesn't exist yet
			if (child == null) {
				child = new HierarchicalGenericConfig();
				child.setPrefix(m_prefix + childKey + ".");
			}
			
			child.add(key.substring(key.indexOf(".") + 1), value);
			
			m_map.put("#" + childKey, child);
		} else {
			m_map.put(key, value);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	public Object get(String key) {
		if (key.contains(".")) {
			HierarchicalGenericConfig child = (HierarchicalGenericConfig)
				m_map.get("#" + key.substring(0, key.indexOf(".")));
			return child.get(key.substring(key.indexOf(".") + 1));
		} else {
			return m_map.get(key);
		}
	}
	
	/**
	 * @param prefix    the prefix of the all configuration entries to get
	 * @return          all entries having the specified prefix
	 */
	public HierarchicalGenericConfig getSubConfig(String prefix) {
		if (prefix.contains(".")) {
			HierarchicalGenericConfig child = (HierarchicalGenericConfig)
				m_map.get("#" + prefix.substring(0, prefix.indexOf(".")));
			return child.getSubConfig(
				prefix.substring(prefix.indexOf(".") + 1));
		} else {
			return (HierarchicalGenericConfig) m_map.get("#" + prefix);
		}
	}
	
	/**
	 * @return    all configuration children
	 */
	public Map<String, Object> getChildren() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		for (String key : m_map.keySet()) {
			if (key.startsWith("#")) {
				map.put(key.substring(1), m_map.get(key));
			} else {
				map.put(key, m_map.get(key));
			}
		}
		
		return map;
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Map<String, Object> map = getMap();
		for (String key : map.keySet()) {
			buffer.append(key);
			buffer.append(" = ");
			buffer.append(map.get(key));
			buffer.append(System.getProperty("line.separator"));
		}
		return buffer.toString();
	}

	/**
	 * @return Returns the prefix.
	 */
	public String getPrefix() {
		return m_prefix;
	}

	/**
	 * @param prefix Is the prefix to set.
	 */
	public void setPrefix(String prefix) {
		m_prefix = prefix;
	}
}
