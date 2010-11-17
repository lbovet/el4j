/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2010 by ELCA Informatique SA, Av. de la Harpe 22-24,
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
import java.awt.Font;

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ch.elca.el4j.demos.gui.service.CacheableService;
import ch.elca.el4j.services.gui.swing.GUIApplication;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.java.dev.designgridlayout.ISpannableGridRow;

/**
 * Demonstrates how to set up and use an Ehcache using spring.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Philipp Brüschweiler (PBW)
 */
@Lazy
@Component("cachingDemoForm")
public class CachingDemoForm extends JPanel {

	/**
	 * The service proxy. Only {@link CacheableService#computeResultCached(int)}
	 * is already cached.
	 */
	@Inject
	@Qualifier("remoteCachingService")
	private CacheableService remoteCachingService;

	/**
	 * A caching proxy around the service. The not yet cached method
	 * ({@link CacheableService#computeResult(int)}) is now cached
	 * locally.
	 */
	@Inject
	@Qualifier("localCachingService")
	private CacheableService localCachingService;
	
	/** "Uncached Service". */
	private JLabel uncachedTitle;
	/** "Remotely Cached Service". */
	private JLabel remoteTitle;
	/** "Locally Cached Service". */
	private JLabel localTitle;
	
	/** The label displaying "Input:". */
	private JLabel uncachedInputLabel;
	/** The label displaying "Input:". */
	private JLabel remoteInputLabel;
	/** The label displaying "Input:". */
	private JLabel localInputLabel;
	
	/** The label displaying "Result:". */
	private JLabel uncachedResultLabel;
	/** The label displaying "Result:". */
	private JLabel remoteResultLabel;
	/** The label displaying "Result:". */
	private JLabel localResultLabel;
	
	/** "Time taken:". */
	private JLabel uncachedTimeTakenLabel;
	/** "Time taken:". */
	private JLabel remoteTimeTakenLabel;
	/** "Time taken:". */
	private JLabel localTimeTakenLabel;
	
	/** Shows the time taken to call the method. */
	private JTextField uncachedTimeTakenField;
	/** Shows the time taken to call the method. */
	private JTextField remoteTimeTakenField;
	/** Shows the time taken to call the method. */
	private JTextField localTimeTakenField;
	
	/** Drops the cache of the remotely cached service. */
	private JButton remoteDropCacheButton;
	/** Drops the cache of the locally cached service. */
	private JButton localDropCacheButton;
	
	/** The button to call the uncached service. */
	private JButton uncachedButton;

	/** The input text field for the uncached service. */
	private JTextField uncachedInputField;
	
	/** The text field for the uncached service result. */
	private JTextField uncachedResultField;	
	
	/** The button to call the remotely cached service. */
	private JButton remoteButton;

	/** The input text field for the remotely cached service. */
	private JTextField remoteInputField;
	
	/** The text field for the remotely cached service result. */
	private JTextField remoteResultField;	
	
	/** The button to call the locally cached service. */
	private JButton localButton;

	/** The input text field for the locally cached service. */
	private JTextField localInputField;
	
	/** The text field for the locally cached service result. */
	private JTextField localResultField;
	
	/**
	 * Constructor.
	 * @param application The application object.
	 */
	@Inject
	public CachingDemoForm(GUIApplication application) {
		createComponents();
		createLayout();

		uncachedButton.setAction(application.getAction(this, "computeUncached"));
		remoteButton.setAction(application.getAction(this, "computeRemote"));
		localButton.setAction(application.getAction(this, "computeLocal"));
		
		remoteDropCacheButton.setAction(application.getAction(this, "remoteDropCache"));
		localDropCacheButton.setAction(application.getAction(this, "localDropCache"));
		setPreferredSize(new Dimension(500, 600));
	}
	
	/**
	 * Create the form components.
	 */
	protected void createComponents() {
		uncachedTitle = new JLabel();
		Font titleFont = uncachedTitle.getFont().deriveFont(Font.BOLD);
		uncachedTitle.setName("uncachedTitle");
		uncachedTitle.setFont(titleFont);
		remoteTitle = new JLabel();
		remoteTitle.setName("remoteTitle");
		remoteTitle.setFont(titleFont);
		localTitle = new JLabel();
		localTitle.setName("localTitle");
		localTitle.setFont(titleFont);
		
		uncachedInputLabel = new JLabel();
		uncachedInputLabel.setName("uncachedInputLabel");
		remoteInputLabel = new JLabel();
		remoteInputLabel.setName("remoteInputLabel");
		localInputLabel = new JLabel();
		localInputLabel.setName("localInputLabel");
		
		uncachedResultLabel = new JLabel();
		uncachedResultLabel.setName("uncachedResultLabel");
		remoteResultLabel = new JLabel();
		remoteResultLabel.setName("remoteResultLabel");
		localResultLabel = new JLabel();
		localResultLabel.setName("localResultLabel");
		
		uncachedTimeTakenLabel = new JLabel();
		uncachedTimeTakenLabel.setName("uncachedTimeTakenLabel");
		remoteTimeTakenLabel = new JLabel();
		remoteTimeTakenLabel.setName("uncachedTimeTakenLabel");
		localTimeTakenLabel = new JLabel();
		localTimeTakenLabel.setName("uncachedTimeTakenLabel");
		
		uncachedTimeTakenField = new JTextField();
		uncachedTimeTakenField.setEditable(false);
		remoteTimeTakenField = new JTextField();
		remoteTimeTakenField.setEditable(false);
		localTimeTakenField = new JTextField();
		localTimeTakenField.setEditable(false);
		
		uncachedInputField = new JTextField();
		remoteInputField = new JTextField();
		localInputField = new JTextField();
		
		remoteDropCacheButton = new JButton();
		remoteDropCacheButton.setName("remoteDropCacheButton");
		localDropCacheButton = new JButton();
		localDropCacheButton.setName("localDropCacheButton");
		
		uncachedButton = new JButton();
		uncachedButton.setName("uncachedButton");
		remoteButton = new JButton();
		remoteButton.setName("remoteButton");
		localButton = new JButton();
		localButton.setName("localButton");
		
		uncachedResultField = new JTextField();
		uncachedResultField.setEditable(false);
		remoteResultField = new JTextField();
		remoteResultField.setEditable(false);
		localResultField = new JTextField();
		localResultField.setEditable(false);
	}
	
	/**
	 * Layout the form components.
	 */
	private void createLayout() {
		// create the form layout
		DesignGridLayout layout = new DesignGridLayout(this);
		setLayout(layout);
		ISpannableGridRow grid;

		layout.row().center().add(uncachedTitle);
		grid = layout.row().grid();
		grid.add(uncachedInputLabel);
		grid.add(uncachedInputField, 3);
		layout.row().right().add(uncachedButton);
		grid = layout.row().grid();
		grid.add(uncachedResultLabel);
		grid.add(uncachedResultField, 3);
		grid = layout.row().grid();
		grid.add(uncachedTimeTakenLabel);
		grid.add(uncachedTimeTakenField, 3);
		
		layout.row().center().add(remoteTitle);
		grid = layout.row().grid();
		grid.add(remoteInputLabel);
		grid.add(remoteInputField, 3);
		layout.row().right().add(remoteDropCacheButton, remoteButton);
		grid = layout.row().grid();
		grid.add(remoteResultLabel);
		grid.add(remoteResultField, 3);
		grid = layout.row().grid();
		grid.add(remoteTimeTakenLabel);
		grid.add(remoteTimeTakenField, 3);
		
		layout.row().center().add(localTitle);
		grid = layout.row().grid();
		grid.add(localInputLabel);
		grid.add(localInputField, 3);
		layout.row().right().add(localDropCacheButton, localButton);
		grid = layout.row().grid();
		grid.add(localResultLabel);
		grid.add(localResultField, 3);
		grid = layout.row().grid();
		grid.add(localTimeTakenLabel);
		grid.add(localTimeTakenField, 3);
	}
	
	/**
	 * Contact the service and time the execution.
	 * @param service The service to contact.
	 * @param inputField The field to read the input from.
	 * @param resultField The field to write the result to.
	 * @param timeTakenField The field to write the time to.
	 */
	public void compute(CacheableService service, JTextField inputField,
		JTextField resultField, JTextField timeTakenField) {
		int input;
		
		try {
			input = Integer.parseInt(inputField.getText());
		} catch (NumberFormatException e) {
			resultField.setText("Please enter an integer!");
			timeTakenField.setText("");
			return;
		}
		
		long start = System.currentTimeMillis();
		int result = service.computeResult(input);
		long time = System.currentTimeMillis() - start;
		
		resultField.setText(Integer.toString(result));
		timeTakenField.setText(Long.toString(time) + " ms");
	}
	
	/**
	 * Compute the answer using the uncached service.
	 */
	@Action
	public void computeUncached() {
		int input;
		
		try {
			input = Integer.parseInt(uncachedInputField.getText());
		} catch (NumberFormatException e) {
			uncachedResultField.setText("Please enter an integer!");
			uncachedTimeTakenField.setText("");
			return;
		}
		
		long start = System.currentTimeMillis();
		int result = remoteCachingService.computeResult(input);
		long time = System.currentTimeMillis() - start;
		
		uncachedResultField.setText(Integer.toString(result));
		uncachedTimeTakenField.setText(Long.toString(time) + " ms");
	}
	
	/**
	 * Compute the answer using the remotely cached service.
	 */
	@Action
	public void computeRemote() {
		int input;
		
		try {
			input = Integer.parseInt(remoteInputField.getText());
		} catch (NumberFormatException e) {
			remoteResultField.setText("Please enter an integer!");
			remoteTimeTakenField.setText("");
			return;
		}
		
		long start = System.currentTimeMillis();
		int result = remoteCachingService.computeResultCached(input);
		long time = System.currentTimeMillis() - start;
		
		remoteResultField.setText(Integer.toString(result));
		remoteTimeTakenField.setText(Long.toString(time) + " ms");
	}
	
	/**
	 * Compute the answer using the locally cached service.
	 */
	@Action
	public void computeLocal() {
		int input;
		
		try {
			input = Integer.parseInt(localInputField.getText());
		} catch (NumberFormatException e) {
			localResultField.setText("Please enter an integer!");
			localTimeTakenField.setText("");
			return;
		}
		
		long start = System.currentTimeMillis();
		int result = localCachingService.computeResult(input);
		long time = System.currentTimeMillis() - start;
		
		localResultField.setText(Integer.toString(result));
		localTimeTakenField.setText(Long.toString(time) + " ms");
	}
	
	/**
	 * Drop the caches of the remotely cached service.
	 */
	@Action
	public void remoteDropCache() {
		remoteCachingService.deleteCaches();
	}
	
	/**
	 * Drop the caches of the locally cached service.
	 */
	@Action
	public void localDropCache() {
		localCachingService.deleteCaches();
	}
}
