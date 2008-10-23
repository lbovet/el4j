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

package ch.elca.el4j.seam.demo.action;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.Actor;

/**
 * 
 * Backing bean for holiday request login page.
 * 
 * @author Frank Bitzer (FBI)
 * 
 */

@Name("loginAction")
@Scope(ScopeType.SESSION)
public class LoginAction implements Serializable {

	/**
	 * jBPM Actor
	 */
	@In
	private Actor actor;

	/**
	 * User name
	 */
	private String user;

	/**
	 * get user
	 * 
	 * @return
	 */
	public String getUser() {
		return user;
	}

	/**
	 * set user
	 * 
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Login as user.
	 * 
	 * @return
	 */
	public String login() {
		actor.getGroupActorIds().clear();

		actor.setId(user);
		actor.getGroupActorIds().add("user");

		return "holiday";
	}

	/**
	 * Login as admin.
	 * 
	 * @return
	 */
	public String loginAdmin() {

		actor.getGroupActorIds().clear();

		actor.setId(user);
		// actor.getGroupActorIds().add("user");
		actor.getGroupActorIds().add("admin");

		return "holiday";
	}
	
	/**
	 * Logout current user and clear jBPM Actor.
	 * @return
	 */
	public String logout() {
	
		user = null;
		
		
		actor.setId(null);
		actor.getGroupActorIds().clear();
		
		return "logout";
	
	}
}
