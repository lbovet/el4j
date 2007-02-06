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
 * 
 * This class is ...
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
public class ServiceData {

    /**
     * Name of the service.
     */
    private String m_name;
    
    /**
     * Id of serivce (corresponds to measureId).
     */
    private int m_id;
    
    /**
     * Legacy field.
     */
    private long m_refTimeAbs;
    
    /**
     * Legacy field.
     */
    private int m_refTimeRel;
    
    
    /**
     * Constructor.
     * 
     * @param name Name of service.
     * @param id Id of service.
     */
    public ServiceData(String name, int id) {
        this.m_name = name;
        this.m_id = id;
        m_refTimeAbs = -1L;
        m_refTimeRel = -1;
    }

    /**
     * Getter for Id.
     * @return Id
     */
    public int getId() {
        return m_id;
    }

    /**
     * Setter of id.
     * @param id Id to set
     */
    public void setId(int id) {
        this.m_id = id;
    }

    /**
     * Getter for name.
     * @return Name
     */
    public String getName() {
        return m_name;
    }

    /**
     * Setter for name.
     * @param name Name to set
     */
    public void setName(String name) {
        this.m_name = name;
    }

    /**
     * Setter for referenceTime.
     * @param abs Absolute value
     * @param rel Relative value
     */
    public void setRefTime(long abs, int rel) {
        if (m_refTimeAbs == -1L) {
            m_refTimeAbs = abs;
            m_refTimeRel = rel;
        }
    }

    /**
     * Getter for absolute reference time.
     * @return absolute reference time
     */
    public long getRefTimeAbs() {
        return m_refTimeAbs;
    }

    /**
     * Getter for relative reference time.
     * @return relative reference time
     */
    public int getRefTimeRel() {
        return m_refTimeRel;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "Service: " + m_name + " / ID: " + m_id;
    }
}
