package ch.elca.el4j.demos.gui;

import java.awt.Dimension;

import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.event.InternalFrameEvent;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.EventSubscriber;

import ch.elca.el4j.demos.gui.events.ExampleEvent;

/** 
 * Demo for an mdi GUI 
 * 
 * @author SWI
 *
 */ 
public class SomeInternalFrame extends JInternalFrame implements EventSubscriber {

    public SomeInternalFrame() {
        // Annotations would make it more easier, but they don't work yet
        //AnnotationProcessor.process(this);

        // so we have to do it manually (see also dispose)
        EventBus.subscribe(ExampleEvent.class, this);
        EventBus.subscribe(InternalFrameEvent.class, this);

        setTitle(null);
        JLabel someLabel = new JLabel();
        someLabel.setName("someLabel");
        getContentPane().add(someLabel);
        
        setResizable(true);
        //setMaximizable(true);
        setClosable(true);
        //setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(300, 200));
        setBounds(0, 0, 300, 100);
        setVisible(true);
    }
 
    
    public void onEvent(Object event) {
        if (event instanceof InternalFrameEvent) {
            InternalFrameEvent ifEvent = (InternalFrameEvent) event;
            if (ifEvent.getID() == InternalFrameEvent.INTERNAL_FRAME_CLOSED) {
                System.out.println("closed");
            }
        }
        System.out.println("[event received: " + event.toString() + "]");
    }
    
    /*
    @EventSubscriber(eventClass=ExampleEvent.class)
    public void onEvent(ExampleEvent event) {
        System.out.println("[example event received: " + event.getMessage() + "]");
    }
    //, referenceStrength=ReferenceStrength.STRONG
    
    @EventSubscriber(eventClass=InternalFrameEvent.class)
    public void onEvent(InternalFrameEvent event) {
        if (event.getID() == event.INTERNAL_FRAME_CLOSED) {
            System.out.println(event.getSource() + "closed");
        }else {
            System.out.println("[other InternalFrameEvent received]");
        }
    }
    */
    
    @Override
    public void dispose() {
        EventBus.unsubscribe(ExampleEvent.class, this);
        EventBus.unsubscribe(InternalFrameEvent.class, this);
        super.dispose();
    }
}
