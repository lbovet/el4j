/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * This program is published under the GNU General Public License (GPL) license.
 * http://www.gnu.org/licenses/gpl.txt
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.search.criterias;


/**
 * 
 * Criteria for the include pattern.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Alex Mathey (AMA)
 */
public class IncludeCriteria extends AbstractCriteria {

    /**
     * Default constructor for remoting protocols like hessian and burlap added.
     */
    protected IncludeCriteria() { }
    
    /**
     * Constructor.
     * 
     * @param field
     *            Is the field the criteria is made for.
     * @param value
     *            Is the value of this criteria.
     */
    public IncludeCriteria(String field, Object value) {
        super(field, value);
    }
        
    /**
     * {@inheritDoc}
     */
    public String getType() {
        return "include";
    }

}
