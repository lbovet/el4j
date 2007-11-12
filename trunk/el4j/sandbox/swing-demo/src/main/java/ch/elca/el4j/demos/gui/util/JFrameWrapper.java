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
package ch.elca.el4j.demos.gui.util;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

/**
 * A wrapper for panels to make them a frame.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
public class JFrameWrapper extends JFrame implements FrameWrapper {
    /**
     * The wrapped panel.
     */
    private JPanel m_panel;
    
    /**
     * @param name     the name of the frame (e.g. for resource injection)
     * @param panel    the wrapped panel
     */
    public JFrameWrapper(String name, JPanel panel) {
        if (EventSubscriber.class.isInstance(panel)) {
            m_panel = panel;
            EventBus.subscribe(InternalFrameEvent.class,
                (EventSubscriber<?>) panel);
        }
        setName(name);
        // set title (in case title is not set in properties file)
        setTitle(name);
        
        setContentPane(panel);
        
        ApplicationContext appContext = Application.getInstance().getContext();
        ResourceMap map = appContext.getResourceMap(panel.getClass());

        // inject values from properties file
        map.injectComponents(panel);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
    }
    
    /** {@inheritDoc} */
    public Component getContent() {
        return m_panel;
    }
    
    /** {@inheritDoc} */
    @Override
    public void dispose() {
        if (m_panel != null) {
            EventBus.unsubscribe(InternalFrameEvent.class, m_panel);
        }
        super.dispose();
    }
}
