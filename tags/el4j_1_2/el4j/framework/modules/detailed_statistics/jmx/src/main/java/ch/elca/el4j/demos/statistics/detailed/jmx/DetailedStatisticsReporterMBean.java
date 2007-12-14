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
package ch.elca.el4j.demos.statistics.detailed.jmx;

/**
 * This class publishes the detailed statistics through JMX (MBean Interface).
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
public interface DetailedStatisticsReporterMBean {
    
    /**
     * 
     * @return Table of ids of all measurement.
     */
    public  String showMeasureIDTable();
    
    
    /**
     * Creates a CSV file with the measurment results.
     * @param filename Name of file for output
     * @param measureId Id of measurments to output
     */
    public void createCSVFile(String filename, String measureId);
    
    
    /**
     * Creates a png file with the measurment results.
     * @param filename Name of file for output
     * @param measureId Id of measurments to output 
     */
    public void createDiagramFile(String filename, String measureId);
    
    /**
     * Creates a png file with the measurment results.
     * @param filename Name of file for output
     * @param measureId Id of measurments to output
     * @param width Width of the diagram
     * @param height Height of the diagram 
     */
    public void createDiagramFile(String filename, String measureId, 
        int width, int height);
}