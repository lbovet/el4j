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
package ch.elca.el4j.services.statistics.detailed.processing;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.elca.el4j.services.statistics.detailed.MeasureItem;
import ch.elca.el4j.services.statistics.detailed.svg.CallHierarchy;
import ch.elca.el4j.services.statistics.detailed.svg.MethodCall;
import ch.elca.el4j.services.statistics.detailed.svg.SVGDiagramCreator;
import ch.elca.el4j.services.statistics.detailed.svg.ServiceData;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class assembles and formats the data for the SVGGraphCreator. This class
 * was refactored from the ch.elca.util.BaUt_CommunicationBusStatisticsEvaluator
 * legacy class and reduced to the functionality needed in this context.
 * However, it still uses other classes of the performanceViewer library.
 * <script type="text/javascript">printFileStatus ("$URL$", "$Revision$",
 * "$Date$", "$Author$" );</script>
 * 
 * @author David Stefan (DST)
 */
public class DataProcessor {

    /**
     * Zoomfactor of graph.
     */
    private int m_zoomFactor;

    /**
     * Time-ordered entries per requestId.
     */
    private Map<String, List<MeasureItem>> m_perRequestId;

    
    /**
     * Map of all ServiceData objects.
     */ 
    private Map<String, ServiceData> m_servicesObjects;
    
    /**
     * List of trace level entries.
     */
    private List<MeasureItem> m_traceLevelEntries; 
    
    
    /**
     * Constructor.
     */
    public DataProcessor() {
        m_perRequestId = new Hashtable<String, List<MeasureItem>>();
        m_servicesObjects = new Hashtable<String, ServiceData>();
        m_traceLevelEntries = new ArrayList<MeasureItem>();
        
        m_zoomFactor = 2;
    }
    
    /**
     * Getter for zoomFactor.
     * 
     * @return zoomFactor
     */
    public int getZoomFactor() {
        return m_zoomFactor;
    }

    /**
     * Setter for zoomFactor.
     * 
     * @param zoomFactor To set
     */
    public void setZoomFactor(int zoomFactor) {
        m_zoomFactor = zoomFactor;
    }

    /**
     * Compute and return an SVG Graph for the given data and measureId.
     * 
     * @param data List of all MeasureItems
     * @param measureId MeasureId we want graph for
     * @return SVG Graph as string
     */
    public String getSVGGraph(List<MeasureItem> data, String measureId)
        throws Exception {
        List<MeasureItem> enriched = enrichData(data);
        List<MethodCall> calls = processData(enriched, measureId);
        return createGraph(calls, measureId);
    }

    /**
     * Process data to format we use in this class.
     * 
     * @param data Data to be sorted
     * @param measureId MeasureId we generate graph for
     * @return List of MethodCalls
     * @throws Exception
     */
    private List<MethodCall> processData(List<MeasureItem> data, 
        String measureId) throws Exception {
        
        // Add data to m_perRequestId
        Iterator<MeasureItem> iter = data.iterator();
        while (iter.hasNext()) {
            MeasureItem item = iter.next();
            String key = item.getID().getFormattedString();
            List<MeasureItem> list = m_perRequestId.get(key);
            if (list == null) {
                list = new ArrayList<MeasureItem>();
                m_perRequestId.put(key, list);
            }
            addEntryToList(list, item);
        }
        
        // Get all MeasureItems for the given Id and scan for services
        List<MeasureItem> entriesForId = m_perRequestId.get(measureId);
        findAllServices(entriesForId);
        
        // Create and return List of all MethodCalls
        return buildMethodCalls();
    }
    
    /**
     * Create SVG Graph.
     * 
     * @param calls List of all methodCalls
     * @param measureId MeasureId to build graph for
     * @return SVG Graph as string
     */
    private String createGraph(List<MethodCall> calls, String measureId) {
        Reject.ifFalse(m_perRequestId.containsKey(measureId));
        
        // Calculate Screen size
        // Constants for screen size taken from legacy code.
        // Checkstyle: MagicNumber off
        int xScreenSize = (m_servicesObjects.size() + 6) * 100;
        int yScreenSize = calculateYScreenSize(measureId, calls.get(0));
        // Checkstyle: MagicNumber on

        // Generate Diagrams
        SVGDiagramCreator sc = new SVGDiagramCreator(measureId, xScreenSize,
            yScreenSize, m_zoomFactor);
        return sc.createGraph(m_servicesObjects.values(), calls, 
            yScreenSize);        
    }
      
    /**
     * Adds the entry to the List such that the List stays sorted. Sorting is
     * ascending according to context.
     * 
     * @see prior()
     * @param set
     *            The set where entry should be added
     * @param item
     *            The entry to add
     */
    private void addEntryToList(List<MeasureItem> set, 
           MeasureItem item) {
        int low = 0;
        int high = set.size();
        int mid = 0;

        while (low < high) {
            mid = (low + high) / 2;

            MeasureItem current = set.get(mid);
            
            if (item.getHierarchy().before(current.getHierarchy())) {
                high = mid;
            } else {
                low = mid + 1;
                mid = mid + 1;
            }
        }
        set.add(mid, item);
    }
     
     
     /**
         * Go through all measureItems and find all related services and add
         * them to the services map.
         * 
         * @param allEntries
         *            List to scan for services
         */
    private void findAllServices(List<MeasureItem> allEntries) {
        ServiceData serviceData;
        int serviceId = 0;
        // Add client to services. Must be
        // done by hand because it is not in the stats file.
        serviceData = new ServiceData("Client", serviceId);
        m_servicesObjects.put("Client", serviceData);
        serviceId++;

        // Scan all entries and find all other services
        // and add them to the services map
        for (MeasureItem elem : allEntries) {
            if (m_servicesObjects.get(elem.getServiceName()) == null) {
                serviceData = new ServiceData(elem.getServiceName(), serviceId);
                m_servicesObjects.put(elem.getServiceName(), serviceData);
                serviceId++;
            }
            m_traceLevelEntries.add(elem);
        }
    }
    
    
    /**
     * Generates a list holding all MethodCall objects.
     * 
     * @return a list of all MethodCall objects
     */
    private List<MethodCall> buildMethodCalls() {

        List<MethodCall> calls = new ArrayList<MethodCall>();

        // Entries: e1 = calling side, e2 = called side, previous = a call
        // entering the service from where this call was made.
        
        MeasureItem e1;
        MeasureItem e2;
        MeasureItem previous;

        int i = 0;
        // to loop through the entries
        int step = 2;

        while (i < m_traceLevelEntries.size()) {
            // Find the two corresponding entries ...
            e1 = m_traceLevelEntries.get(i);
            if (!e1.getHierarchy().isParent() && (i + 1) 
                    < m_traceLevelEntries.size()) {
                e2 = m_traceLevelEntries.get(i + 1);
                step = 2;
                // no second entry for this call
                if (!e2.getHierarchy().isParent()) {
                    // use twice the same object should work
                    e2 = e1;
                    step = 1;
                }
            } else {
                e2 = e1;
                step = 1;
            }
            
            // Search a previous entry calling the service from which this
            // call was made. Used to find the fromService!
            previous = findPreviousEntry(m_traceLevelEntries, e1, i - 1);
            calls.add(createMethodCall(e1, e2, previous));
            i += step;
        }
        return calls;
    }
    
    /**
     * Create a new MethodCall object and fill it with data from the given 
     * MeasureItems.
     * 
     * @param e1 MeasureItem of Caller
     * @param e2 MeasureItem of Callee
     * @param previous Previous caller
     * @return A new MethodCall object for the given items
     */
    private MethodCall createMethodCall(MeasureItem e1, 
            MeasureItem e2, MeasureItem previous) {
        
        ServiceData fromService;
        ServiceData toService;
        int fromTime;
        int toTime;
        
        // Prepare the data ...
        String name = e1.getMethodName();
        
        // If no previous found ... caller was client.
        if (previous == null) {
            fromService = m_servicesObjects.get("Client");
        } else {
            fromService = m_servicesObjects.get(previous.getServiceName());
        }
        toService = m_servicesObjects.get(e1.getServiceName());

        // Set the times
        fromTime = (int) e1.getDuration();

        if (e1 == e2) {
            toTime = 0;
        } else {
            toTime = (int) e2.getDuration();
        }

        // Get the timestamps
        long thisTimestamp = createTimeStamp(e1).getTime();
        long otherTimestamp = createTimeStamp(e2).getTime();

        return new MethodCall(name, fromService, toService,
            fromTime, toTime, thisTimestamp, otherTimestamp); 
    }
    
    /**
     * Find an other call entry calling a method on the service from which call
     * e was made. From the found rntry it is possible to find
     * the name of the fromService of entry e.
     * 
     * @param entries
     *            a <code>List</code> holding all entries of the request
     * @param item
     *            the <code>Entry</code> of which we are searching a previous
     *            entry
     * @param start
     *            indicates where to start searching in the entries List.
     * @return an entry which is a previous call of <code>Entry</code> e
     */
    private MeasureItem findPreviousEntry(List<MeasureItem> 
        entries, MeasureItem item, int start) {
        
        MeasureItem previous = null;
        MeasureItem tmp;
  
        CallHierarchy thisCtx = item.getHierarchy();
        CallHierarchy otherCtx;
        boolean isPrevious = true;

        // Scan back in entries list
        for (int i = start; i > 0; i--) {
            isPrevious = true;
            tmp = entries.get(i);
            otherCtx = tmp.getHierarchy();

            // If length of both contexts are equal it might be
            // possible that it could be a previous entry
            if (otherCtx.getDepth() == thisCtx.getDepth()) {
                int last = otherCtx.getDepth() - 1;

                // Check if it is an entry on the called side
                if (otherCtx.getLevel(last) == 0) {
                    // Look if it is a previous call. Only valid if
                    // all numbers are equal except the last digit.
                    for (int j = 0; j < last; j++) {
                        if (otherCtx.getLevel(j) != thisCtx.getLevel(j)) {
                            isPrevious = false;
                            break;
                        }
                    }
                    // last position is not a 0
                } else {
                    isPrevious = false;
                }
                if (isPrevious) {
                    previous = tmp;

                    break;
                }
            }
        }

        return previous;
    }

    /**
     * Calculate vertical size of screen, which depend on how long the method
     * call were.
     * Constants taken from legacy code.
     * 
     * @param measureId MeasureId we build graph for
     * @param firstCall
     *            First method call
     * @return vertical screen size
     */
    private int calculateYScreenSize(String measureId, MethodCall firstCall) {
        List<MeasureItem> entriesForId = m_perRequestId.get(measureId);
        
        long mintimestamp = firstCall.getFromTimestamp() 
            - firstCall.getFromTime();
        long maxtimestamp = 0;

        for (int i = 0; i < entriesForId.size(); i++) {
            long timestamp = createTimeStamp(entriesForId.get(i)).getTime();

            if (timestamp > maxtimestamp) {
                maxtimestamp = timestamp;
            } else if (timestamp < mintimestamp) {
                mintimestamp = timestamp;
            }
        }
        // Checkstyle: MagicNumber off
        return (int) (maxtimestamp - mintimestamp) + 200;
        // Checkstyle: MagicNumber on
    }
    
    /**
     * Create the timestap that was part of the BaUt_Entry class used earlier.
     * @param item MeasureItem to create timestamp for
     * @return The timestamp
     */
    private Date createTimeStamp(MeasureItem item) {
        Date date = new Date(item.getStartTime());
        date.setTime(date.getTime() + item.getDuration());
        return date;
    }

    /**
     * This method is legacy code and should be removed.
     * 
     * It serves two purposes:
     * 1) split succeeding method calls to a certain class to method calls 
     *    of many classes (described in detail in the method body)
     * 2) Add a measure item for the return call. 
     * 
     * However, removing either of these two 'enrichments' ends in a 
     * buggy graph. That's why this method is still here.
     * 
     * @param measures The measures to enrich
     * @return The formatted measures.
     */
    private List<MeasureItem> enrichData(List<MeasureItem> measures) {

        List<MeasureItem> formatedMeasures 
            = new ArrayList<MeasureItem>();

        String serviceName;
        String previousServiceName = "";
        String previousOperation = "";
        String lastMeasureId = "";

        int previousServiceNameCnt = 0;

        Iterator<MeasureItem> iter = measures.iterator();

        while (iter.hasNext()) {
            MeasureItem measure = iter.next();

            // new measure id
            if (!lastMeasureId.equals(measure.getID().getFormattedString())
                && !lastMeasureId.equals("")) {
                previousServiceName = "";
                previousOperation = "";
            }

            // other operation on same service
            if (!previousOperation.equals(measure.getMethodName())) {
                previousServiceNameCnt = -1;
            }

            previousOperation = measure.getMethodName();

            // calculate previous service name (because of same name on proxy
            // and preremote invoker) resp. postremote and object invoker
            serviceName = measure.getEjbName() + "." + measure.getShortLevel();

            if (serviceName.equals(previousServiceName)) {
                previousServiceNameCnt++;

                /*
                 * This inconspicuous little loop shows one class as many in a
                 * SVG graph. 
                 * 
                 * E.g. in Detailed Statistics Demo, DemoC Class: 
                 * 
                 * The method "print" is called n times from Class DemoB and 
                 * this loop let DemoC appear as n different classes in the 
                 * diagram. If removed, however, the diagram is not shown 
                 * correctly anymore.
                 */
                for (int i = 0; i < previousServiceNameCnt; i++) {
                    serviceName = serviceName + "'";
                }
            } else {
                previousServiceNameCnt = 0;
            }

            previousServiceName = measure.getEjbName() + "."
                + measure.getShortLevel();

            lastMeasureId = measure.getID().getFormattedString();

            // int[] hierarchy = measure.getHierarchy();
            addToEnrichedMeasures(formatedMeasures, serviceName, measure);
        }
        return formatedMeasures;
    }
    
    /**
     * This method is a helper method for 'enrichData' and does the second
     * purpose described there.
     * 
     * @param enrichedMeasures
     *            List of enriched measures.
     * @param serviceName
     *            serviceName of measureItem.
     * @param measure
     *            MeasureItem to enrich.
     */
    private void addToEnrichedMeasures(List<MeasureItem> enrichedMeasures, 
        String serviceName, MeasureItem measure) {
        MeasureItem measureItemEnhanced;
        
        measureItemEnhanced = new MeasureItem(measure.getID(), measure
            .getSequence(), measure.getClient(), measure.getLevel(), measure
            .getEjbName(), measure.getMethodName(), measure.getStartTime(),
            measure.getDuration(), measure.getHierarchy().toString());
        
        measureItemEnhanced.setServiceName(serviceName);
        
        enrichedMeasures.add(measureItemEnhanced);        

        // copy array for return record (rr)
        String hierarchyRR = measure.getHierarchy() + "-0";
        
        measureItemEnhanced = new MeasureItem(measure.getID(), measure
            .getSequence(), measure.getClient(), measure.getLevel(), measure
            .getEjbName(), measure.getMethodName(), measure.getStartTime(),
            measure.getDuration(), hierarchyRR);
        
        measureItemEnhanced.setServiceName(serviceName);

        enrichedMeasures.add(measureItemEnhanced);
    }
    
}
