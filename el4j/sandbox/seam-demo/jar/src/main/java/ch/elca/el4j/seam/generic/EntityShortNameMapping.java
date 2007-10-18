package ch.elca.el4j.seam.generic;

import java.util.HashMap;
import java.util.Map;

import ch.elca.el4j.seam.generic.humanization.Humanization;

/**
 * A bijective mapping between entity short-names and fully qualified entity class names.
 * E.g. "foo" <-> my.package.baz.bar.Foo.
 *
 * @author  Baeni Christoph (CBA)
 */
public class EntityShortNameMapping {
	private Map<String, String> m_EntityClassNames;
	private Map<String, String> m_EntityShortNames;
	private String m_DefaultEntityPackage;
	
	public void setEntityShortNames(Map<String, String> entityShortNames) {
		m_EntityShortNames = entityShortNames;
		m_EntityClassNames = invertMap(m_EntityShortNames);
	}
	
	public void setDefaultEntityPackage(String defaultEntityPackage) {
		m_DefaultEntityPackage = defaultEntityPackage;
	}
	
	private Map<String, String> invertMap(Map<String, String> map) {
		Map<String, String> invertedMap = new HashMap<String, String>();
		
		for (String key: map.keySet()) {
			String value = map.get(key);
			
			if (!invertedMap.containsKey(value)) {
				invertedMap.put(value, key);
			} else {
				throw new RuntimeException("Cannot invert map, given map not injective.");
			}
		}

		return invertedMap;
	}
	
	private String capitalize(String string) {
		return Humanization.capitalize(string);
	}
	
	public String getClassName(String entityShortName) {
		if (m_EntityClassNames.containsKey(entityShortName)) {
			return m_EntityClassNames.get(entityShortName);
		} else {
			if (m_DefaultEntityPackage == null) {
				throw new RuntimeException("Cannot guess entity class name. Default entity package not set.");
			}
			
			String className = m_DefaultEntityPackage + "." + capitalize(entityShortName);
			
			if (m_EntityShortNames.containsKey(className)) {
				throw new RuntimeException("Guessed class name already associated with different shortname!");
			}
			
			return className;
		}
	}
	
	private String uncapitalize(String string) {
		return Humanization.uncapitalize(string);
	}
	
	public String getShortName(String entityClassName) {
		if (m_EntityShortNames.containsKey(entityClassName)) {
			return m_EntityShortNames.get(entityClassName);
		} else {
			if (m_DefaultEntityPackage == null) {
				throw new RuntimeException("Cannot guess entity shortname. Default entity package not set.");
			}
			
			if (entityClassName.startsWith(m_DefaultEntityPackage + ".")) {
				String regex = "^" + m_DefaultEntityPackage + "\\.";
				String shortName = uncapitalize(entityClassName.replaceFirst(regex, ""));
				
				if (m_EntityShortNames.containsKey(shortName)) {
					throw new RuntimeException("Guessed shortname already associated with different class name!");
				}
				
				return shortName;
			} else {
				throw new RuntimeException("Unregistered class name not in default entity package!");
			}
		}
	}
}
