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

import ch.elca.el4j.demos.gui.events.ExampleEvent;
import ch.elca.el4j.demos.gui.events.SearchProgressEvent;
import ch.elca.el4j.gui.swing.cookswing.binding.Bindable;
import ch.elca.el4j.gui.swing.frames.ApplicationFrame;
import ch.elca.el4j.gui.swing.frames.ApplicationFrameAware;
import ch.elca.el4j.model.mixin.PropertyChangeListenerMixin;

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
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
@SuppressWarnings("serial")
public class EventBusDemoForm extends JPanel implements Bindable, ApplicationFrameAware {
	/**
	 * The list of exception entries.
	 */
	protected List<EventEntry> m_events;
	/**
	 * The table showing the occurred events.
	 */
	protected JTable m_eventsTable;
	
	/**
	 * The frame this component is embedded.
	 */
	private ApplicationFrame m_applicationFrame;
	
	/**
	 * The binder instance variable.
	 */
	protected final Binder m_binder = BinderManager.getBinder(this);
	
	/**
	 * This class represents an entry in the table.
	 */
	public class EventEntry {
		/**
		 * The time when the event occurred as String.
		 */
		private String m_time;
		
		/**
		 * The event.
		 */
		private String m_event;
		
		/**
		 * @param event    The event
		 */
		public EventEntry(String event) {
			SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
			m_time = fmt.format(new Date());
			m_event = event;
		}
		
		/**
		 * @param time     The time when the event occurred as String
		 * @param event    The event
		 */
		public EventEntry(String time, String event) {
			m_time = time;
			m_event = event;
		}
		
		/**
		 * @return Returns the time.
		 */
		public String getTime() {
			return m_time;
		}
		/**
		 * @return Returns the event.
		 */
		public String getEvent() {
			return m_event;
		}
	}
	
	public EventBusDemoForm() {
		setLayout(new BorderLayout());
		
		m_events = PropertyChangeListenerMixin.addPropertyChangeMixin(new ArrayList<EventEntry>());
		
		CookSwing cookSwing = new CookSwing(this);
		cookSwing.render("gui/eventBusForm.xml");
		
		m_binder.bindAll();
		
		// make first column as small as possible but not smaller than 50 pixel (a bit hacky)
		m_eventsTable.getColumnModel().getColumn(0).setMinWidth(50);
		m_eventsTable.getColumnModel().getColumn(1).setPreferredWidth(5000);
	}
	
	/**
	 * Clear the exception list.
	 */
	@Action
	public void clearList() {
		m_events.clear();
	}
	
	/**
	 * Close this form.
	 */
	@Action
	public void close() {
		m_applicationFrame.close();
	}
	
	/** {@inheritDoc} */
	public Binder getBinder() {
		return m_binder;
	}
	
	/** {@inheritDoc} */
	public void setApplicationFrame(ApplicationFrame applicationFrame) {
		m_applicationFrame = applicationFrame;
	}
	
	@EventSubscriber
	public void onEvent(ExampleEvent event) {
		m_events.add(0, new EventEntry("example event: [" + event.getMessage() + "]"));
	}
	
	@EventSubscriber
	public void onEvent(SearchProgressEvent event) {
		m_events.add(0, new EventEntry("search event: [" + event.getMessage() + "]"));
	}
	
	/**
	 * Remark: This event is registered an fired by
	 * {@link ch.elca.el4j.gui.swing.AbstractMDIApplication}.
	 *
	 * @param event    internalFrame event
	 */
	@EventSubscriber(eventClass = InternalFrameEvent.class)
	public void onEvent(InternalFrameEvent event) {
		m_events.add(0, new EventEntry("internal frame event: [" + event + "]"));
	}
}
