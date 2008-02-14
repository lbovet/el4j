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
package ch.elca.el4j.core.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Locates all beans in the application context that match the configured
 * criteria.
 * <ul>
 * <li>In the <code>classes</code> property a comma-separated list of classes
 * whose instances to include in the result can be specified.</li>
 * <li>In the <code>excludeBeanNames</code> property a comma-separated list
 * of bean names to exclude from the result can be specified.</li>
 * <li>In the <code>includeBeanNames</code> property a comma-separated list
 * of bean names to include in the result can be specified.</li>
 * <li>In the <code>includeFactoryBeans</code> property it is specified if
 * FactoryBeans should be included in the result. Default value = true.</li>
 * <li>In the <code>includePrototypes</code> property it is specified if
 * prototype beans should be included in the result. Default value = true.</li>
 * </ul>
 * 
 * For the <code>excludeBeanNames</code> and <code>includeBeanNames</code>
 * properties wildcards <code>xxx*</code> and <code>*xxx</code> are allowed.
 * 
 * If a bean is listed in <code>includeBeanNames</code> and 
 * <code>excludeBeanNames</code> it will be excluded.
 * 
 * Beans which exist in application context and are listed in 
 * <code>includeBeanNames</code> but are not an instance of a listed class in 
 * <code>classes</code> will be also returned on invocation of method 
 * <code>getBeans</code>.
 * 
 *  
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Andreas Pfenninger (APR)
 */
public class BeanLocator implements ApplicationContextAware, InitializingBean {

    /**
     * The delimiter (",") for the include- and exclude bean names properties.
     */
    private static final String DELIMITER = ",";

    /** The application context. */
    private ApplicationContext m_applicationContext;

    /**
     * A comma-delimited String of all the classes whose instances have to be
     * located.
     */
    private String m_classes;

    /**
     * An array of the classes whose instances have to be located, converted
     * from the <code>m_classes</code> String.
     */
    private Class<?>[] m_classesArray;

    /**
     * A comma-delimited String of all bean names that have to be excluded from
     * the result. <b>Wildcards </b> *xxx and xxx* are allowed.
     */
    private String m_excludeBeanNames;

    /**
     * An array of the bean names to be excluded, converted from the
     * <code>m_excludeBeanNames</code> String.
     */
    private String[] m_excludeBeanNamesArray;

    /**
     * A comma-delimited String of all bean names that have to be included in
     * the result. <b>Wildcards </b> *xxx and xxx* are allowed.
     */
    private String m_includeBeanNames;

    /**
     * An array of the bean names to be included, converted from the
     * <code>m_excludeBeanNames</code> String.
     */
    private String[] m_includeBeanNamesArray;

    /**
     * Whether to include FactoryBeans too or just conventional beans. Default
     * value: true.
     */
    private boolean m_includeFactoryBeans = true;

    /**
     * Whether to include prototype beans too or just singletons (also applies
     * to FactoryBeans). Default value: true.
     */
    private boolean m_includePrototypes = true;

    /**
     * @param classes
     *            The comma-delimited String of all the classes whose instances
     *            have to be located.
     */
    public void setClasses(String classes) {
        m_classes = classes;
    }

    /**
     * @param excludeBeanNames
     *            The comma-delimited String of all bean names that have to be
     *            excluded from the result.
     */
    public void setExcludeBeanNames(String excludeBeanNames) {
        m_excludeBeanNames = excludeBeanNames;
    }

    /**
     * @param includeBeanNames
     *            The comma-delimited String of all bean names that have to be
     *            included in the result.
     */
    public void setIncludeBeanNames(String includeBeanNames) {
        m_includeBeanNames = includeBeanNames;
    }

    /**
     * @param includeFactoryBeans
     *            Whether to include FactoryBeans too or just conventional
     *            beans.
     */
    public void setIncludeFactoryBeans(boolean includeFactoryBeans) {
        m_includeFactoryBeans = includeFactoryBeans;
    }

    /**
     * @param includePrototypes
     *            Whether to include prototype beans too or just singletons
     *            (also applies to FactoryBeans).
     */
    public void setIncludePrototypes(boolean includePrototypes) {
        m_includePrototypes = includePrototypes;
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        m_applicationContext = applicationContext;
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (m_classes != null && m_classes.length() > 0
                && m_classes.trim().length() > 0) {
            m_classes = m_classes.replaceAll(" ", "");
            String[] classesStringArray = m_classes.split(DELIMITER);
            m_classesArray = new Class[classesStringArray.length];
            for (int i = 0; i < classesStringArray.length; i++) {
                m_classesArray[i] = Class.forName(classesStringArray[i]);
            }
        }
        if (m_includeBeanNames != null
                && m_includeBeanNames.trim().length() > 0) {
            m_includeBeanNames = m_includeBeanNames.replaceAll(" ", "");
            m_includeBeanNamesArray = m_includeBeanNames.split(DELIMITER);
        }
        if (m_excludeBeanNames != null
                && m_excludeBeanNames.trim().length() > 0) {
            m_excludeBeanNames = m_excludeBeanNames.replaceAll(" ", "");
            m_excludeBeanNamesArray = m_excludeBeanNames.split(DELIMITER);
        }
    }

    /**
     * Returns all beans that match the specified search criteria.
     * 
     * @return A map with the matching beans, containing the bean names as keys
     *         and the corresponding bean instances as values.
     */
    public Map<String,Object> getBeans() {
        Map<String,Object> beans = new HashMap<String,Object>();
        if (m_classesArray != null) {
            for (int i = 0; i < m_classesArray.length; i++) {
                Map addbeans = m_applicationContext.getBeansOfType(
                        m_classesArray[i], m_includePrototypes,
                        m_includeFactoryBeans);
                beans.putAll(addbeans);
            }
        }

        String[] beanNames = m_applicationContext.getBeanDefinitionNames();
        for (int i = 0; i < beanNames.length; i++) {
            handleIncludedBeanNames(beans, beanNames[i]);
            handleExcludedBeanNames(beans, beanNames[i]);
        }
        return beans;
    }

    /**
     * Method to include beans with a given name.
     * 
     * @param beans
     *            Are the currently collected beans.
     * @param beanName
     *            Is the name of the bean to include.
     */
    private void handleIncludedBeanNames(Map<String,Object> beans, String beanName) {
        if (m_includeBeanNamesArray != null) {
            for (int j = 0; j < m_includeBeanNamesArray.length; j++) {
                if (isMatch(beanName, m_includeBeanNamesArray[j])) {
                    boolean prototypeConstraint = !m_includePrototypes
                            && !m_applicationContext.isSingleton(beanName);
                    DefaultListableBeanFactory beanFactory 
                        = new DefaultListableBeanFactory(
                            m_applicationContext);
                    boolean factoryBeanConstraint = !m_includeFactoryBeans
                            && (beanFactory.isFactoryBean(beanName));
                    if (!prototypeConstraint && !factoryBeanConstraint) {
                        Object bean = m_applicationContext.getBean(beanName);
                        beans.put(beanName, bean);
                    }
                }
            }
        }
    }

    /**
     * Method to exclude beans with a given name.
     * 
     * @param beans
     *            Are the currently collected beans.
     * @param beanName
     *            Is the name of the bean to exclude.
     */
    private void handleExcludedBeanNames(Map beans, String beanName) {
        if (m_excludeBeanNamesArray != null) {
            for (int j = 0; j < m_excludeBeanNamesArray.length; j++) {
                if (isMatch(beanName, m_excludeBeanNamesArray[j])) {
                    beans.remove(beanName);
                }
            }
        }
    }

    /**
     * Returns whether the given bean name matches the mapped name. The default
     * implementation checks for "xxx*", "*xxx" and exact matches. Can be
     * overridden in subclasses.
     * 
     * @param beanName
     *            The bean name to check.
     * @param mappedName
     *            The name in the configured list of names.
     * @return True if the names match, false otherwise.
     */
    protected boolean isMatch(String beanName, String mappedName) {
        boolean match = false;
        if (beanName.equals(mappedName)) {
            match = true;
        } else {
            boolean prefixMatch = (mappedName.endsWith("*") && beanName
                    .startsWith(mappedName
                            .substring(0, mappedName.length() - 1)));
            boolean suffixMatch = (mappedName.startsWith("*") && beanName
                    .endsWith(mappedName.substring(1, mappedName.length())));
            match = prefixMatch || suffixMatch;
        }
        return match;
    }
}