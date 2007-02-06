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

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * This class is a representation for the call hierarchy.
 * It was taken and cleaned up from a leaf class.
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
public class CallHierarchy {

    /**
     * Seperator for Tokenizer.
     */
    private static final String SEPARATOR = "-";
    /**
     * Internal represenation of hierarchy.
     */
    private int[] m_counter;

    /**
     * Constructor. 
     * @param hierarchy The hierarchy this CallContext should represent
     */
    public CallHierarchy(String hierarchy) {
        List<Integer> level = new LinkedList<Integer>();
        
        StringTokenizer tokens = new StringTokenizer(hierarchy, SEPARATOR);
        while (tokens.hasMoreTokens()) {
            level.add(new Integer(tokens.nextToken()));
        }
        m_counter = new int[level.size()];
        for (int i = 0; i < level.size(); i++) {
            m_counter[i] = level.get(i);
        }
    }

    /**
     * Calculate if this call is a parent.
     * 
     * @return if this call is a parent
     */
    public boolean isParent() {
        return m_counter[m_counter.length - 1] == 0;
    }

    /**
     * Calculate if this call is a children of 'other'.
     * 
     * @param other Other call
     * @return if this call is a children of 'other'
     */
    public boolean isChild(CallHierarchy other) {
        return !equals(other) && m_counter.length == other.m_counter.length;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String toString() {
        String result = "" + m_counter[0];
        for (int i = 1; i < m_counter.length; i++) {
            result = result + "-" + m_counter[i];
        }
        return result;
    }

    /**
     * Calculate if this call was before 'other'.
     * 
     * @param other Other call
     * @return if this call was before 'other'
     */
    public boolean before(CallHierarchy other) {
        int result = 0;
        int i;
        for (i = 0; i < m_counter.length; i++) {
            if (i >= other.m_counter.length) {
                result = 1;
                break;
            }
            if (m_counter[i] < other.m_counter[i]) {
                result = -1;
                break;
            }
            if (m_counter[i] <= other.m_counter[i]) {
                continue;
            }
                
            result = 1;
            break;
        }

        if (result == 0 && i < other.m_counter.length) {
            result = -1;
        } 
        return result == -1;
    }

    /**
     * Get depth of hierarchy.
     * @return Depth of hierarchy
     */
    public int getDepth() {
        return m_counter.length;
    }
    
    /**
     * Get a certain level of the hierarchy.
     * 
     * @param i Desired level
     * @return Value of level
     */
    public int getLevel(int i) {
        return m_counter[i];
    }

  
}