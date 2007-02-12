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
package ch.elca.el4j.services.statistics.detailed;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ch.elca.el4j.services.statistics.detailed.contextpassing.DetailedStatisticsSharedContextHolder;
import ch.elca.el4j.services.statistics.detailed.processing.MeasureCollectorService;


/**
 * A <code>MeasureID</code> uniquely identifies a global measure. <p>
 *
 * The ID of a measure is made unique through :
 * <ul>
 *   <li>the hostname of the machine
 *   <li>the invocation time of the method
 * </ul>
 *
 * Remark : the sequential number associated to each measure is not
 * part of the measure ID.
 *
 * The ID of the measure is reused during the whole lifetime of an
 * end-to-end measure (global measure with all its sub-measures). The 
 * sequential number will be increased for each sub-measure.
 *
 * 
 * This class was ported from Leaf 2.
 * Original authors: YMA, DBA. 
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
 */
public class MeasureId implements Serializable {

    /** 
     * The separator used for the string represention. 
     */
    private static final String FORMATTED_SEP = "::";
    
    
    /**
     * The prefixCounter is a module MAXSHORT counter. It is required, 
     * in order to ensure that no two requests have the same id. 
     * This was not ensured till now, because measureId generated within 
     * the same milli-second on the same host are not distinguishable!
     * 
     * It is assumed, that the host, where this program is running can not
     * perform more than MAXINT-1 operations and interceptions in one single 
     * milli-second (which seems reasonable at the moment).
     * 
     */
    private static short s_prefixCounter = 0;
    
    
    /** The host name of the ID. */
    private String m_host;
    
    
    /** The invocation time of the ID. */
    private long m_invocationTime;
    
    
    /**
     * The prefix of this MeasureId.
     */
    private short m_prefix;

    /**
     * Creates a MeasureID object for the localhost machine and with the current
     * time.
     */
    public MeasureId() {
        try {
            m_host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            // nothing
            m_host = null;
        }
        s_prefixCounter = (short) ((s_prefixCounter + 1) % Short.MAX_VALUE);
        m_prefix = s_prefixCounter;
        m_invocationTime = System.currentTimeMillis();
    }

    /**
     * Creates a MeasureID object for a given host and a given invocation time.
     * 
     * @param host
     *            the host name of the ID
     * @param invocationTime
     *            the invocation time of the ID
     */
    public MeasureId(String host, long invocationTime) {
        m_host = host;
        m_invocationTime = invocationTime;
    }

    /**
     * Creates a new MeasureID object for the localhost machine and with the
     * current time.
     * <p>
     * Remark : This method should be called only by the client.
     * 
     * @return new MeasureID object
     */
    public static MeasureId createID() {
        return new MeasureId();
    }


    /**
     * Returns the host name of the measure ID.
     * 
     * @return host name
     */
    public String getHost() {
        return m_host;
    }

    /**
     * Returns the invocation time of the measure ID.
     * 
     * @return invocation time
     */
    public long getInvocationTime() {
        return m_invocationTime;
    }

    /**
     * Returns a human-readable representation of the measure ID.
     * 
     * @return measure representation
     */
    public String getFormattedString() {
        return m_host + FORMATTED_SEP + m_prefix + ":" + m_invocationTime;
    }

    /**
     * Compares two MeasureID objects and returns true if they are equivalent.
     * 
     * @param obj
     *            the MeasureID object to compare with
     * @return true or false
     */
    public boolean equals(Object obj) {
        MeasureId measure = (MeasureId) obj;

        return (m_host.equals(measure.getHost()) && m_invocationTime == measure
            .getInvocationTime());
    }

    /**
     * Returns a human-readable representation of the object.
     * 
     * @return human-readable representation
     */
    public String toString() {
        return getFormattedString();
    }
}
