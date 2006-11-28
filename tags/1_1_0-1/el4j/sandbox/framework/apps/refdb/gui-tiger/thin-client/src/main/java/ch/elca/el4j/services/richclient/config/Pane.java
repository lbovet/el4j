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

import java.awt.GridLayout;
import java.awt.LayoutManager;

import org.springframework.richclient.application.PageComponentDescriptor;

import ch.elca.el4j.services.gui.richclient.pagecomponents.descriptors.impl.DefaultGroupPageComponentDescriptor;
import ch.elca.el4j.services.richclient.context.AwakingContext;
import ch.elca.el4j.util.collections.ExtendedWritableList;
import ch.elca.el4j.util.collections.impl.ExtendedArrayList;



/** 
 * A composite component containing several components laid out next to each
 * other.
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
public class Pane extends AbstractComponent {
    /** the supported layouts. */
    public enum Layout {
        vertically  (new GridLayout(0, 1)), 
        horizontally(new GridLayout(1, 0));
        
        /***/
        LayoutManager m_layoutManager;
        /***/
        Layout(LayoutManager l) {
            m_layoutManager = l;
        }
    }
    
    /** the layout to be used. */
    public Layout layout;
    
    /***/
    public ExtendedWritableList<AbstractComponent> components;
    
    /***/
    protected Pane() {
        this(Layout.vertically);
    }
        
    /** @param l .*/
    protected Pane(Layout l) {
        layout = l;
        components = new ExtendedArrayList<AbstractComponent>();
    }
    
    /** @param l . 
     * @param cs .*/
    public Pane(Layout l, AbstractComponent... cs) {
        layout = l;
        components = new ExtendedArrayList<AbstractComponent>(cs);
    }
    
    /** {@inheritDoc} */
    @Override 
    void init(AwakingContext context) {
        for (AbstractComponent c : components) {
            c.init(context);
        }
    }

    /** {@inheritDoc} */
    @Override
    PageComponentDescriptor getDescriptor(AwakingContext awaker) {
        DefaultGroupPageComponentDescriptor d 
            = new DefaultGroupPageComponentDescriptor();
        
        PageComponentDescriptor[] cds 
            = new PageComponentDescriptor[components.size()];
        int i = 0;
        for (AbstractComponent c : components) {
            cds[i++] = c.getDescriptor(awaker);
        }
        d.setPageComponentDescriptors(cds);
        d.setLayoutManager(layout.m_layoutManager);
        awaker.awaken(d);
        return d;
    }
}
