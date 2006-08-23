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

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

import ch.elca.el4j.services.dom.annotations.MemberOrder;


/**
 * A source of information.
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
    "hashValue",
    "description",
    "version",
    "incomplete",
    "whenInserted",
    "date",
    "keywords"
})
public class Reference {
    private String name;
    
    /** This reference's unique identifier; an ISBN for instance. */
    private String hashValue;
    private String description;
    private String version;
    
    /** Is only part of this reference available? */
    private boolean incomplete;
    
    /** the time this reference was entered into the database */
    private Timestamp whenInserted;
    
    /** When was this reference last changed? */
    private Date date;
    
    /** the keyword(s) that apply to this reference */
    private Set<Keyword> keywords;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHashValue() { return hashValue; }
    public void setHashValue(String hashValue) { this.hashValue = hashValue; }

    public String getDescription() { return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public boolean isIncomplete() { return incomplete; }
    public void setIncomplete(boolean incomplete) {
        this.incomplete = incomplete;
    }

    public Timestamp getWhenInserted() { return whenInserted; }
    public void setWhenInserted(Timestamp whenInserted) {
        this.whenInserted = whenInserted;
    }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Set<Keyword> getKeywords() { return keywords; }
    public void setKeywords(Set<Keyword> keywords) { this.keywords = keywords; }
}