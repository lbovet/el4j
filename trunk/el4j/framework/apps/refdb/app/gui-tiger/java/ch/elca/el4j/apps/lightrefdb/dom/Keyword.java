/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.apps.lightrefdb.dom;

import javax.persistence.Entity;

import ch.elca.el4j.services.dom.annotations.MemberOrder;
import ch.elca.el4j.services.persistence.generic.dto.AbstractIntKeyIntOptimisticLockingDto;

/** 
 * .
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Adrian Moos (AMS)
 */

@MemberOrder({
    "name",
    "description"
})
@Entity
public class Keyword extends AbstractIntKeyIntOptimisticLockingDto {
    /** This keyword's name. */
    private String name;    
    
    /** 
     * Describes this keyword's meaning.
     */
    private String description;

    /***/
    public String getDescription() { return description; }
    /***/
    public void setDescription(String description) { 
        this.description = description; 
    }

    /***/
    public String getName() { return name; }
    /***/
    public void setName(String name) { this.name = name; }
}