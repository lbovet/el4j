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

import java.awt.Dimension;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.bushe.swing.event.EventBus;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.gui.events.SearchProgressEvent;
import ch.elca.el4j.demos.gui.events.SearchRefDBEvent;
import ch.elca.el4j.services.gui.swing.GUIApplication;

/**
 * This class represents a simple search dialog.
 *
 * A search triggers two events: The {@link SearchRefDBEvent} is used to query
 * the refDB and the various {@link SearchProgressEvent} events are just to
 * generate status events for long running tasks. They can be tracked in the
 * {@link EventBusDemoForm}.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
@LazyInit
@Component("searchForm")
public class SearchForm extends AbstractSearchForm {
	/**
	 * The appFramework application.
	 */
	private GUIApplication application;
	
	/**
	 * The resource map.
	 */
	private transient ResourceMap resourceMap;
	
	/**
	 * The background search task.
	 */
	@SuppressWarnings("unchecked")
	private transient Task currentSearch = null;
	
	/**
	 * The constructor.
	 */
	@Autowired
	public SearchForm(GUIApplication application) {
		super();
		
		this.application = application;
		resourceMap = application.getContext()
			.getResourceMap(SearchForm.class);
		searchButton.setAction(application.getAction(this, "search"));
		
		// Checkstyle: MagicNumber off
		setPreferredSize(new Dimension(200, 300));
		// Checkstyle: MagicNumber on
	}
	
	/** {@inheritDoc} */
	@Override
	protected void createOptionalComponents() {
		super.createOptionalComponents();
		
		// Checkstyle: MagicNumber off
		options = new JComponent[4];
		options[0] = new JCheckBox();
		options[0].setName("option0");
		options[1] = new JCheckBox();
		options[1].setName("option1");
		options[2] = new JLabel();
		options[2].setName("info0");
		options[3] = new JLabel();
		options[3].setName("info1");
		// Checkstyle: MagicNumber on
	}
	
	
	/**
	 * Progress is interdeterminate for the first 150ms, then run for another
	 * 1500, marking progress every 150ms.
	 */
	private class BackgroundSearch extends Task<Void, Void> {
		/**
		 * The constructor.
		 */
		BackgroundSearch() {
			super(application);
			searchButton.setText(getRes("cancel"));
		}

		/** {@inheritDoc} */
		@Override
		protected Void doInBackground() throws InterruptedException {
			// send refBD event
			EventBus.publish(new SearchRefDBEvent(new String[]{"description"},
				"%" + searchField.getText() + "%"));
			
			// Checkstyle: MagicNumber off
			for (int i = 0; i < 10; i++) {
				sendEvent(String.format(getRes("progress"), i));
				Thread.sleep(150L);
				setProgress(i, 0, 9);
			}
			Thread.sleep(150L);
			// Checkstyle: MagicNumber on
			return null;
		}

		/** {@inheritDoc} */
		@Override
		protected void succeeded(Void ignored) {
			sendEvent(getRes("done"));
			searchField.setText(searchField.getText() + " found!");
		}

		/** {@inheritDoc} */
		@Override
		protected void cancelled() {
			sendEvent(getRes("canceled"));
		}
		
		/** {@inheritDoc} */
		@Override
		protected void finished() {
			super.finished();
			
			searchButton.setText(getRes("search"));
			currentSearch = null;
		}
		
		/**
		 * Sends a progess event via eventbus.
		 *
		 * @param message    the message to send
		 */
		private void sendEvent(String message) {
			EventBus.publish(new SearchProgressEvent(message));
		}

	}
	
	/**
	 * Perform the search.
	 *
	 * @return    the task to run
	 */
	@SuppressWarnings("unchecked")
	@Action
	public Task search() {
		if (currentSearch == null) {
			currentSearch = new BackgroundSearch();
			return currentSearch;
		} else {
			currentSearch.cancel(true);
			return null;
		}
	}
	
	/**
	 * @param id    the resource ID
	 * @return      the String associated with the given resource ID
	 */
	protected String getRes(String id) {
		return resourceMap.getString(id);
	}
}
