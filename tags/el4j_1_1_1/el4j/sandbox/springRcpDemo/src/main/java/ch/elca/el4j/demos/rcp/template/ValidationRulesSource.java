/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.demos.rcp.template;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.rules.Rules;
import org.springframework.rules.support.DefaultRulesSource;

import ch.elca.el4j.demos.rcp.helpers.HibernateConstraint;
import ch.elca.el4j.demos.rcp.helpers.ReflectionHelper;

/**
 * This class is a source for validation rules associated with the 
 * domain objects in this application. This clas is wired into application 
 * via the application context configuration like this:
 * 
 * <pre>
 *    &lt;bean id=&quot;rulesSource&quot; 
 *        class=&quot;org.springframework.richclient.samples.simple.domain.SimpleValidationRulesSource&quot;/&gt;
 * </pre>
 * 
 * With this configuration, validating forms will interrogate the 
 * rules source for rules that apply to the class of a form object 
 * (in this case, that's objects of type {@link Contact}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @param <T> domain type
 * @author David Stefan (DST)
 */
public class ValidationRulesSource<T> extends DefaultRulesSource 
    implements InitializingBean {

    /**
     * Domain type.
     */
    private Class<T> m_domainType;
    
    /**
     * The Reflection Helper.
     */
    private ReflectionHelper<T> m_helper;
    
    /**
     * Construct the rules source. Just add all the rules for each class that
     * will be validated.
     */
    public ValidationRulesSource() {
        super();
    }

    /**
     * @param domainType Domain Type to set
     */
    public void setDomainType(Class<T> domainType) {
        m_domainType = domainType;     
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        m_helper = new ReflectionHelper<T>(m_domainType);
        // Add the rules specific to the object types we manage
        addRules(createRules());
    }
    
    /**
     * Construct the rules that are used to validate a Contact domain object.
     * 
     * @return validation rules
     * @see Rules
     */
    private Rules createRules() {
        // Construct a Rules object that contains all the constraints we need to
        // apply
        // to our domain object. The Rules class offers a lot of convenience
        // methods
        // for creating constraints on named properties.
        return new Rules(m_domainType) {
            protected void initRules() {
                for (String item : m_helper.getProperties()) {
                    add(item, new HibernateConstraint<T>(m_domainType, item));
                }
            }
        };
    }
}
