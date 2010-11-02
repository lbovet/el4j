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

import javax.inject.Inject;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.java.dev.designgridlayout.DesignGridLayout;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jdesktop.application.Action;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ch.elca.el4j.services.gui.swing.GUIApplication;

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
	
	/** The cache. */
	@Inject
	private Ehcache cache;
	
	/** The label displaying "Input:". */
	private JLabel input;
	
	/** The text field for the input. */
	private JTextField inputField;
	
	/** The button to start encryption. */
	private JButton encryptButton;
	
	/** The label displaying "Result:". */
	private JLabel result;
	
	/** The text field for the 'encrypted' result. */
	private JTextField resultField;
	
	/**
	 * Constructor.
	 * @param application The application object.
	 */
	@Inject
	public CachingDemoForm(GUIApplication application) {
		createComponents();
		createLayout();
		
		encryptButton.setAction(application.getAction(this, "encryptAndCache"));
		setPreferredSize(new Dimension(500, 200));
	}
	
	/**
	 * Create the form components.
	 */
	protected void createComponents() {
		input = new JLabel();
		input.setName("input");
		
		inputField = new JTextField();

		encryptButton = new JButton();
		encryptButton.setName("encryptButton");
		
		result = new JLabel();
		result.setName("result");
		
		resultField = new JTextField();
		resultField.setEditable(false);
	}
	
	/**
	 * Layout the form components.
	 */
	private void createLayout() {
		// create the form layout
		DesignGridLayout layout = new DesignGridLayout(this);
		setLayout(layout);

		layout.row().left().add(input);
		layout.row().grid().add(inputField, 2);
		layout.row().right().add(encryptButton);
		layout.row().left().add(result);
		layout.row().grid().add(resultField, 2);
	}
	
	/**
	 * Encrypts the text in the input field, but first checks if the
	 * cache already contains the result.
	 * Called when the user clicks the "Encrypt" button.
	 */
	@Action
	public void encryptAndCache() {
		String input = inputField.getText();
		String output = null;
		
		if (cache.isKeyInCache(input)) {
			output = (String) cache.get(input).getValue();
		} else {
			output = encrypt(input);
			cache.put(new Element(input, output));
		}
		
		resultField.setText(output);
	}
	
	/**
	 * 'Encrypt' the input string. Just takes a really long time.
	 * @param input The string to encrypt.
	 * @return The encrypted string, or an error message if
	 * we got interrupted.
	 */
	private String encrypt(String input) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			return "I was interrupted, please try again.";
		}
		
		return "ThIsIsNoTaCtUaLlYeNcRyPtEd" + input;
	}
}
