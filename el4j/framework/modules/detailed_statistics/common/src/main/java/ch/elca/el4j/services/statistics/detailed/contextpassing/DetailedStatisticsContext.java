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
package ch.elca.el4j.services.statistics.detailed.contextpassing;

import ch.elca.el4j.services.statistics.detailed.MeasureId;

/**
 * 
 *  This class contains all fields, which are part of the passed 
 *   context for the detailed statistics. One instance exists per
 *   thread.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Rashid Waraich (RWA)
 * @author Philipp Oser (POS)
 */
public class DetailedStatisticsContext implements java.io.Serializable {

    /** Std serial version ID. */
    private static final long serialVersionUID = 5979980636453006516L;

    /**
     * The measuredId.
     */
    private MeasureId m_measureId = null;
    
    /** 
     * The depth of the call stack. Starts at 0.
     * It's incremented when a call arrives inbound, it's decremented 
     * when the variable arrives outbound.
     */
    private int m_depth = 0;
    
    /**
     * The Sequence number.
     */
    private int m_sequenceNumber = 0;
    
    /**
     * The hierarchy of the method call.
     * 
     * This variables are used if a service is called more than once in the 
     * same transaction. In such a case, the hierarchy is built more wide.
     * (e.g. [1-1] to [1-2])
     * further calls make the hierarchy more deep (e.g. [1-1] to [1-1-1])
     */
    private int[] m_hierarchy = new int[]{0};
    
    /**
     * The startTime of the (sub) measurement.
     */
    private long m_startTime = 0;

    /**
     * @return Returns the hierarchy.
     */
    public int[] getHierarchy() {
        return m_hierarchy;
    }
    
    /**
     * @param hierarchy Is the hierarchy to set.
     */
    public void setHierarchy(int[] hierarchy) {
        m_hierarchy = hierarchy;
    }
    
    /**
     * @return Returns the measureId.
     */
    public MeasureId getMeasureId() {
        return m_measureId;
    }
    
    /**
     * @param measureId Is the measureId to set.
     */
    public void setMeasureId(MeasureId measureId) {
        m_measureId = measureId;
    }
    
    /**
     * @return Returns the sequence.
     */
    public int getSequenceNumber() {
        return m_sequenceNumber;
    }
        
    /**
     * @param sequence Is the sequence to set.
     */
    public void setSequenceNumber(int sequence) {
        m_sequenceNumber = sequence;
    }
    
    /**
     * @return Returns the startTime.
     */
    public long getStartTime() {
        return m_startTime;
    }
    
    /**
     * @param startTime Is the startTime to set.
     */
    public void setStartTime(long startTime) {
        m_startTime = startTime;
    }

    /**
     * @return The depth of the call stack.
     */
    public int getDepth() {
        return m_depth;
    }

    /**
     * @param depth Set the depth of the call stack.
     */
    public void setDepth(int depth) {
        this.m_depth = depth;
    }
}
