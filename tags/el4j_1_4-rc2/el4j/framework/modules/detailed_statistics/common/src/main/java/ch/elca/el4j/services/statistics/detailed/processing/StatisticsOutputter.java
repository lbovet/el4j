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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zanthan.sequence.Fasade;

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
 * @author David Stefan (DST)
 */
public class StatisticsOutputter {
    
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
     * Compute SVGGraph and write it to filename given.
     * 
     * @param filename
     *            Name of file to write
     * @param measureId
     *            Id of measurements that graph is computed
     * @param width 
     *            Width of the diagram
     * @param height
     *            Height of the diagram
     */
    public void createDiagFile(String filename, String measureId, 
        int width, int height) {
        Reject.ifNull(filename);
        String newFilename = filename;
        
        // Check for existing file ending
        if (!newFilename.endsWith(".png")) {
            newFilename = newFilename.concat(".png");
        }
        
        Fasade fasade = new Fasade();
        // Check if width and height were set, else use defaults 
        if (width == 0 || height == 0) {
            fasade.createSequenceDiagram(convertData(measureId), newFilename);
        } else {
            fasade.createSequenceDiagram(convertData(measureId), newFilename, 
                width, height);
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
     * Converts the data into the format needed by the library that generates
     * the diagram.
     * 
     * @param id
     *            The MeasureId we want to generate a diagram for
     * @return String representation of the graph
     */
    private String convertData(String id) {
        Map<Integer, MeasureItem> myMap = new HashMap<Integer, MeasureItem>();
        int i = 1;
        MeasureItem elem;
        String hierarchy = "";
        String oldHierarchy = "";
        String res = "";
        
        // Get the measure items with our id
        for (MeasureItem m : m_measures) {
            if (id.equalsIgnoreCase(m.getID().toString())) {
                myMap.put(m.getSequence(), m);
            }
        }
        // Check if there is a measure item with the sequence no 1
        if (myMap.get(i) == null) {
            throw new RuntimeException("Measurement are messed up");
        }
        
        // Convert the graph. Goes through all measure items and adds them to 
        // the string representation by distinguishing the hierarchical level. 
        while ((elem = myMap.get(i)) != null) {
            hierarchy = elem.getHierarchy();
            // Check if hierarchy increased. If so, open a new bracket
            if (hierarchy.length() > oldHierarchy.length()) {
                res = res + printCall(elem);
            // Check if hierarchy is the same. If so, close the bracket and open
            // another one.
            } else if (hierarchy.length() == oldHierarchy.length()) {
                res = res + ")" + printCall(elem);
            // Check if hierarchy has decreased. If so, close brackets.
            } else if (hierarchy.length() < oldHierarchy.length()) {
                while (hierarchy.length() != oldHierarchy.length()) {
                    res = res + ") ";
                    oldHierarchy 
                        = oldHierarchy.substring(0, oldHierarchy.length() - 2);
                }
                res = res + ")" + printCall(elem);
            // hopefully we'll never get here
            } else {
                throw new RuntimeException("Measurements messed up");
            }
            oldHierarchy = elem.getHierarchy();
            i++;
        }
        // Finally, close the remaining brackets
        while (oldHierarchy.length() > 1) {
            res = res + ")";
            oldHierarchy = oldHierarchy.substring(0, oldHierarchy.length() - 2);
        }
        // Close the last bracket
        res = res + ")";
        return res;
    }
    
    /**
     * Removes the package declaration in front of a Class name.
     * 
     * @param className
     *            the class name to trim.
     * @return The class name without package prefix
     */
    private String trimClassNames(String className) {
        int i = className.lastIndexOf('.'); 
        if (i  > -1) {
            return className.substring(i + 1);
        } else {
            return className;
        }
    }
    
    /**
     * Print a method call in the new format for the given Measure Item.
     * 
     * @param elem
     *            The Measure Item
     * @return The method call string
     */
    private String printCall(MeasureItem elem) {
        return "(" + trimClassNames(elem.getEjbName()) 
            + " " + elem.getMethodName() 
            + ":" + elem.getDuration() + "ms" + " " + "\"\"";
    }
}
