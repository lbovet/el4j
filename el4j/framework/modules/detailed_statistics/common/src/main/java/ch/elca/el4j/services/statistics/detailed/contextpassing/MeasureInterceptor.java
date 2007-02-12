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

import java.net.InetAddress;
import java.net.UnknownHostException;

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
    private static ThreadLocal<int[]> s_hierarchy = new ThreadLocal<int[]>();
    
    protected static String s_hostName = "<unknown>";   
    {
        try {
            s_hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
        }
    }
        
    
    /**
     * See comment on s_hierarcy.
     */
    private static ThreadLocal<MeasureId> s_id = new ThreadLocal<MeasureId>();
    
    /**
     * Holds the depth of the current call stack. It's incremented when a call
     *  arrives inbound, it's decremented when the variable arrives outbound.
     */
    private static ThreadLocal<Integer> s_depth = new ThreadLocal<Integer>();
    
    
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
     * @deprecated use the constructor with (MeasureCollectorService,String) instead
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
     * The constructor.
     * @param collectorService The collectorService, where the measured data 
     * should be stored to.
     * 
     * @param isServer Is this a server (client=false).
     */
    public MeasureInterceptor(MeasureCollectorService collectorService, String vmName) {
        m_collectorService = collectorService;
        m_level = vmName;
    }    
    
    /**
     * {@inheritDoc}
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        
        // we will work on the passed object directly (it is stored in a
        //  threadlocal in the DetailedStatisticsSharedContextHolder)
        DetailedStatisticsContext context = DetailedStatisticsSharedContextHolder.getContext();

        int seqNumber;
        int[] hierarchy;
        
        // Indicates that the ID was newly created in this currently running invoke method.
        //  At the end of this currently running invoke method, we should drop it again.
        boolean dropIdAtEnd = false;

        
        // depth++
        context.setDepth(context.getDepth()+1); 
        seqNumber = context.getSequenceNumber();
        seqNumber++;

        System.out.println(methodInvocation);
        System.out.println("depth:"+context.getDepth());
        
        
        /* ---------------------------------------------------------------------
         * If no mesure id is available this is the first measure and the  
         * interceptor has to generate a new MeasureId
         * -------------------------------------------------------------------*/
        if (context.getMeasureId() == null) {

            // The id does not exist : client side, or first call
            // of the invoker from service side
            context.setMeasureId(MeasureId.createID());
            hierarchy = new int[]{1};
            
            dropIdAtEnd = true;

        } else {
            /* ----------------------------------------------------------------
             * A MeasureId was found in the shared context    
             * This means an additional call in the existing measuring chain.
             * ---------------------------------------------------------------*/

            hierarchy = context.getHierarchy();

            assert (context.getDepth() <= hierarchy.length);  // after previous depth incremental!          
            
            System.out.println("*hier:"+hierarchy.length+" "+context.getDepth());            
            
            // extend hierarchy-array if needed
            if ((hierarchy.length+1) == context.getDepth()) {
                int[] extendedHierarchy = new int[hierarchy.length+1];
                for (int i = 0; i < hierarchy.length; i++) {
                    extendedHierarchy[i] = hierarchy[i];
                }
                extendedHierarchy[hierarchy.length] = 0;

                hierarchy = extendedHierarchy;
            }
                
            System.out.println("hier:"+hierarchy.length+" "+context.getDepth());
            hierarchy[context.getDepth()-1] = hierarchy[context.getDepth()-1] + 1;
        }


 
        // Perform invocation and measure time
        long startTime = System.currentTimeMillis();

        long lastStartTime = context.getStartTime();

        // Correction of unsynchronized clocks.
        if (startTime < lastStartTime) {
            startTime = lastStartTime;
        }

        // Put the values in shared context
        context.setStartTime(startTime);       
        context.setSequenceNumber(seqNumber);
        context.setHierarchy(hierarchy);      

        Object retVal = null;
        try {
            // the invocation
            retVal = methodInvocation.proceed();
        } catch (Throwable t) {
            throw t;
        }
        finally {       
            // Calculate duration after invocation
            long duration = System.currentTimeMillis() - startTime;

            /*
             * ---------------------------------------------------------------------
             * Create MeasureItem and pass it to the CollectorService that
             * stores is
             * -------------------------------------------------------------------
             */
            if (m_collectorService != null) {
                MeasureItem tempMeasure = new MeasureItem(context
                        .getMeasureId(), seqNumber, s_hostName, m_level,
                        methodInvocation.getThis().getClass().getName(),
                        methodInvocation.getMethod().getName(), startTime,
                        duration, arrayToString(hierarchy, context.getDepth()));
                m_collectorService.add(tempMeasure);

            } else {
                s_logger
                        .info("invoke, Unable to write measure because "
                                + "MeasureCollectorService is not available. "
                                + "The measure is ignored");
            }

            // depth--
            context.setDepth(context.getDepth() - 1);

            if (dropIdAtEnd) {
                // start with a fresh context
                DetailedStatisticsSharedContextHolder
                        .setContext(new DetailedStatisticsContext());
            }       

        }
        return retVal;
    }

    /**
     * Print the hierarchy array as String with "-" between every array entry. 
     * @param array Every array entry must provide a toString() method
     * @return A string representation of the array. (E.g. "1-2-1")
     */
    private String arrayToString(int[] array, int depth) {
        if (array == null) {
            return "null";
        }

        StringBuffer str = new StringBuffer();
        for (int j = 0; j < depth; j++) {
            str.append(array[j]);

            if ((j + 1) != depth) {
                str.append("-");
            }
        }

        return str.toString();
    }
}
