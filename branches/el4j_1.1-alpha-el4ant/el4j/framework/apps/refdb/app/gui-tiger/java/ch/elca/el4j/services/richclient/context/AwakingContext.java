/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.richclient.context;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.richclient.image.ImageSource;

import ch.elca.el4j.services.i18n.Images;
import ch.elca.el4j.services.i18n.MessageProvider;


/**
 * awaker that performs the initialization steps prescribed by BeanFactory. To 
 * provide the required ApplicationContexts, it requires a backing context. 
 * This object is not an ApplicationContext, as it neither keeps track of the
 * beans registered nor creates them.
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
public class AwakingContext implements Awaker {
    /** the backing context (for application services and the like). */
    public // for debug 
    ApplicationContext m_backingContext;
    
    /***/
    Images m_images;

    /** counter for name generation. */
    int m_gid = 0;
    
    /** creates a new AwakingContext that delegates to {@code backContext}. 
     * @param backingContext .*/
    public AwakingContext(ApplicationContext backingContext) {
        m_backingContext = backingContext;
        MessageProvider.setInstance(new MessageProvider(backingContext));
        m_images = new Images(
            (ImageSource) m_backingContext.getBean(
                "imageSource",
                ImageSource.class
            )
        );
    }
    
    /** returns the Images. */
    public Images getImages() { return m_images; }
    
    /**
     * {@inheritDoc}
     */
    public void awaken(Object o) {
        awaken(o, null);
    }
    

    /**
     * Awakens {@code o}.
     * @param requestedName {@code o}'s bean name. May be null, in which case
     *                      a numeric default name is used.
     */
    public void awaken(Object o, String requestedName) {
        if (o instanceof ApplicationEventPublisherAware) {
            ((ApplicationEventPublisherAware) o).setApplicationEventPublisher(
                m_backingContext
            );
        }
        if (o instanceof ApplicationContextAware) {
            ((ApplicationContextAware) o).setApplicationContext(
                m_backingContext
            );
        }
        if (o instanceof BeanNameAware) {
            String name = (requestedName == null) ? Integer.toString(m_gid++)
                                                  : requestedName;
            ((BeanNameAware) o).setBeanName(name);
        }
        if (o instanceof InitializingBean) {
            try {
                ((InitializingBean) o).afterPropertiesSet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
