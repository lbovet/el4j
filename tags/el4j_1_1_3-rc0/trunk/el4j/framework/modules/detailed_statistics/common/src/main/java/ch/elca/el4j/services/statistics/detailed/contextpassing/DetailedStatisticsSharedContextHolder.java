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

/**
 * 
 * This class is specific implementation of the generic shared context 
 * of Leaf 2 for the detailed statistics service. The shared context is 
 * held in a static ThreadLocal variable.
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
public class DetailedStatisticsSharedContextHolder {
    
    /**
     * Internal holder of thread static variable.
     */
    private static final ThreadLocal<DetailedStatisticsContext> 
    DETAILED_STATISTICS_CONTEXT 
        = new ThreadLocal<DetailedStatisticsContext>() {
                @Override
                protected DetailedStatisticsContext initialValue() {
                    return new DetailedStatisticsContext();
                } 
            }; 
    
    /**
     * Hide default constructor as this is a Utility class.
     *
     */
    protected DetailedStatisticsSharedContextHolder() { }
    
    
    /**
     * Get the passed object.
     * 
     * @return DetailedStatisticsContext.
     */
    public static final DetailedStatisticsContext getContext() {
        return DETAILED_STATISTICS_CONTEXT.get();
    }
   
    /**
     * Set the passed object.
     * @param passedObject The passed object.
     */
    public static final void setContext(
        DetailedStatisticsContext passedObject) {
        DETAILED_STATISTICS_CONTEXT.set(passedObject);
    }
    
}