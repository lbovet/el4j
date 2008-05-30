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
package ch.elca.el4j.services.statistics.detailed.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.elca.el4j.services.statistics.detailed.MeasureItem;
/**
 * 
 * This class can collect data from (possibly) two MeasureCollectorServices
 * and can display them in a webbrowser in svg-format.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Rashid Waraich (RWA)
 */
public class DataRepository {
    
    /**
     * The local MeasureCollectorService.
     */
    private MeasureCollectorService m_localCollectorService;
    
    /**
     * The remote MeasureCollectorService.
     */
    private MeasureCollectorService m_remoteCollectorService;    
    
    /**
     * Constructor.
     * 
     * @param localCollectorService
     *            Local MeasureCollectorService.
     * @param remoteCollectorService
     *          Remote MeasureCollectorServices.
     */
    public DataRepository(MeasureCollectorService localCollectorService, 
        MeasureCollectorService remoteCollectorService) {
        
        this.m_localCollectorService = localCollectorService;
        this.m_remoteCollectorService = remoteCollectorService;
    }    
   
    /**
     * Returns the joined Measures of both MeasureCollectorServices.
     * 
     * @return The joined ArrayList.
     */
    public List<MeasureItem> getAllMeasureItems() {
        List<MeasureItem> aList = new ArrayList<MeasureItem>();
        List<MeasureItem> tempList;
        Iterator<MeasureItem> iter;
        
        if (m_localCollectorService != null) {
            tempList = m_localCollectorService.getAllMeasureItems();
            iter = tempList.iterator();
            while (iter.hasNext()) {
                aList.add(iter.next());
            } 
        }

        
        if (m_remoteCollectorService != null) {
            tempList = m_remoteCollectorService.getAllMeasureItems();
            iter = tempList.iterator();
            while (iter.hasNext()) {
                aList.add(iter.next());
            } 
        }
        return aList;
    }
    
    /**
     * Returns the MeasureIds of the locally collected MeasureItems.
     * @return List of the first MeasureItem of each entry.
     */
    public List<MeasureItem> getFirstMeasureItems() {
        return m_localCollectorService.getFirstMeasureItems();
    }
}
