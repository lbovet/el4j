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
package ch.elca.el4j.demos.gui.events;

/**
 * This event informs about the search progress.
 *
 * @svnLink $Revision$;$Date$;$Author$;$URL$
 *
 * @author Stefan Wismer (SWI)
 */
public class SearchProgressEvent {
	/**
	 * The status.
	 */
	private String status;
	
	/**
	 * @param status    the search progess status
	 */
	public SearchProgressEvent(String status) {
		this.status = status;
	}
	
	/**
	 * @return the status
	 */
	public String getMessage() {
		return status;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Search progress: [" + status + "]";
	}
}
