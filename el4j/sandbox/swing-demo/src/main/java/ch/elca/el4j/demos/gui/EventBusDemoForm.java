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
package ch.elca.el4j.demos.gui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import ch.elca.el4j.demos.gui.events.ExampleEvent;
import ch.elca.el4j.demos.gui.events.SearchProgressEvent;
import ch.elca.el4j.demos.gui.util.FrameWrapper;
import ch.elca.el4j.gui.swing.GUIApplication;

/**
 * This class demonstates the basic use of EventBus.
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
@SuppressWarnings("unchecked")
public class EventBusDemoForm extends JPanel implements EventSubscriber {

    private JLabel m_lastEvent;
    
    public EventBusDemoForm(GUIApplication app) {
        // Annotations would make it more easier, 
        // but they didn't work properly in 1.1 beta2 ...
        //AnnotationProcessor.process(this);

        // ... so we have to do it manually (see onEvent for unsubscribe)
        EventBus.subscribe(ExampleEvent.class, this);
        EventBus.subscribe(SearchProgressEvent.class, this);
        
        
        m_lastEvent = new JLabel();
        add(m_lastEvent);
        
        setPreferredSize(new Dimension(700, 50));
        setBounds(0, 0, 500, 50);
    }
    
    public void onEvent(Object event) {
        if (event instanceof InternalFrameEvent) {
            InternalFrameEvent ifEvent = (InternalFrameEvent) event;
            if (ifEvent.getID() == InternalFrameEvent.INTERNAL_FRAME_CLOSING) {
                if (ifEvent.getInternalFrame() instanceof FrameWrapper) {
                    FrameWrapper wrapper
                        = (FrameWrapper) ifEvent.getInternalFrame();
                    if (wrapper.getContent() == this) {
                        // This frame gets closed! -> cleanup
                        EventBus.unsubscribe(ExampleEvent.class, this);
                        EventBus.unsubscribe(SearchProgressEvent.class, this);
                    }
                }
            }
        }
        m_lastEvent.setText("Last event: " + event.toString());
    }
    
    /*
    //, referenceStrength=ReferenceStrength.STRONG
    @EventSubscriber(eventClass=ExampleEvent.class)
    public void onEvent(ExampleEvent event) {
        m_lastEvent.setText("example event: [" + event.getMessage() + "]");
    }
    
    @EventSubscriber(eventClass=InternalFrameEvent.class)
    public void onEvent(InternalFrameEvent event) {
        if (event.getID() == event.INTERNAL_FRAME_CLOSED) {
            System.out.println(event.getSource() + "closed");
        }else {
            System.out.println("[other InternalFrameEvent received]");
        }
    }
    */
}
