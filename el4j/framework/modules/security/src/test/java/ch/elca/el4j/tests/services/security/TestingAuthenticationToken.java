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
package ch.elca.el4j.tests.services.security;

/**
 * 
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Daniel Thomas (DTH) 
 * 
 */
 
import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.AbstractAuthenticationToken;

public class TestingAuthenticationToken extends AbstractAuthenticationToken {

	private Object credentials;
	private Object principal;
	private GrantedAuthority[] authorities;
	private boolean authenticated = false;



	public TestingAuthenticationToken(Object principal, Object credentials, GrantedAuthority[] authorities) {
		this.principal = principal;
		this.credentials = credentials;
		this.authorities = authorities;
	}

	protected TestingAuthenticationToken() {
		throw new IllegalArgumentException("Cannot use default constructor");
	}


	
	public void setAuthenticated(boolean isAuthenticated) {
		this.authenticated = isAuthenticated;
	}

	public boolean isAuthenticated() {
		return this.authenticated;
	}

	public GrantedAuthority[] getAuthorities() {
		return this.authorities;
	}

	public Object getCredentials() {
		return this.credentials;
	}

	public Object getPrincipal() {
		return this.principal;

	}
}
