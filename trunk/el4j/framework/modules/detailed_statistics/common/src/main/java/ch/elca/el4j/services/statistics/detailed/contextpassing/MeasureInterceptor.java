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

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.statistics.detailed.MeasureId;
import ch.elca.el4j.services.statistics.detailed.MeasureItem;
import ch.elca.el4j.services.statistics.detailed.processing.MeasureCollectorService;

/**
 * Invoker for measuring the time of calls to a EL4J service.
 * <p>
 * This invoker is not limited to a position. It may be used
 * as Stub invoker or Proxy invoker.
 * <p>
 * Its configuration defines :
 * <ul>
 * <li>the associated collector service name with <code>collector</code> entry
 * </li>
 * <li>the level to publish in MeasureItem with <code>level</code> entry
 * </li>
 * </ul>
 *
 * This class was ported from Leaf 2.
 * Original authors: WHO, DBA. 
 * Leaf2 package name: ch.elca.leaf.services.measuring 
 *
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
public class MeasureInterceptor  implements MethodInterceptor {
    /**
     * Private logger.
     */
    private static Log s_logger = LogFactory.getLog(MeasureInterceptor.class);
    
    /** ThreadLocal variables to store the hierarchy locally in the thread.
     *  This variables are used if a service is called more than once in the 
     *  same transaction. In such a case, the hierarchy is built more wide.
     *  (e.g. [1-1] to [1-2])
     *  further calls make the hierarchy more deep (e.g. [1-1] to [1-1-1]) 
     */
    private static ThreadLocal s_hierarchy = new ThreadLocal();
    /**
     * See comment on s_hierarcy.
     */
    private static ThreadLocal s_id = new ThreadLocal();
    /**
     * ThreadLocal variables to store the measureId locally in the thread.
     */
    private MeasureCollectorService m_collectorService = null;

    /** Level for MeasureItem. */
    private String m_level = null;

    /**
     * The constructor.
     * @param collectorService The collectorService, where the measured data 
     * should be stored to.
     * 
     * @param isServer Is this a server (client=false).
     */
    public MeasureInterceptor(MeasureCollectorService collectorService,
        boolean isServer) {
        this.m_collectorService = collectorService;
        if (isServer) {
            this.m_level = "EJB_CONTAINER";
        } else {
            this.m_level = "CLIENT";
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {      
        PassedContext passedObject = SharedContext.getPassedObject();
        MeasureId id;
        int seqNumber;
        int[] hierarchy;
        boolean noId = false;

        // Get current Id "MEASURE_ID"
        id = passedObject.getMeasureId();

        // Get previous startTime
        Long oldStartTime = new Long(passedObject.getStartTime());

        // No starttime in context
        if (oldStartTime == null) {
            oldStartTime = new Long(0);
        }

        
        /* ---------------------------------------------------------------------
         * If no id is available this is the first measure and the Invoker has 
         * to generate a MeasureId
         * -------------------------------------------------------------------*/
        if (id == null) {

            // The id does not exist : client side, or first call
            // of the invoker from service side
            id = MeasureId.createID();
            seqNumber = 1;

            hierarchy = new int[] {1};
            
            passedObject.setMeasureId(id);
            noId = true;

            /* ----------------------------------------------------------------
             * A MeasureId was found in the shared context with context level
             *  ACTION   
             * This means an additional call in the whole measuring chain.
             * ---------------------------------------------------------------*/
        } else {

            // get seq from calling svc increment it by one
            seqNumber = passedObject.getSequenceNumber();

            seqNumber++;

            // Get hierarchy from previous service
            // if ThreadLocal Variable has something different to null
            // then this is an additional measure.
            int[] previousHierarchy = null;
            int[] threadLocalHierarchy = (int[]) s_hierarchy.get();
            MeasureId threadLocalId = (MeasureId) s_id.get();

            if (threadLocalHierarchy != null && threadLocalId != null) {
                // test if it's still the same measure id, 
                // else it's a new measure
                if (threadLocalId.getFormattedString().equals(
                    id.getFormattedString())) {
                    previousHierarchy = threadLocalHierarchy;
                }
            }

            // normal case, no additional call. take hierachy from context
            if (previousHierarchy == null) {
                previousHierarchy = passedObject.getHierarchy();

            }

            // distinguish if call is more wide or more deep
            if (previousHierarchy.length < seqNumber) {
                // more deep e.g. from 1-1 to 1-1-1
                hierarchy = new int[seqNumber];

                System.arraycopy(previousHierarchy, 0, hierarchy, 0,
                    previousHierarchy.length);
                hierarchy[hierarchy.length - 1] = 1;

            } else if (previousHierarchy.length >= seqNumber) {
                // more wide e.g. from 1-1 to 1-2
                hierarchy = new int[seqNumber];

                for (int i = 0; i < seqNumber - 1; i++) {
                    hierarchy[i] = previousHierarchy[i];
                }

                hierarchy[seqNumber - 1] = previousHierarchy[seqNumber - 1] + 1;

            } else {
                // should not happen
                hierarchy = null;

            }

        }

        // put sequence into context with context level ACTION
        passedObject.setSequenceNumber(seqNumber);

        // put hierarchy into context with context level ACTION
        // This refactoring was done because the SESSION context 
        // level (of the shared context service) was removed in the cejb.
        // The new solution uses ThreadLocal variables.
        passedObject.setHierarchy(hierarchy);

        s_hierarchy.set(hierarchy);
        s_id.set(id);

        // Perform invocation and measure time
        long startTime = System.currentTimeMillis();

        long lastStartTime = oldStartTime.longValue();

        // Correction of unsynchronized clocks.
        if (startTime < lastStartTime) {
            startTime = lastStartTime;
        }

        // Put the starttime in shared context
        passedObject.setStartTime(startTime);

        /* ---------------------------------------------------------------------
         * The invokation */

        Object retVal = methodInvocation.proceed();

        /* ---------------------------------------------------------------------
         */

        // Calculate duration after invocation
        long duration = System.currentTimeMillis() - startTime;

        /* ---------------------------------------------------------------------
         * Create MeasureItem and put duration into this item. Then pass the 
         * item to the CollectorService which will add the item to persistence
         * -------------------------------------------------------------------*/

        // Add new measure
        if (m_collectorService != null) {
            MeasureItem tempMeasure = new MeasureItem(
                id, seqNumber, id.getHost(), m_level,
                methodInvocation.getThis().getClass().getName(),
                methodInvocation.getMethod().getName(), startTime, duration,
                arrayToString(hierarchy));
            m_collectorService.add(tempMeasure);

        } else {
            s_logger.info("invoke, Unable to write measure because "
                + "MeasureCollectorService is not available. The measure"
                + "is ignored");
        }

        if (noId) {
            // Erase id in shared context
            passedObject.setMeasureId(null);
        }

        return retVal;
    }

    /**
     * Print the hierarchy array as String with "-" between every array entry. 
     * @param array Every array entry must provide a toString() method
     * @return A string representation of the array. (E.g. "1-2-1")
     */
    private String arrayToString(int[] array) {
        if (array == null) {
            return "null";
        }

        StringBuffer str = new StringBuffer();

        for (int j = 0; j < array.length; j++) {
            str.append(array[j]);

            if ((j + 1) != array.length) {
                str.append("-");
            }
        }

        return str.toString();
    }
}
