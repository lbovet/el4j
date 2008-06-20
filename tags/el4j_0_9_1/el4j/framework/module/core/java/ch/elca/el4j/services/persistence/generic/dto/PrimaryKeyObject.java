/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */

package ch.elca.el4j.services.persistence.generic.dto;

/**
 * Interface to provide primary key support.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
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
     * This method will be called when the primary key object is requested to
     * generate a key object for himself.
     */
    public void useGeneratedKey();
}