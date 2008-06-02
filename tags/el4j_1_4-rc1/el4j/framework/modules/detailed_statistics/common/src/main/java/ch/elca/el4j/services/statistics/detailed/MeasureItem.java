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
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Represents a performance measure collected by the detailed measurement 
 * service.
 * <p>
 * The level attribute of each measure can be used freely. <p>
 *
 * Please refer to the package level javadoc 
 * {@link ch.elca.leaf.services.measuring}
 * for more information on the format or the usage.
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
 * @author Philipp Oser (POS)
 */
public class MeasureItem implements Serializable {

    /**
     * Serial ID, as this class is serializable.
     */
    private static final long serialVersionUID = 5083234396519618596L;

    /** Date and time format. */
    private static final SimpleDateFormat DATE_FORMAT 
        = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");

    /** The id of the measure. */
    private MeasureId m_id;

    /** The sequence of the measure. */
    private int m_seq;

    /** The client (host) of the measure. */
    private String m_client;

    /** The level (measure type) of the measure. */
    private String m_level;

    /** The EJB doing the measure (not available in case 
     * of a database measure). */
    private String m_service;

    /** The method doing the measure. */
    private String m_method;

    /** The start time of the measure. */
    private long m_startTime;

    /** The duration of the measure. */
    private long m_duration;
    
    /**
     * The service name.
     */
    private String m_serviceName;

    /** The hierachy of the measure.
     * Example:
     * [1] First measure on svc A
     * [1,1] First measure on svc B
     * [1,2] Second measure on svc B
     * [1,2,1] First measure on svc C, following call is second measure on svc B
     */
    private String m_hierarchy;

    /**
     * Creates a new MeasureItem object.
     *
    * @param id The id of the measure(a MeasureID object).
    * This value is the same during one measure and all following
    * measures. Following measures are additional service calls in the same
    * method and also all services called after the measuring has been started.
    * <br>Attribute in database: MID
    *
    * @param seq The sequence of the measure (integer that should start at
    * 1 and be incremented for each sub-value of a same global measure).
    * This value displays the deepth of the method call.
    * <br>Attribute in database: SEQ
    *
    * @param client The client (host name of a machine) for which the measure
    * is done.
    * <br>Attribute in database: CLIENT
    *
    * @param level The type of the measure (free usage). If Execution Unit (EU) 
    * is gui or web this value commonly is named CLIENT, if EU is cejb then the
    * value is EJB_CONTAINER.
    * <br>Attribute in database: TYPE
    *
    * @param service The JNDI name of the EJB for which the measure is done
    * (not available in case of a database measure)
    * <br>Attribute in database: EJBNAME
    *
    * @param methodName The name of the method for which the measure is done
    * <br>Attribute in database: METHODNAME
    *
    * @param startTime The start time of the measure (as long)
    * <br>Attribute in database: STARTTIME
    *
    * @param duration The duration of the measure in milliseconds
    * <br>Attribute in database: DURATION
    *
    * @param hierarchy The hierarchy of the global measure. This information
    * is required to show which call follows the other. Because of time shift
    * between differnet machines, the starttime may vary for- or backward.
    * <br>Attribute in database: HIERARCHY
    */
    public MeasureItem(MeasureId id, int seq, String client, String level,
                       String service, String methodName, long startTime,
                       long duration, String hierarchy) {
        m_id = id;
        m_seq = seq;
        m_client = client;
        m_level = level;
        m_service = service;
        m_method = methodName;
        m_startTime = startTime;
        m_duration = duration;
        m_hierarchy = hierarchy;
        m_serviceName = service + "." + getShortLevel();
        
    }

    /**
     * Returns the MeasureID id of the measure.
     *
     * @return the measure id
     */
    public MeasureId getID() {
        return m_id;
    }
    
    /**
     * Returns the sequence of the measure.
     *
     * @return the measure sequence
     */
    public int getSequence() {
        return m_seq;
    }

    /**
     * Returns the client of the measure.
     *
     * @return the measure client
     */
    public String getClient() {
        return m_client;
    }
    
    /**
     * @return Service plus Level (Type) separated by a dot
     */
    public String getServiceName() {
        return m_serviceName;
    }
    
    /**
     * Sets the serviceName explicitly.
     * 
     * @param serviceName Name to set
     */
    public void setServiceName(String serviceName) {
        m_serviceName = serviceName;
    }

    /**
     * Returns the level of the measure.
     *
     * @return the measure level
     */
    public String getLevel() {
        return m_level;
    }
    
    /**
     * The level String representation.
     * @return The level String representation.
     */
    public String getShortLevel() {
        String level = this.getLevel();

        if (level.equals("CLIENT")) {
            level = "CL";
        } else if (level.equals("EJB_CONTAINER")) {
            level = "EJB";
        } 

        return level;
    }

    /**
     * Returns the EJB name of the measure.
     *
     * @return the measure EJB
     */
    public String getEjbName() {
        return m_service;
    }

    /**
     * Returns the method name of the measure.
     *
     * @return the measure method
     */
    public String getMethodName() {
        return m_method;
    }

    /**
     * Returns the start time of the measure (as long value).
     *
     * @return the measure start time
     */
    public long getStartTime() {
        return m_startTime;
    }

    /**
     * Returns the duration of the measure.
     *
     * @return the measure duration
     */
    public long getDuration() {
        return m_duration;
    }

    /**
     * The Hierarchy.
     * @return The Hierarchy.
     */
    public String getHierarchy() {
        return m_hierarchy;
    }

    /**
     * Returns a human-readable representation of the measure.
     *
     * @return measure representation
     */
    public String getFormattedString() {
        return m_id + ", " + m_seq + ", " + m_client + ", " + m_level + ", "
            + m_startTime + ", " + "(" 
            + ((m_service == null) ? "<undef>" : m_service)
            + ", " + ((m_method == null) ? "<undef>" : m_method) + "), "
            + m_duration;
    }

    /**
     * Returns a CSV (comma-separated value) representation of the measure.
     * 
     * @param delimiter
     *            the string to use as CSV delimiter
     * @return measure CSV representation
     */
    // synchronized due to dateformat
    public synchronized String getCsvString(String delimiter) {
        String dateSign = "_";
        String hierarchySign = "#";

        return m_id + delimiter + hierarchySign + m_hierarchy + delimiter
            + m_seq + delimiter + m_client + delimiter + m_level + delimiter
            + dateSign + DATE_FORMAT.format(new Date(m_startTime)) + delimiter
            + ((m_service == null) ? "" : m_service) + delimiter
            + ((m_method == null) ? "" : m_method) + delimiter + m_duration;
    }

    /**
     * Returns a human-readable representation of the measure.
     *
     * @return measure representation
     */
    public String toString() {
        return getFormattedString();
    }
}
