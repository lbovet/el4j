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
package ch.elca.el4j.services.statistics.detailed.svg;

/**
 * This class represents a method call. 
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author David Stefan (DST)
 */
public class MethodCall {
    
    
    /**
     * Type of call. This is a legacy field.
     */
    private static final String CALL_TYPE = "synchCall";
    
    /**
     * Name of MethodCall.
     */
    private String m_name;
    
    /**
     * Time of caller.
     */
    private int m_fromTime;
    
    /**
     * Time of callee.
     */
    private int m_toTime;
    
    /**
     * Callee service object.
     */
    private ServiceData m_toService;
    
    /**
     * Caller service object.
     */
    private ServiceData m_fromService;
    
    /**
     * Timestamp of callee.
     */
    private long m_toTimestamp;
    
    /**
     * Timestamp of caller.
     */
    private long m_fromTimestamp;
    
    /**
     * Constructor.
     * 
     * @param name Name of method call
     * @param fromService Caller service
     * @param toService Callee service
     * @param fromTime Time caller needed
     * @param toTime Time callee needed
     * @param fromTimestamp Timestamp of caller
     * @param toTimestamp Timestamp of callee
     */
    public MethodCall(String name, ServiceData fromService, 
        ServiceData toService, int fromTime, int toTime, long fromTimestamp, 
        long toTimestamp) {
        this.m_name = name;
        this.m_fromService = fromService;
        this.m_toService = toService;
        this.m_fromTime = fromTime;
        this.m_toTime = toTime;
        this.m_fromTimestamp = fromTimestamp;
        this.m_toTimestamp = toTimestamp;
    }

    /**
     * Getter for name.
     * @return Name of method call
     */
    public String getName() {
        return m_name;
    }

    /**
     * Getter for call type.
     * @return the call type
     */
    public String getCallType() {
        return CALL_TYPE;
    }

    /**
     * Getter for fromService.
     * @return fromService
     */
    public ServiceData getFromService() {
        return m_fromService;
    }

    /**
     * Getter for toService.
     * @return toService
     */
    public ServiceData getToService() {
        return m_toService;
    }

    /**
     * Getter for fromTime.
     * @return fromTime
     */
    public int getFromTime() {
        return m_fromTime;
    }

    /**
     * Getter for toTime.
     * @return toTime
     */
    public int getToTime() {
        return m_toTime;
    }

    /**
     * First gestter for Time difference.
     * This is legacy code.
     * @return Time difference
     */
    public int getTimeDiff1() {
        return m_fromTime - m_toTime - (int) (m_fromTimestamp - m_toTimestamp);
    }

    /**
     * Second getter for Time difference. 
     * This is legacy code.
     * @return Time difference
     */
    public int getTimeDiff2() {
        return (int) (m_fromTimestamp - m_toTimestamp);
    }

    /**
     * Getter for fromTimestamp.
     * @return fromTimestamp
     */
    public long getFromTimestamp() {
        return m_fromTimestamp;
    }

    /**
     * Getter for toTimestamp.
     * @return toTimestamp
     */
    public long getToTimestamp() {
        return m_toTimestamp;
    }   
}