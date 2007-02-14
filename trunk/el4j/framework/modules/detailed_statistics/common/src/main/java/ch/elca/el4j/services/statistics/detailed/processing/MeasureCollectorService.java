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

import java.util.List;

import ch.elca.el4j.services.statistics.detailed.MeasureItem;


/**
 * Local service (both in container and in clients) allowing to collect measures
 * in the system.
 * <p>
 * A measure is made of a collection of <code>MeasureItem</code> objects. A
 * measure may be started by a client or on a server and sub-measures are then
 * added by <code>MeasureInvoker</code>. <br>
 * In order to group the diverse sub-measures of a same global measure, they are
 * all identified by the same identifier. A sequence number is used to identify
 * the order of the diverse sub-measures (starting at 1 for the global measure).
 * <p>
 * The id and sequence of the current measure (or sub-measures) are stored in
 * the LEAF DetailedStatisticsSharedContextHolder object (keys defined by 
 * <code>CONTEXT_NAME_ID</code>
 * and <code>CONTEXT_NAME_SEQ</code>).
 * <p>
 * So the <code>MeasureInvoker</code> (wherever it is) starts a measure if
 * there is no defined <code>CONTEXT_NAME_ID</code> or it increases the
 * sequence number to create a sub-measure. 
 * 
 * This class was ported from Leaf 2.
 * Original authors: YMA,DBA. 
 * Leaf2 package name: ch.elca.leaf.services.measuring 
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Rashid Waraich (RWA)
 * 
 * @see MeasureItem
 * @see MeasureInterceptor
 */
public interface MeasureCollectorService {

    /** 
     * DetailedStatisticsSharedContextHolder key for saving the measure 
     * MeasureID object. 
     */
    public static final String CONTEXT_NAME_ID = "MEASURE_ID";

    /** 
     * DetailedStatisticsSharedContextHolder key for saving the 
     * measure sequence. 
     */
    public static final String CONTEXT_NAME_SEQ = "MEASURE_SEQ";

    /**
     * Adds a new measure.
     * 
     * @param item
     *            the measure to add
     */
    public void add(MeasureItem item);

    /**
     * Deletes all measures.
     * 
     * @param amount
     *            Dummy paramater, not used for non persistant collector (will
     *            simply be ignored). Perhaps required later if other
     *            CollectorServices are also ported.
     */
    public void delete(int amount);

    /**
     * Returns a List of all the measures.
     * 
     * @return the list of measures
     */
    public List<MeasureItem> getAllMeasureItems();

    /**
     * Inserts the locally stored measures in permanent storage.
     */
    public void writeMeasures();
    
    /**
     * @return The MeasureIds of the collected MeasureItems.
     */
    public List<MeasureItem> getFirstMeasureItems();
}
