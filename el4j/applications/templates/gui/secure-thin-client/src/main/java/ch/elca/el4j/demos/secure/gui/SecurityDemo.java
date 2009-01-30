/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2008 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationCredentialsNotFoundException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.jdesktop.application.Action;
import org.jdesktop.beans.AbstractBean;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXLoginPane.JXLoginFrame;
import org.jdesktop.swingx.auth.LoginService;

import ch.elca.el4j.demos.gui.GUIExtension;
import ch.elca.el4j.gui.swing.ActionsContext;
import ch.elca.el4j.gui.swing.GUIApplication;
import ch.elca.el4j.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.gui.swing.exceptions.Handler;
import ch.elca.el4j.gui.swing.util.MenuUtils;

/**
 * This GUI extension shows security related demos.
 * 
 * Remark: It extends AbstractBean because property change support is needed.
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
public class SecurityDemo extends AbstractBean implements GUIExtension {

	/**
	 * The main application which gets extended.
	 */
	GUIApplication m_application;
	
	/** {@inheritDoc} */
	public void setApplication(GUIApplication application) {
		m_application = application;
		
		Exceptions.getInstance().addHandler(new Handler() {
			public boolean recognize(Exception e) {
				if (e instanceof InvocationTargetException) {
					InvocationTargetException ite
						= (InvocationTargetException) e;
					if (ite.getTargetException() instanceof AccessDeniedException) {
						return true;
					} else if (ite.getTargetException() instanceof AuthenticationCredentialsNotFoundException) {
						return true;
					}
				}
				return false;
			}
			public void handle(Exception e) {
				JOptionPane.showMessageDialog(null,
					"Access denied.", "Security Demo",
					JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	/** {@inheritDoc} */
	public void extendMenuBar(JMenuBar menubar) {
		String[] reportMenuActionNames = {"showLoginForm", "showSimpleDemo", "showSimpleDemoForced", "showSecureRefDb"};
		
		ActionsContext actionsContext = ActionsContext.extendDefault(this);
		JMenu menu = MenuUtils.createMenu(actionsContext, "securityMenu", reportMenuActionNames);
		
		menubar.add(menu, menubar.getComponentCount() - 2);
	}
	
	/** {@inheritDoc} */
	public void extendToolBar(JToolBar toolbar) { }

	/**
	 * Shows a login form.
	 */
	@Action
	public void showLoginForm() {
		final boolean wasAdminBeforeLogin = isAdmin();
		
		final JXLoginFrame frame = JXLoginPane.showLoginFrame(new LoginService() {
			@Override
			public boolean authenticate(final String name, final char[] password, String server) throws Exception {
				
				// test credentials using client security (not server) -> only for demo purposes
				SecurityContextHolder.getContext().setAuthentication(
					new UsernamePasswordAuthenticationToken(name, new String(password)));
				
				PrivateData data = (PrivateData) m_application.getSpringContext().getBean("PrivateData");
				try {
					data.testAccess();
				} catch (Exception e) {
					return false;
				}
				
				// transfer security context to GUI thread (where it will be used later)
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						SecurityContextHolder.getContext().setAuthentication(
							new UsernamePasswordAuthenticationToken(name, new String(password)));
					}
				});
				
				return true;
			}
		});
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				JXLoginPane.Status status = frame.getStatus();
				String oldTitle = m_application.getMainFrame().getTitle();
				if (oldTitle.contains(" (")) {
					oldTitle = oldTitle.substring(0, oldTitle.indexOf(" ("));
				}
				
				if (status == JXLoginPane.Status.SUCCEEDED) {
					m_application.getMainFrame().setTitle(oldTitle + " (logged in as user '"
						+ SecurityContextHolder.getContext().getAuthentication().getName() + "')");
				} else {
					m_application.getMainFrame().setTitle(oldTitle + " (not logged in)");
				}
				
				// notify menu item
				firePropertyChange("admin", wasAdminBeforeLogin, isAdmin());
			}
		});

		frame.setTitle("Login (accounts: el4normal/el4j, el4super/secret)");
		frame.setVisible(true);
	}
	
	/**
	 * Shows a very simple security demo.
	 */
	@Action(enabledProperty = "admin")
	public void showSimpleDemo() {
		m_application.show("securityDemoForm");
	}
	
	/**
	 * Shows a very simple security demo. This menu item will not be deactivated.
	 */
	@Action
	public void showSimpleDemoForced() {
		m_application.show("securityDemoForm");
	}
	
	/**
	 * Shows the "secured" refdb from.
	 */
	@Action
	public void showSecureRefDb() {
		m_application.show("refDBDemoForm");
	}
	
	/**
	 * Indicates whether current user has "admin" permissions.
	 *  @return    <code>true</code> if user has admin rights
	 */
	public boolean isAdmin() {
		return hasRole("ROLE_SUPERUSER");
	}
	
	/**
	 * @param requestedRole    the role to check
	 * @return                 <code>true</code> if user has specified role.
	 */
	public boolean hasRole(String requestedRole) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return false;
		}
		try {
			// do a manual authentication if necessary (it's not done automatically because we access the roles
			// using authentication.getAuthorities() and not though a method interceptor)
			if (!authentication.isAuthenticated()) {
				AuthenticationManager manager = (AuthenticationManager) m_application.getSpringContext().getBean(
					"authenticationManager");
				authentication = manager.authenticate(authentication);
			}
	
			for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
				if (grantedAuthority.getAuthority().equals(requestedRole)) {
					return true;
				}
			}
		} catch (Exception e) {
			// continue -> return false
		}
		return false;
	}

}
