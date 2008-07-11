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

package ch.elca.el4j.services.persistence.generic.dto;

/**
 * Interface to provide primary key support.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public interface PrimaryKeyObject {
	/**
	 * @return Returns <code>true</code> if the primary key is new.
	 */
	public boolean isKeyNew();
	
	/**
	 * @param keyObject Is the key to set.
	 */
	public void setKey(Object keyObject);
	
	/**
	 * @return Returns the key as an object or <code>null</code> if it does not
	 *         exist.
	 */
	public Object getKeyAsObject();
	
	/**
	 * This method will be called when the primary key object is requested to
	 * generate a key object for himself.
	 */
	public void useGeneratedKey();
}
