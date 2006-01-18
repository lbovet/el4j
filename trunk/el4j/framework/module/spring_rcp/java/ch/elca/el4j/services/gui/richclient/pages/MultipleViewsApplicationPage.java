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
package ch.elca.el4j.services.gui.richclient.pages;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentDescriptor;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.application.support.DefaultViewContext;

/**
 * Application page that can have multiple views.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class MultipleViewsApplicationPage extends AbstractApplicationPage 
    implements PageLayoutBuilder {
    /**
     * Is the control of this page. It can contain multiple views.
     */
    private JComponent m_control;
    
    /**
     * Are the positioning arguments for the views. The view descriptor id is 
     * used as key for the map. 
     */
    private final Map m_positionArguments = new HashMap();

    /**
     * {@inheritDoc}
     * 
     * Returns the control of this page.
     */
    public JComponent getControl() {
        if (m_control == null) {
            this.m_control = new JPanel(new BorderLayout());
            getPageDescriptor().buildInitialLayout(this);
            setActiveComponent();
        }
        return m_control;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean giveFocusTo(PageComponent pageComponent) {
        PageComponentPane pane = pageComponent.getContext().getPane();
        Object positionArgument = m_positionArguments.get(
            pageComponent.getId());
        this.m_control.add(pane.getControl(), positionArgument);
        this.m_control.validate();
        this.m_control.repaint();
        pane.requestFocusInWindow();

        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected PageComponent createPageComponent(
        PageComponentDescriptor pageComponentDescriptor) {
        
        PageComponent pageComponent 
            = pageComponentDescriptor.createPageComponent();
        pageComponent.setContext(
            new DefaultViewContext(this, new PageComponentPane(pageComponent)));

        // trigger the createControl method of the PageComponent, so if a
        // PageComponentListener is added
        // in the createControl method, the componentOpened event is received.
        pageComponent.getControl();

        return pageComponent;
    }

    /**
     * {@inheritDoc}
     */
    public void addView(String viewDescriptorId) {
        showView(viewDescriptorId);
    }

    /**
     * {@inheritDoc}
     */
    public void addView(String viewDescriptorId, Object positionArgument) {
        m_positionArguments.put(viewDescriptorId, positionArgument);
        addView(viewDescriptorId);
    }
}
