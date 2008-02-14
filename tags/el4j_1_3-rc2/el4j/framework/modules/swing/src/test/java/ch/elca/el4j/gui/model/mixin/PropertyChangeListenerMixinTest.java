package ch.elca.el4j.gui.model.mixin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import com.silvermindsoftware.hitch.events.PropertyChangeListenerCapability;

import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.model.mixin.SaveRestoreCapability;

import junit.framework.TestCase;

public class PropertyChangeListenerMixinTest extends TestCase {

    public void testSaveRestore() throws Exception {
        ExampleModel model = new ExampleModelImpl();

        model = PropertyChangeListenerMixin.addPropertyChangeMixin(model);
        
        model.setProperty1("some text");
        
        // save state
        ((SaveRestoreCapability) model).save();
        assertEquals(model.getProperty1(), "some text");
        
        model.setProperty1("some other text");
        assertEquals(model.getProperty1(), "some other text");
        
        // restore state
        ((SaveRestoreCapability) model).restore();
        assertEquals(model.getProperty1(), "some text");
        
        model.setProperty1("some new text");
        assertEquals(model.getProperty1(), "some new text");
        
        // save again
        ((SaveRestoreCapability) model).save();
        assertEquals(model.getProperty1(), "some new text");
        
        model.setProperty1("some other new text");
        assertEquals(model.getProperty1(), "some other new text");
        
        // restore again
        ((SaveRestoreCapability) model).restore();
        assertEquals(model.getProperty1(), "some new text");
    }
    
    private class TestListener implements PropertyChangeListener {
        public PropertyChangeEvent event;
        
        public void propertyChange(PropertyChangeEvent evt) {
            event = evt;
        }
    }
    
    public void testPropertyChangeListener() throws Exception {
        ExampleModel model = new ExampleModelImpl();

        model = PropertyChangeListenerMixin.addPropertyChangeMixin(model);
        
        TestListener testListener = new TestListener();
        
        ((PropertyChangeListenerCapability) model).addPropertyChangeListener(testListener);
        model.setProperty1("initial value");
        
        assertEquals(testListener.event.getOldValue(), null);
        assertEquals(testListener.event.getNewValue(), "initial value");
        assertEquals(testListener.event.getPropertyName(), "property1");
        
        model.setProperty1("modified value");
        
        assertEquals(testListener.event.getOldValue(), "initial value");
        assertEquals(testListener.event.getNewValue(), "modified value");
        
        // remove listener
        ((PropertyChangeListenerCapability) model).removePropertyChangeListener(testListener);
        
        model.setProperty1("silent update");
        
        assertEquals(testListener.event.getOldValue(), "initial value");
        assertEquals(testListener.event.getNewValue(), "modified value");
        
        // test listener on single property
        ((PropertyChangeListenerCapability) model).addPropertyChangeListener("property1", testListener);
        
        model.setProperty1("property1 has changed");
        
        assertEquals(testListener.event.getOldValue(), "silent update");
        assertEquals(testListener.event.getNewValue(), "property1 has changed");
        
        ((PropertyChangeListenerCapability) model).removePropertyChangeListener("property1", testListener);
        
        // test listener on property that doesn't exist
        ((PropertyChangeListenerCapability) model).addPropertyChangeListener("property99", testListener);
        
        model.setProperty1("property2 doesn't exist");
        
        assertEquals(testListener.event.getOldValue(), "silent update");
        assertEquals(testListener.event.getNewValue(), "property1 has changed");
        
        ((PropertyChangeListenerCapability) model).removePropertyChangeListener("property99", testListener);
    }
}
