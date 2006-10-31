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

import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageDescriptor;

import ch.elca.el4j.services.gui.richclient.pages.descriptors.MultipleViewsPageDescriptor;
import ch.elca.el4j.services.richclient.context.AwakingContext;
import ch.elca.el4j.util.codingsupport.annotations.Preliminary;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;




/**
 * A window layout. Windows typically have multiple layouts. Analogous to
 * eclipse's Perspective abstraction.
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
@Preliminary
public class Page extends Pane {
    /** this page's identifier. */
    public String name;

    /***/
    PageDescriptor m_descriptor;
    
    /***/
    protected Page() {
        components = new ExtendedArrayList<AbstractComponent>();
    }

    /** initializes this page. */
    void init(AwakingContext context) {
        for (AbstractComponent c : components) {
            c.init(context);
        }

        MultipleViewsPageDescriptor d = new MultipleViewsPageDescriptor();
        d.setPageComponentDescriptors(
            new PageComponentDescriptor[] {
                super.getDescriptor(context)
            }
        );
        //d.setLayoutManager();
        context.awaken(d);
        m_descriptor = d;
    }
}