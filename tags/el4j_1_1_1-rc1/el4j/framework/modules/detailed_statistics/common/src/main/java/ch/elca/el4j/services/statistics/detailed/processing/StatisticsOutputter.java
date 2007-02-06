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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ch.elca.el4j.services.statistics.detailed.MeasureId;
import ch.elca.el4j.services.statistics.detailed.MeasureItem;
import ch.elca.el4j.util.codingsupport.Reject;

/**
 * This class provides methods for visualizing measurement data. This class was
 * ported from Leaf 2. Original authors: WHO,SHO. Leaf2 package name:
 * ch.elca.leaf.services.measuring 
 * 
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Rashid Waraich (RWA)
 * @author David Stefan (DST)
 */
public class StatisticsOutputter {

    /**
     * Logger.
     */
    private static Log s_logger 
        = LogFactory.getLog(StatisticsOutputter.class);
    
    /**
     * The SVG Diagram Evaluator.
     */
    private DataProcessor m_eval;

    /**
     * List of MeasureItems.
     */
    private List<MeasureItem> m_measures;


    /**
     * The constructor.
     * 
     * @param measures
     *            The measures.
     */
    public StatisticsOutputter(List<MeasureItem> measures) {
        m_measures = measures;
    }


    /**
     * Lists the loaded RequestIds.
     */
    public void listRequestIds() {
        List<MeasureId> mids = getRequestIds();

        StringBuffer buff = new StringBuffer();
        Iterator<MeasureId> iter = mids.iterator();

        int i = 0;
        while (iter.hasNext()) {
            i++;
            buff.append(iter.next());
            // Checkstyle: MagicNumber off
            if (i % 4 == 0) {
                buff.append("\n");
            } else {
                buff.append("   ");
            }
            // Checkstyle: MagicNumber on
        }
        s_logger.info(buff);
    }
    
    
    /**
     * Computes a SVG Graph and format it to a HTML compatible format.
     * 
     * @see convertTags
     * @param measureId
     *            Id of measurements that graph is computed
     * @return String containing the HTML compatible SVG Graph
     */
    public String getHTMLCompatibleSVGGraph(String measureId) {
        String svgGraph = createSVGGraph(measureId);
        return convertTags(svgGraph);
    }

    /**
     * Compute SVGGraph and write it to filename given.
     * 
     * @param filename
     *            Name of file to write
     * @param measureId
     *            Id of measurements that graph is computed
     */
    public void createSVGFile(String filename, String measureId) {
        Reject.ifNull(filename);
        String newFilename = filename;
        
        if (!newFilename.endsWith(".svg")) {
            newFilename = newFilename.concat(".svg");
        }
        String svgGraph = createSVGGraph(measureId);

        // Write svgGraph to file
        try {
            PrintStream out 
                = new PrintStream(new FileOutputStream(newFilename));
            out.println(svgGraph);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  
    }
    
    /**
     * Write CSV file with measures to filename given.
     * 
     * @param filename
     *            Name of file to write
     * @param measureId
     *            Id of measurements that graph is computed
     */
    public void createCVSFile(String filename, String measureId) {
        Reject.ifNull(filename);
        String newFilename = filename;
        
        if (!newFilename.endsWith(".txt")) {
            newFilename = newFilename.concat(".txt");
        }
        
//        List<MeasureItemEnhanced> formatedMeasures 
//            = formatToPerformanceViewerFormat(m_measures);
        
        try {
            PrintStream out 
                = new PrintStream(new FileOutputStream(newFilename));
            
            Iterator<MeasureItem> iter = m_measures.iterator();
            while (iter.hasNext()) {
                MeasureItem item = iter.next();
                if (item.getID().getFormattedString().equals(measureId)) {
                    out.println(item.getCsvString(","));
                }
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }    
    }
    
    /**
     * Take all the step to compute SVG graph and return it as String.
     * 
     * @param measureId
     *            Id of measurements that graph is computed
     * @return String containing the SVG Graph
     */
    private String createSVGGraph(String measureId) {
        Reject.ifNull(measureId);
        m_eval = new DataProcessor();
        try {
            return m_eval.getSVGGraph(m_measures, measureId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    

    /**
     * Gives back a list of the loaded measure ids.
     * 
     * @return the list of the ids.
     */
    private List<MeasureId> getRequestIds() {
        List<MeasureId> result = new ArrayList<MeasureId>();

        Iterator<MeasureItem> iter = m_measures.iterator();
        while (iter.hasNext()) {
            result.add(iter.next().getID());
        }
        return result;
    }

    /**
     * The 'convertTags' returns XML, which is embeddable in HTML. For this
     * reason, the XML tag symbols '<' and '>' are converted to '&lt;' resp.
     * '&gt;'.
     * 
     * @param input
     *            XML document.
     * @return HTML embeddable XML.
     */
    private String convertTags(String input) {
        String output = input;
        output = output.replaceAll("<", "&lt;");
        output = output.replaceAll(">", "&gt;");
        return output;
    }
}
