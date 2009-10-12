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
package ch.elca.el4j.demos.secure.gui.forms;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ch.elca.el4j.core.context.annotations.LazyInit;
import ch.elca.el4j.demos.secure.gui.PrivateData;
import ch.elca.el4j.services.gui.swing.GUIApplication;

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
@LazyInit
@Scope("prototype")
@Component("securityDemoForm")
public class SecurityDemoForm extends JPanel {
	public SecurityDemoForm() {
		JLabel someLabel = new JLabel();
		
		add(someLabel);
		
		setPreferredSize(new Dimension(200, 50));
		setBounds(0, 0, 500, 50);
		
		// set security token (this is now done using login form)
		//SecurityContextHolder.getContext().setAuthentication(
		//    new UsernamePasswordAuthenticationToken("el4normal", "el4j"));
		//SecurityContextHolder.getContext().setAuthentication(
		//	new UsernamePasswordAuthenticationToken("el4super", "secret"));
		
		PrivateData data = (PrivateData) GUIApplication.getInstance()
			.getSpringContext().getBean("PrivateData");
		
		try {
			String result = data.getSecret();
			someLabel.setText(result);
		} catch (Exception e) {
			someLabel.setText("<html><b>Access denied.</b><br>"
				+ "Only super users are allowed.<br>Login as el4super/secret</html>");
		}
	}
}
