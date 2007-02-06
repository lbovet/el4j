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
package ch.elca.el4j.services.richclient.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ch.elca.el4j.services.richclient.context.AwakingContext;
import ch.elca.el4j.util.collections.ExtendedWritableList;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;

/** Abstract superclass to application GUIs. 
 * 
 * <p>An application GUI consists of one or more {@link Window Windows}. Windows
 * may be added at creation time by adding them to {@link #windows}. They may 
 * also be added later, in which case they must be initialized manually 
 * (see {@link #initialize(Window)}).
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
public abstract class Gui implements InitializingBean, ApplicationContextAware {
    /***/
    public ExtendedWritableList<Window> windows 
        = new ExtendedArrayList<Window>();
    
    /** the awaker to be used to wake manually created beans. */ 
    private AwakingContext m_context; 
    
    /***/
    protected Gui() {
//        test();
        System.out.println("Gui is beeing configured.");
    }
    
    /**
     * Initializes the GUI.
     * {@inheritDoc}
     */
    public void afterPropertiesSet() {
        System.out.println("Gui initializing.");
        for (Window w : windows) {
            initialize(w);
        }       
        System.out.println("Gui initialization succeeded");        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        m_context = new AwakingContext(applicationContext);
    }
    
    /** 
     * Initializes the provided {@link Window}. Windows added to 
     * {@link #windows} at creation time are automatically initialized, 
     * others must be initialized using this method.
     * @param w .
     **/
    protected void initialize(Window w) {
        w.init(m_context);
    }
    
//    /** place to put tests during testing. */
//    void test() {
//        SimpleGenericRepository<Reference> refrepo
//            = Services.get(RepositoryAgency.class)
//                      .getFor(Reference.class);
//
//        if (refrepo.findAll().size() == 0) {
//            Reference ref = new Reference();
//            ref.setName("Hibernate in Action");
//            ref.setDescription("");
//            refrepo.saveOrUpdate(ref);
//        }
//
//        SimpleGenericRepository<Keyword> kwrepo
//            = Services.get(RepositoryAgency.class)
//                      .getFor(Keyword.class);
//        
//        if (kwrepo.findAll().size() == 0) {
//            Keyword kw = new Keyword();
//            kw.setName("persistence");
//            kwrepo.saveOrUpdate(kw);            
//        }
//    }
}
