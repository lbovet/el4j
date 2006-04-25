/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.services.gui.richclient.windows;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageDescriptor;
import org.springframework.richclient.application.support.DefaultApplicationWindow;

import ch.elca.el4j.services.gui.richclient.pages.impl.AbstractApplicationPage;
import ch.elca.el4j.services.gui.richclient.pages.impl.MultipleViewsApplicationPage;

/**
 * Application window for having multiple views in a page.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class MultipleViewsApplicationWindow extends DefaultApplicationWindow {
    /**
     * Is the bean id to get the application page.
     */
    private static final String APPLICATION_PAGE_BEAN_ID 
        = "applicationPagePrototype";

    /**
     * {@inheritDoc}
     */
    protected ApplicationPage createPage(PageDescriptor pageDescriptor) {
        AbstractApplicationPage page;
        try {
            page = (AbstractApplicationPage) getServices().getBean(
                APPLICATION_PAGE_BEAN_ID, AbstractApplicationPage.class);
        } catch (NoSuchBeanDefinitionException e) {
            page = new MultipleViewsApplicationPage();
        }
        page.setApplicationWindow(this);
        page.setPageDescriptor(pageDescriptor);
        return page;
    }
}
