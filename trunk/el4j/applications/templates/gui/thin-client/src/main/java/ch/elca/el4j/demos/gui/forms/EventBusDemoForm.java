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
package ch.elca.el4j.demos.gui.forms;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.InternalFrameEvent;

import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.application.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.gui.events.ExampleEvent;
import ch.elca.el4j.demos.gui.events.SearchProgressEvent;
import ch.elca.el4j.services.gui.model.mixin.PropertyChangeListenerMixin;
import ch.elca.el4j.services.gui.swing.cookswing.binding.Bindable;
import ch.elca.el4j.services.gui.swing.frames.ApplicationFrame;
import ch.elca.el4j.services.gui.swing.frames.ApplicationFrameAware;

import com.silvermindsoftware.hitch.Binder;
import com.silvermindsoftware.hitch.BinderManager;

import cookxml.cookswing.CookSwing;

/**
 * This class demonstrates the basic use of EventBus.
 *
 * Event handlers are all methods having the following form:
 * <code>
 * @EventSubscriber(eventClass=SomeEvent.class)
 * public void onEvent(SomeEvent event) { ... }
 * </code>
 *
 * To subscribe to these events, it is necessary to call
 * <code>AnnotationProcessor.process(this)</code>, unsubscription is done by
 * <code>AnnotationProcessor.unsubscribe(this)</code>.
 * This is done for us by using {@link JFrameWrapper} or
 * {@link JInteralFrameWrapper}.
 *
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@Lazy
@Component("eventBusDemoForm")
@SuppressWarnings("serial")
public class EventBusDemoForm extends JPanel implements Bindable, ApplicationFrameAware {
	/**
	 * The list of event entries.
	 */
	protected List<EventEntry> events;
	/**
	 * The table showing the occurred events.
	 */
	protected JTable eventsTable;
	
	/**
	 * The frame this component is embedded.
	 */
	private ApplicationFrame applicationFrame;
	
	/**
	 * The binder instance variable.
	 */
	protected final Binder binder = BinderManager.getBinder(this);
	
	/**
	 * This class represents an entry in the table.
	 */
	public class EventEntry {
		/**
		 * The time when the event occurred as String.
		 */
		private String time;
		
		/**
		 * The event.
		 */
		private String event;
		
		/**
		 * @param event    The event
		 */
		public EventEntry(String event) {
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
			time = fmt.format(new Date());
			this.event = event;
		}
		
		/**
		 * @param time     The time when the event occurred as String
		 * @param event    The event
		 */
		public EventEntry(String time, String event) {
			this.time = time;
			this.event = event;
		}
		
		/**
		 * @return Returns the time.
		 */
		public String getTime() {
			return time;
		}
		/**
		 * @return Returns the event.
		 */
		public String getEvent() {
			return event;
		}
	}
	
	public EventBusDemoForm() {
		setLayout(new BorderLayout());
		
		events = PropertyChangeListenerMixin.addPropertyChangeMixin(new ArrayList<EventEntry>());
		
		CookSwing cookSwing = new CookSwing(this);
		cookSwing.render("gui/eventBusForm.xml");
		
		binder.bindAll();
		
		// make first column as small as possible but not smaller than 50 pixel (a bit hacky)
		eventsTable.getColumnModel().getColumn(0).setMinWidth(50);
		eventsTable.getColumnModel().getColumn(1).setPreferredWidth(5000);
	}
	
	/**
	 * Clear the exception list.
	 */
	@Action
	public void clearList() {
		events.clear();
	}
	
	/**
	 * Close this form.
	 */
	@Action
	public void close() {
		applicationFrame.close();
	}
	
	/** {@inheritDoc} */
	public Binder getBinder() {
		return binder;
	}
	
	/** {@inheritDoc} */
	public void setApplicationFrame(ApplicationFrame applicationFrame) {
		this.applicationFrame = applicationFrame;
	}
	
	@EventSubscriber
	public void onEvent(ExampleEvent event) {
		events.add(0, new EventEntry("example event: [" + event.getMessage() + "]"));
	}
	
	@EventSubscriber
	public void onEvent(SearchProgressEvent event) {
		events.add(0, new EventEntry("search event: [" + event.getMessage() + "]"));
	}
	
	/**
	 * Remark: This event is registered an fired by
	 * {@link ch.elca.el4j.services.gui.swing.AbstractMDIApplication}.
	 *
	 * @param event    internalFrame event
	 */
	@EventSubscriber(eventClass = InternalFrameEvent.class)
	public void onEvent(InternalFrameEvent event) {
		events.add(0, new EventEntry("internal frame event: [" + event + "]"));
	}
}
