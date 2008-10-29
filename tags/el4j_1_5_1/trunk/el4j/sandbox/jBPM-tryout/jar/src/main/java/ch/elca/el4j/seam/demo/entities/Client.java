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
package ch.elca.el4j.seam.demo.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

/**
 * This class represents a client for the tender tracker demo.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Stefan Wismer (SWI)
 */
@Entity
public class Client implements Serializable {
    /**
     * The ID (for hibernate).
     */
    private Integer m_id;
    /**
     * The version (for optimistic locking).
     */
    private Integer m_version;

    /**
     * The name of the enterprise.
     */
    private String m_enterprise;
    
    /**
     * The activity of this client.
     */
    private String m_activity;
    
    /**
     * The address of this client.
     */
    private String m_address;

    @Id
    @GeneratedValue
    public Integer getId() {
        return m_id;
    }

    public void setId(Integer id) {
        m_id = id;
    }

    @Version
    public Integer getVersion() {
        return m_version;
    }

    private void setVersion(Integer version) {
        m_version = version;
    }

    @NotNull
    @Length(max = 32)
    public String getEnterprise() {
        return m_enterprise;
    }

    public void setEnterprise(String enterprise) {
        m_enterprise = enterprise;
    }

    @Length(max = 256)
    public String getActivity() {
        return m_activity;
    }

    public void setActivity(String activity) {
        m_activity = activity;
    }

    @Length(max = 64)
    public String getAddress() {
        return m_address;
    }

    public void setAddress(String address) {
        m_address = address;
    }

    public String toString() {
        return m_enterprise;
    }
}
