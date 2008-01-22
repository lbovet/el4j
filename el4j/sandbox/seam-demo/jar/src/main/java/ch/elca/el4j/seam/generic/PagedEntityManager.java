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
package ch.elca.el4j.seam.generic;

import java.util.List;

/**
 * Provides methods needed for paging.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Frank Bitzer (FBI)
 */
public interface PagedEntityManager {
    
    
   
    /**
     * Count total number of entities of given type.
     * @param entityClassName
     * @return
     */
    public int getEntityCount(String entityClassName);
    
    
    
    /**
     * Sets the range of the data to be returned when 
     *<code>getEntities</code> is called.
     *
     * @param firstResult
     * @param count
     */
    public void setRange(int firstResult, int count);
    
    

    /**
     * Support for paging. Returns entities after range was set using 
     * <code>setRange</code>.
     * 
     * @param entityClassName
     * @return
     */
    public List getEntities(String entityClassName);
    
    
    /**
     * Underlying data was changed. If this property is true, a complete reload 
     * and reset of the DataTable is forced.
     * 
     * @return
     */
    public boolean isViewReset();
    
    /**
     * Setter for viewReset. viewReset is set to false again when the DataTable 
     * was  refreshed.
     * 
     * @param value
     */
    public void setViewReset(boolean value);
}
