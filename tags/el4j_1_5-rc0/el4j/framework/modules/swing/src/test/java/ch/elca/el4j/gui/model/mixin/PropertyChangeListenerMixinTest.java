package ch.elca.el4j.gui.model.mixin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.jdesktop.observablecollections.ObservableList;
import org.jdesktop.observablecollections.ObservableListListener;
import org.junit.Test;

import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.model.mixin.SaveRestoreCapability;

import com.silvermindsoftware.hitch.events.PropertyChangeListenerCapability;

public class PropertyChangeListenerMixinTest {

	@Test
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
	
	private class PropertyListener implements PropertyChangeListener {
		public PropertyChangeEvent event;
		
		public void propertyChange(PropertyChangeEvent evt) {
			event = evt;
		}
	}
	
	@SuppressWarnings("unused")
	private class ListListener implements ObservableListListener {
		public String event;
		
		public void listElementsAdded(ObservableList list, int index, int length) {
			event = "add";
		}
		public void listElementReplaced(ObservableList list, int index,
			Object oldElement) {
			event = "replace";
		}
		public void listElementsRemoved(ObservableList list, int index,
			List oldElements) {
			event = "remove";
		}
		public void listElementPropertyChanged(ObservableList list, int index) {
			event = "change";
		}
	}
	
	@Test
	public void testPropertyChangeListener() throws Exception {
		// create model
		ExampleModel model = new ExampleModelImpl();
		model = PropertyChangeListenerMixin.addPropertyChangeMixin(model);
		
		// add listener
		PropertyListener testListener = new PropertyListener();
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
	
	@Test
	public void testBinding() throws Exception {
		// create model
		ExampleModel model = new ExampleModelImpl();
		model = PropertyChangeListenerMixin.addPropertyChangeMixin(model);
		
		// create listener
		ListListener testListener = new ListListener();
		
		assertTrue(model.getList().size() == 0);
		assertTrue(model.getMap().size() == 0);
		
		List<Integer> list = model.getList();
		((ObservableList<Integer>) list).addObservableListListener(testListener);
		list.add(1);
		assertEquals(testListener.event.toString(), "add");
		list.remove(0);
		assertEquals(testListener.event.toString(), "remove");
		list.add(1);
		assertEquals(testListener.event.toString(), "add");
		list.set(0, 2);
		assertEquals(testListener.event.toString(), "replace");
	}
}
