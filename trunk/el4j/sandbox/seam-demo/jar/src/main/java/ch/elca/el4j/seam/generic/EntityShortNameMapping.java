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

import java.util.HashMap;
import java.util.Map;

import ch.elca.el4j.seam.generic.humanization.Humanization;

/**
 * A bijective mapping between entity short-names and fully qualified
 * entity class names.
 * E.g. "foo" <-> my.package.baz.bar.Foo.
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
public class EntityShortNameMapping {
    /**
     * The class name of the entity.
     */
    private Map<String, String> m_entityClassNames;

    /**
     * The short name of the entity.
     */
    private Map<String, String> m_entityShortNames;

    /**
     * The default package name of the entities.
     */
    private String m_defaultEntityPackage;

    /**
     * @param entityShortNames    the short name mapping to set
     */
    public void setEntityShortNames(Map<String, String> entityShortNames) {
        m_entityShortNames = entityShortNames;
        m_entityClassNames = invertMap(m_entityShortNames);
    }

    /**
     * @param defaultEntityPackage    the default package name to set
     */
    public void setDefaultEntityPackage(String defaultEntityPackage) {
        m_defaultEntityPackage = defaultEntityPackage;
    }

    /**
     * @param map    a name mapping
     * @return       the inversed map (value -> key)
     */
    private Map<String, String> invertMap(Map<String, String> map) {
        Map<String, String> invertedMap = new HashMap<String, String>();

        for (String key : map.keySet()) {
            String value = map.get(key);

            if (!invertedMap.containsKey(value)) {
                invertedMap.put(value, key);
            } else {
                throw new RuntimeException(
                    "Cannot invert map, given map not injective.");
            }
        }

        return invertedMap;
    }

    /**
     * @param string    the string to capitalize
     * @return          the capitalized string
     */
    private String capitalize(String string) {
        return Humanization.capitalize(string);
    }

    /**
     * @param entityShortName    the short name of the entity
     * @return                   the full class name of the entity
     */
    public String getClassName(String entityShortName) {
        if (m_entityClassNames.containsKey(entityShortName)) {
            return m_entityClassNames.get(entityShortName);
        } else {
            if (m_defaultEntityPackage == null) {
                throw new RuntimeException(
                    "Cannot guess entity class name. "
                    + "Default entity package not set.");
            }

            String className = m_defaultEntityPackage + "."
                + capitalize(entityShortName);

            if (m_entityShortNames.containsKey(className)) {
                throw new RuntimeException(
                    "Guessed class name already associated "
                    + "with different shortname!");
            }

            return className;
        }
    }

    /**
     * @param string    the string to uncapitalize
     * @return          the uncapitalized string
     */
    private String uncapitalize(String string) {
        return Humanization.uncapitalize(string);
    }

    /**
     * @param entityClassName    the full class name of the entity
     * @return                   the short name of the entity
     */
    public String getShortName(String entityClassName) {
        if (m_entityShortNames.containsKey(entityClassName)) {
            return m_entityShortNames.get(entityClassName);
        } else {
            if (m_defaultEntityPackage == null) {
                throw new RuntimeException(
                    "Cannot guess entity shortname. "
                    + "Default entity package not set.");
            }

            if (entityClassName.startsWith(m_defaultEntityPackage + ".")) {
                String regex = "^" + m_defaultEntityPackage + "\\.";
                String shortName = uncapitalize(entityClassName.replaceFirst(
                    regex, ""));

                if (m_entityShortNames.containsKey(shortName)) {
                    throw new RuntimeException(
                        "Guessed shortname already associated "
                        + "with different class name!");
                }

                return shortName;
            } else {
                throw new RuntimeException(
                    "Unregistered class name not in default entity package!");
            }
        }
    }
}
