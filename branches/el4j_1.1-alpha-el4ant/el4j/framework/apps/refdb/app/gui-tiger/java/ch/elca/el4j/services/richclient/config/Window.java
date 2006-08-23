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
package ch.elca.el4j.services.richclient.config;

import org.springframework.richclient.application.PageDescriptor;

import ch.elca.el4j.services.gui.richclient.windows.MultipleViewsApplicationWindow;
import ch.elca.el4j.services.richclient.context.AwakingContext;
import ch.elca.el4j.util.codingsupport.Reject;
import ch.elca.el4j.util.codingsupport.annotations.Preliminary;
import ch.elca.el4j.util.collections.ExtendedWritableList;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;
import ch.elca.el4j.util.observer.ObservableValue;
import ch.elca.el4j.util.observer.ValueObserver;
import ch.elca.el4j.util.observer.impl.SettableObservableValue;




/**
 * A window. Support for switching pages is preliminary.
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
public class Window {
    /** this window's pages. */
    public ExtendedWritableList<Page> pages = new ExtendedArrayList<Page>();
    
    /** the current page. */
    public ObservableValue<Page> currentPage;
    
    /***/
    GenericWindow m_windowComponent;
    
    /** Initializes this window.
     * @param awaker The object to be used to wake beans.
     */
    void init(AwakingContext awaker) {
        //Reject.ifNull(name);
        Reject.ifNull(pages);
        Reject.ifFalse(!pages.isEmpty());
        
        for (Page p : pages) {
            p.init(awaker);
        }
        if (currentPage == null) {
            currentPage = new SettableObservableValue<Page>(pages.get(0));
        }

        m_windowComponent = new GenericWindow();

        awaker.awaken(m_windowComponent);
        m_windowComponent.postInit();
    }
    
    /***/
    class GenericWindow extends MultipleViewsApplicationWindow {
        /** keeps the displayed page up to date. */
        ValueObserver<Page> m_currentPageObserver
            = new ValueObserver<Page>() {
                /** update the displayed page */
                // improve cooperation with Spring RCP.
                // e.g. what if window closed?
                @Preliminary
                public void changed(Page newPage) {
                    if (newPage != null) {
                        showPage(newPage.name);  
                    } else {
                        if (isControlCreated()) {
                            getControl().setVisible(false);
                        }
                    }
                }
            };
        
        /**{@inheritDoc}*/
        @Override
        protected PageDescriptor getPageDescriptor(String pageDescriptorId) {
            for (Page p : pages) {
                if (p.name.equals(pageDescriptorId)) {
                    return p.m_descriptor;
                }
            }
            throw new IllegalArgumentException(
                "Page " + pageDescriptorId + " does not exist in window."
            );
        }
        
        protected void postInit() {
            currentPage.subscribe(m_currentPageObserver);
        }
        
    }
    
    
}