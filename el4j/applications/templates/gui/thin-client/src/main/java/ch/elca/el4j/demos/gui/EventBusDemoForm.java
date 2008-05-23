/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
package ch.elca.el4j.demos.gui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameEvent;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import ch.elca.el4j.demos.gui.events.ExampleEvent;
import ch.elca.el4j.demos.gui.events.SearchProgressEvent;
import ch.elca.el4j.gui.swing.wrapper.JFrameWrapper;
import ch.elca.el4j.gui.swing.wrapper.JInteralFrameWrapper;

/**
 * This class demonstates the basic use of EventBus.
 * 
 * Event handlers are all methods having the following form:
 * <code>
 * @EventSubscriber(eventClass=SomeEvent.class)
 * public void onEvent(SomeEvent event) { ... }
 * </code>
 * 
 * To subscribe to these events, it is necessary to call
 * <code>AnnotationProcessor.process(this)</code>, unsubscription is done by
 * <code>AnnotationProcessor.unsubscribe(this)</code>
 * 
 * This is done for us by using {@link JFrameWrapper} or
 * {@link JInteralFrameWrapper}.
 * 
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
public class EventBusDemoForm extends JPanel {

    private JLabel m_lastEvent;
    
    public EventBusDemoForm() {
        m_lastEvent = new JLabel();
        add(m_lastEvent);
        
        setPreferredSize(new Dimension(700, 50));
        setBounds(0, 0, 500, 50);
    }
    
    @EventSubscriber(eventClass = ExampleEvent.class)
    public void onEvent(ExampleEvent event) {
        m_lastEvent.setText("example event: [" + event.getMessage() + "]");
    }
    
    @EventSubscriber(eventClass = SearchProgressEvent.class)
    public void onEvent(SearchProgressEvent event) {
        m_lastEvent.setText("search event: [" + event.getMessage() + "]");
    }
    
    /**
     * Remark: This event is registered an fired by
     * {@link ch.elca.el4j.gui.swing.AbstractMDIApplication}.
     * 
     * @param event    internalFrame event
     */
    @EventSubscriber(eventClass = InternalFrameEvent.class)
    public void onEvent(InternalFrameEvent event) {
        m_lastEvent.setText("internal frame event: [" + event + "]");
    }
}
