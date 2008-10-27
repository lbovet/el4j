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
package ch.elca.el4j.demos.secure.gui;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import ch.elca.el4j.gui.swing.GUIApplication;

/**
 * This class shows how to use the acegi security framework.
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
public class SecurityDemoForm extends JPanel {
	public SecurityDemoForm() {
		JLabel someLabel = new JLabel();
		
		add(someLabel);
		
		setPreferredSize(new Dimension(200, 50));
		setBounds(0, 0, 500, 50);
		
		// set security token
		//SecurityContextHolder.getContext().setAuthentication(
		//    new UsernamePasswordAuthenticationToken("el4normal", "el4j"));
		SecurityContextHolder.getContext().setAuthentication(
			new UsernamePasswordAuthenticationToken("el4super", "secret"));
		
		PrivateData data = (PrivateData) GUIApplication.getInstance()
			.getSpringContext().getBean("PrivateData");
		
		try {
			String result = data.getSecret();
			someLabel.setText(result);
		} catch (Exception e) {
			someLabel.setText("Access denied");
		}
	}
}
