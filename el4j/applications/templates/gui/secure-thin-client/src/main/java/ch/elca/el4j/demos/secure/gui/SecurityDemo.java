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
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXLoginPane.JXLoginFrame;
import org.jdesktop.swingx.auth.LoginService;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationCredentialsNotFoundException;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

import ch.elca.el4j.demos.gui.extension.AbstractGUIExtension;
import ch.elca.el4j.services.gui.swing.exceptions.Exceptions;
import ch.elca.el4j.services.gui.swing.exceptions.Handler;

/**
 * This GUI extension shows security related demos.
 * 
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class SecurityDemo extends AbstractGUIExtension {

	/**
	 * Default Constructor.
	 */
	public SecurityDemo() {
		Exceptions.getInstance().addHandler(new Handler() {
			public boolean recognize(Exception e) {
				if (e instanceof InvocationTargetException) {
					InvocationTargetException ite = (InvocationTargetException) e;
					if (ite.getTargetException() instanceof AccessDeniedException) {
						return true;
					} else if (ite.getTargetException() instanceof AuthenticationCredentialsNotFoundException) {
						return true;
					}
				}
				return false;
			}
			public int getPriority() {
				return -1000;
			}
			public boolean handle(Exception e) {
				JOptionPane.showMessageDialog(null, "Access denied.", "Security Demo", JOptionPane.ERROR_MESSAGE);
				return true;
			}
		});
	}
	
	/** {@inheritDoc} */
	public void extendMenuBar(JMenuBar menubar) {
		extendMenuBarDefault(menubar, "securityMenu");
	}
	
	/** {@inheritDoc} */
	public List<String> getActions() {
		return Arrays.asList("showLoginForm", "showSimpleDemo", "showSimpleDemoForced", "showSecureRefDb");
	}
	
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
				
				PrivateData data = (PrivateData) application.getSpringContext().getBean("PrivateData");
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
				String oldTitle = application.getMainFrame().getTitle();
				if (oldTitle.contains(" (")) {
					oldTitle = oldTitle.substring(0, oldTitle.indexOf(" ("));
				}
				
				if (status == JXLoginPane.Status.SUCCEEDED) {
					application.getMainFrame().setTitle(oldTitle + " (logged in as user '"
						+ SecurityContextHolder.getContext().getAuthentication().getName() + "')");
				} else {
					application.getMainFrame().setTitle(oldTitle + " (not logged in)");
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
		application.show("securityDemoForm");
	}
	
	/**
	 * Shows a very simple security demo. This menu item will not be deactivated.
	 */
	@Action
	public void showSimpleDemoForced() {
		application.show("securityDemoForm");
	}
	
	/**
	 * Shows the "secured" refdb from.
	 */
	@Action
	public void showSecureRefDb() {
		application.show("refDBDemoForm");
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
				AuthenticationManager manager = (AuthenticationManager) application.getSpringContext().getBean(
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
