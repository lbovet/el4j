/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.core.aop;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.InitializingBean;

/**
 * Auto proxy creator that identifies beans to proxy via a list of names.
 * Additionally, it allows you to specify a list of names that must not be
 * proxied. Both list, <code>beanNames</code> and
 * <code>exclusiveBeanNames</code> check for direct, "xxx*", and "*xxx" matches.
 * 
 * <p><b>Note</b> if you don't specify an include pattern (i.e. not setting the
 * <code>beanNames</code> property) and you have specified some beans to
 * exclude, then all beans except the excluding ones will be auto-proxied.
 *
 * <p>Exclusion has higher precedence than inclusions.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 * @see org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator
 */
public class ExclusiveBeanNameAutoProxyCreator
    extends BeanNameAutoProxyCreator implements InitializingBean {

    /** Bean names to autoproxy all beans. */
    public static final String[] AUTOPROXY_ALL_BEANS = {"*"};
    
    /** List of bean names that don't have to be advised. */
    private List m_exclusiveBeanNames;
    
    /** Whether there have been inclusive patterns set. */
    private boolean m_hasBeanNames = false;
    
    /**
     * Set the names of the beans that must not automatically get wrapped with
     * proxies. A name can specify a prefix to match by ending with "*",
     * e.g. "myBean,tx*" will match the bean named "myBean" and all beans whose
     * name start with "tx".
     * 
     * @param exclusiveBeanNames
     *      The bean names to exclude.
     */
    public void setExclusiveBeanNames(String[] exclusiveBeanNames) {
        m_exclusiveBeanNames = Arrays.asList(exclusiveBeanNames);
    }

    /**
     * {@inheritDoc}
     */
    public void setBeanNames(String[] beanNames) {
        if (beanNames.length > 0) {
            m_hasBeanNames = true;
        }
        super.setBeanNames(beanNames);
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (!m_hasBeanNames) {
            super.setBeanNames(AUTOPROXY_ALL_BEANS);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Object[] getAdvicesAndAdvisorsForBean(
            Class beanClass, String beanName, TargetSource targetSource) {
        
        if (m_exclusiveBeanNames != null) {
            if (m_exclusiveBeanNames.contains(beanName)) {
                return DO_NOT_PROXY;
            }
            
            for (Iterator it = m_exclusiveBeanNames.iterator(); it.hasNext();) {
                String mappedName = (String) it.next();
                if (isMatch(beanName, mappedName)) {
                    return DO_NOT_PROXY;
                }
            }
        }
        
        return super.getAdvicesAndAdvisorsForBean(
                beanClass, beanName, targetSource);
    }
}
