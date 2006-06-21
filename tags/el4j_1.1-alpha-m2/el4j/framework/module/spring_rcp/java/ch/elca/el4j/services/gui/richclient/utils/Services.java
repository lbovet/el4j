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
package ch.elca.el4j.services.gui.richclient.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.richclient.application.ApplicationServicesLocator;

/**
 * Utility class to assist with fetching service implementations and
 * spring-beans.
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
public final class Services {
    /***/
    private Services() { }
    
    /**
     * Returns the service of kind {@code type}. Fetches the standard services 
     * locator, then delegates to
     *{@link org.springframework.richclient.application.ApplicationServices
     *#getService(Class) ApplicationServices.getService()}.
     * @param type the kind of service whose implementation is desired
     * @return the service implementation
     * @throws UnsupportedOperationException if no service is known for 
     *         the given serviceType.
     */
    public static <S> S get(Class<S> type)
        throws UnsupportedOperationException {
        
        return type.cast(
            ApplicationServicesLocator.services().getService(type)
        );
    }
    
    /**
     * Type-safety wrapper for 
     * {@link org.springframework.beans.factory.BeanFactory#getBean(String name,
     *Class requiredType) ApplicationContext.getBean}.
     * @param name the name of the bean to look up
     * @param requiredType the desired return type (which the bean implements)
     * @return the bean
     */
    public static <B> B getBean(String name, Class<B> requiredType) {
        return requiredType.cast(
            get(ApplicationContext.class).getBean(name, requiredType)
        );
    }
}