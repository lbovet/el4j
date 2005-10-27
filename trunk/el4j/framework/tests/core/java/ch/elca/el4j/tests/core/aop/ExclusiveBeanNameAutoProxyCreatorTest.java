/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
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

package ch.elca.el4j.tests.core.aop;

import org.springframework.context.ApplicationContext;

import ch.elca.el4j.core.context.ModuleApplicationContext;

import junit.framework.TestCase;

/**
 * This class tests the {@link
 * ch.elca.el4j.core.aop.ExclusiveBeanNameAutoProxyCreator}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class ExclusiveBeanNameAutoProxyCreatorTest extends TestCase {

    /** String returned by proxied beans. */
    public static final String PROXIED = "proxied";
    
    /** Bean A. */
    private static final String BEAN_A = "beanA";
    
    /** Bean B. */
    private static final String BEAN_B = "beanB";
    
    /** Bean foo. */
    private static final String FOO = "foo";
    
    /** Bean bar. */
    private static final String BAR = "bar";
    
    /** Bean foobar. */
    private static final String FOOBAR = "foobar";
    
    /** Registered beans. */
    private static final String[] BEAN_NAMES = {
        BEAN_A, BEAN_B, FOO, BAR, FOOBAR
    };
    
    /** Configuration without any beans to exclude. */
    private static final String EMPTY_EXCLUSIVE_CONFIG
        = "classpath:core/aop/emptyExclusiveList.xml";
    
    /** Configuration that exclude all beans. */
    private static final String EXCLUDE_ALL_CONFIG
        = "classpath:core/aop/excludeAll.xml";
    
    /** Configuration that excludes only bean 'foobar'. */
    private static final String INCLUDE_ALL_BUT_FOOBAR_CONFIG
        = "classpath:core/aop/includeAllButFoobar.xml";
    
    /** Configuration that includes all beans but those beginning with 'foo'. */
    private static final String INCLUDE_ALL_BUT_FOO_PREFIX_CONFIG
        = "classpath:core/aop/includeAllButFooPrefix.xml";
    
    /** Configuration that includes all beans but those ending with 'bar'. */
    private static final String INCLUDE_ALL_BUT_BAR_SUFFIX_CONFIG
        = "classpath:core/aop/includeAllButBarSuffix.xml";
    
    /**
     * Configuration that includes all beans starting with 'bean' but those
     * ending with 'a'.
     */
    private static final String 
        INCLUDE_ALL_STARTING_WITH_BEAN_BUT_SUFFIX_A_CONFIG
        = "classpath:core/aop/includeAllStartingWithBeanButSuffixA.xml";
    
    /**
     * Tests the configuration that doesn't exclude any beans.
     */
    public void testIncludesOnly() {
        ApplicationContext appCtx = createAppContext(EMPTY_EXCLUSIVE_CONFIG);
        
        for (int i = 0; i < BEAN_NAMES.length; i++) {
            assertAdvised(appCtx, BEAN_NAMES[i]);
        }
    }

    /**
     * Tehsts the configuration that excludes all beans.
     */
    public void testExcludeAll() {
        ApplicationContext appCtx = createAppContext(EXCLUDE_ALL_CONFIG);
        
        for (int i = 0; i < BEAN_NAMES.length; i++) {
            assertNotAdvised(appCtx, BEAN_NAMES[i]);
        }
    }

    /**
     * Tests the configuration that includes all beans but the one named
     * 'foobar'.
     */
    public void testIncludeAllButFoobar() {
        ApplicationContext appCtx = createAppContext(
                INCLUDE_ALL_BUT_FOOBAR_CONFIG);
        
        assertAdvised(appCtx, BEAN_A);
        assertAdvised(appCtx, BEAN_B);
        assertAdvised(appCtx, FOO);
        assertAdvised(appCtx, BAR);
        assertNotAdvised(appCtx, FOOBAR);
    }
    
    /**
     * Tests the configuration that includes all beans but those starting with
     * 'foo'.
     *
     */
    public void testIncludeAllButFooPrefix() {
        ApplicationContext appCtx = createAppContext(
                INCLUDE_ALL_BUT_FOO_PREFIX_CONFIG);
        
        assertAdvised(appCtx, BEAN_A);
        assertAdvised(appCtx, BEAN_B);
        assertNotAdvised(appCtx, FOO);
        assertAdvised(appCtx, BAR);
        assertNotAdvised(appCtx, FOOBAR);
    }
    
    /**
     * Tests the configuration that includes all beans but those ending with
     * 'bar'.
     */
    public void testIncludeAllButBarSuffix() {
        ApplicationContext appCtx = createAppContext(
                INCLUDE_ALL_BUT_BAR_SUFFIX_CONFIG);
        
        assertAdvised(appCtx, BEAN_A);
        assertAdvised(appCtx, BEAN_B);
        assertAdvised(appCtx, FOO);
        assertNotAdvised(appCtx, BAR);
        assertNotAdvised(appCtx, FOOBAR);
    }

    /**
     * Tests the configuration that includes all beans which start with
     * 'bean' but don't end with 'a'.
     */
    public void testIncludeAllStartingWithBeanButSuffixA() {
        ApplicationContext appCtx = createAppContext(
                INCLUDE_ALL_STARTING_WITH_BEAN_BUT_SUFFIX_A_CONFIG);
        
        assertNotAdvised(appCtx, BEAN_A);
        assertAdvised(appCtx, BEAN_B);
        assertNotAdvised(appCtx, FOO);
        assertNotAdvised(appCtx, BAR);
        assertNotAdvised(appCtx, FOOBAR);
    }

    /**
     * Asserts that the bean with the given name is advised in the given
     * application context.
     * 
     * @param appCtx
     *      The application context.
     * @param beanName
     *      The name of the bean which is asserted to be advised.
     */
    protected void assertAdvised(ApplicationContext appCtx, String beanName) {
        Bean bean = (Bean) appCtx.getBean(beanName);
        assertEquals("Bean '" + beanName + "' has not been advised.",
                PROXIED, bean.getBeanName());
    }
    
    /**
     * Asserts that the bean with the given name is not advised in the given
     * application context.
     * 
     * @param appCtx
     *      The application context.
     * @param beanName
     *      The name of the bean which is asserted not to be advised.
     */
    protected void assertNotAdvised(
            ApplicationContext appCtx, String beanName) {
        Bean bean = (Bean) appCtx.getBean(beanName);
        assertEquals("Bean '" + beanName + "' has not been advised.",
                beanName, bean.getBeanName());
    }
    
    /**
     * Crates a new application context for the given configuration file.
     * 
     * @param config
     *      The configuration file's location.
     *      
     * @return Returns a newly created application context.
     */
    private ApplicationContext createAppContext(String config) {
        return new ModuleApplicationContext(config, false);
    }
}
