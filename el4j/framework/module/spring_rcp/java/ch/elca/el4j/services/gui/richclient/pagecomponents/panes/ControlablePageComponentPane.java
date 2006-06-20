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
package ch.elca.el4j.services.gui.richclient.pagecomponents.panes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.PageComponentPane;
import org.springframework.richclient.application.View;
import org.springframework.richclient.image.IconSource;
import org.springframework.richclient.util.SpringLayoutUtils;

import com.jgoodies.forms.factories.Borders;

import ch.elca.el4j.services.gui.richclient.pages.ExtendedApplicationPage;
import ch.elca.el4j.services.gui.richclient.utils.ComponentUtils;
import ch.elca.el4j.services.gui.richclient.utils.Services;
import ch.elca.el4j.services.gui.swing.panel.ControlableInternalFrame;

/**
 * Page component pane for a controlable internal frame.
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
public class ControlablePageComponentPane extends PageComponentPane {
    /**
     * Is the extended application page where this pane is included. 
     */
    protected final ExtendedApplicationPage m_extendedApplicationPage;
    
    /**
     * Constructor.
     * 
     * @param pageComponent
     *            Is the page component that is wrapped by this pane.
     */
    public ControlablePageComponentPane(PageComponent pageComponent) {
        this(pageComponent, null);
    }

    /**
     * Constructor.
     * 
     * @param pageComponent
     *            Is the page component that is wrapped by this pane.
     * @param extendedApplicationPage
     *            Is the extended application page where this pane is included.
     */
    public ControlablePageComponentPane(PageComponent pageComponent,
        ExtendedApplicationPage extendedApplicationPage) {
        super(pageComponent);
        m_extendedApplicationPage = extendedApplicationPage;
    }

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (isControlCreated()) {
            ControlableInternalFrame frame = getControlableInternalFrame();
            String propertyName = evt.getPropertyName();
            if ("pageComponentSelected".equals(propertyName)) {
                boolean selected = ((Boolean) evt.getNewValue()).booleanValue();
                frame.setSelected(selected);
            }
            refreshProperties(frame);
        }
    }
    
    /**
     * Refreshs properties of given controlable internal frame.
     * 
     * @param frame Is the frame where to set properties.
     */
    public void refreshProperties(ControlableInternalFrame frame) {
        PageComponent pageComponent = getPageComponent();
        frame.setTitle(pageComponent.getDisplayName());
        frame.setIcon(pageComponent.getIcon());
        frame.setToolTipText(pageComponent.getCaption());
    }

    /**
     * {@inheritDoc}
     */
    protected JComponent createControl() {
        ControlableInternalFrame frame = new ControlableInternalFrame();
        refreshProperties(frame);
        JComponent control = getPageComponent().getControl();
        JComponent controlPanel = createControlPanel();
        frame.init(control, controlPanel);
        
        final PageComponent PAGE_COMPONENT = getPageComponent();
        if (PAGE_COMPONENT instanceof View) {
            ComponentUtils.addMouseListenerRecursivly(frame, 
                new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    m_extendedApplicationPage.setActiveComponent(
                        PAGE_COMPONENT);
                }
            });
        }
        
        return frame;
    }

    /**
     * @return Returns the created control panel.
     */
    protected JComponent createControlPanel() {
        IconSource iconSource = Services.get(IconSource.class);
        Icon closeIcon = iconSource.getIcon("pagecomponent.close.icon");
        Icon closeIconRollover = iconSource.getIcon(
            "pagecomponent.close.rolloverIcon"
        );
        JButton closeButton = new JButton(closeIcon);
        closeButton.setRolloverIcon(closeIconRollover);
        closeButton.setOpaque(false);
        closeButton.setBorder(Borders.EMPTY_BORDER);
        JPanel controlPanel = new JPanel(new SpringLayout());
        controlPanel.setOpaque(false);
        controlPanel.add(closeButton);
        // Checkstyle: MagicNumber off
        SpringLayoutUtils.makeCompactGrid(controlPanel, 
            1, controlPanel.getComponentCount(), 3, 3, 3, 3);
        // Checkstyle: MagicNumber on
        
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_extendedApplicationPage.close(getPageComponent());
            }
        });
        
        return controlPanel;
    }
    
    /**
     * @return Returns the control of this pane.
     */
    protected ControlableInternalFrame getControlableInternalFrame() {
        return (ControlableInternalFrame) getControl();
    }
}
