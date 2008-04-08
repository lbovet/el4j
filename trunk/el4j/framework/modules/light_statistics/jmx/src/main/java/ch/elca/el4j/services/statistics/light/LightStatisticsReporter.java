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

package ch.elca.el4j.services.statistics.light;

import java.text.MessageFormat;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import ch.elca.el4j.services.monitoring.jmx.display.DisplayManager;
import ch.elca.el4j.services.monitoring.jmx.display.HtmlDisplayManager;
import ch.elca.el4j.services.monitoring.jmx.display.HtmlTabulator;
import ch.elca.el4j.services.monitoring.jmx.display.Section;

import com.jamonapi.MonitorFactory;

/**
 * This class is a JMX proxy for JAMon performance measurements.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public class LightStatisticsReporter 
        implements LightStatisticsReporterMBean, InitializingBean,
        DisposableBean { 

    /** The address that points to this class. */
    public static final String NAME = "Performance:key=lightStatisticsReporter";
    
    /** The measurements' captions. */
    private static String[] CAPTIONS = {
        "Target", "Hits", "Avg",
        "Total", "StdDev", "LastValue",
        "Min", "Max", "Active",
        "AvgActive", "MaxActive", "FirstAccess",
        "LastAccess", "Enabled", "Primary", "HasListeners"
    };
    
    
    /** Foramt string used to render responses. */
    private String m_formatString;
    
    /** The MBean server where this class is registered in. */
    private MBeanServer m_server;
    
    /** Whether class names are printed fully qualified or not. */
    private boolean m_fullyQualified = false;
    
    /**
     * {@inheritDoc}
     */
    public String getFormatString() {
        return m_formatString;
    }

    /**
     * {@inheritDoc}
     */
    public void setFormatString(String formatString) {
        this.m_formatString = formatString;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFullyQualified() {
        return m_fullyQualified;
    }

    /**
     * {@inheritDoc}
     */
    public void setFullyQualified(boolean fullyQualified) {
        this.m_fullyQualified = fullyQualified;
    }

    /**
     * @return Retruns the MBean server instance where this instance is
     * retistered in.
     */
    public MBeanServer getServer() {
        return m_server;
    }

    /**
     * Sets the MBean server where this instance has to register.
     * @param beanServer The MBean server to set.
     */
    public void setServer(MBeanServer beanServer) {
        m_server = beanServer;
    }
    
    /**
     * {@inheritDoc}
     */
    public void resetMonitor() {
        MonitorFactory.reset();
    }

    /**
     * {@inheritDoc}
     */
    public void enableMonitor() {
        MonitorFactory.setEnabled(true);
    }

    /**
     * {@inheritDoc}
     */
    public void disableMonitor() {
        MonitorFactory.setEnabled(false);
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getData() {
        Object[][] dataAsObj = MonitorFactory.getRootMonitor().getBasicData();
        
        if (dataAsObj == null) {
            return new String[]  {" "};
        }
        
        // make simple conversion to String[][]
        String[][] data;
        data = new String[dataAsObj.length][];
        for (int i = 0; i < dataAsObj.length; i++) {
            String[] line = new String[dataAsObj[i].length];
            for (int j = 0; j < dataAsObj[i].length; j++) {
                line[j] = (dataAsObj[i][j]).toString(); 
            }
            data[i] = line;
        }
        
        
        String[] paddedCaptions = new String[CAPTIONS.length];
        String[] result = new String[data.length + 1];
        MessageFormat format = new MessageFormat(m_formatString);
        
        int[] lengths = prepareData(data);
        
        // pad header
        for (int i = 0; i < CAPTIONS.length; i++) {
            paddedCaptions[i] = padString(CAPTIONS[i], lengths[i]);
        }
        
        // format header
        result[0] = format.format(paddedCaptions);
        
        
        if (data[0].length >= CAPTIONS.length) {
            // pad data
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < CAPTIONS.length; j++) {
                    data[i][j] = padString(data[i][j], lengths[j]);
                }
            }
            
            // format data
            for (int i = 0; i < data.length; i++) {
                result[i + 1] = format.format(data[i]); 
            }
        }
        
        return result;
    }
    
    /**
     * Converts class names, removes blank characters and computes the number
     * of chars in the widest line. 
     * @param data The measurement data.
     * @return The number of chars in the widest row for each column of the
     * data. 
     */
    protected int[] prepareData(String[][] data) {
        int[] lengths = new int[CAPTIONS.length];
        
        for (int i = 0; i < CAPTIONS.length; i++) {
            lengths[i] = CAPTIONS[i].length();
        }
        
        // no measurements taken
        if (data[0].length == 1) {
            return lengths;
        }
        
        // convert class names
        for (int j = 0; j < data.length; j++) {
            data[j][0] = convertClassName(data[j][0]);
            int len = data[j][0].length();
            if (len > lengths[0]) {
                lengths[0] = len;
            }
        }
        
        for (int i = 1; i < CAPTIONS.length; i++) {
            for (int j = 0; j < data.length; j++) {
                // remove &nbsp
                data[j][i] = data[j][i].replaceAll("&nbsp", " ");
                
                // compute length
                int len = data[j][i].length();
                if (len > lengths[i]) {
                    lengths[i] = len;
                }
            }
        }
        return lengths;
    }
    
    /**
     * Pads the given string to the requested lengths using <code>_</code>.
     * @param s The string to pad.
     * @param len The final length of the padded string.
     * @return The padded string.
     */
    private String padString(String s, int len) {
        StringBuffer buffer = new StringBuffer(s);
        for (int i = s.length(); i < len; i++) {
            buffer.append("_");
        }
        return buffer.toString();
    }
    
    /**
     * Formats a class name in demanded representation, fully qualified or just
     * <code>ClassName.MethodName</code>.
     * @param className
     *      The fully qualified class name with method name appended.
     * @return The string in demanded representation.
     */
    private String convertClassName(String className) {
        if (m_fullyQualified) {
            return className;
            
        } else {
            int pos = className.lastIndexOf('.');
            String fqClassName = className.substring(0, pos);
            pos = fqClassName.lastIndexOf('.');
            return className.substring(pos + 1);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void afterPropertiesSet() throws Exception {
        if (m_server == null) {
            throw new IllegalStateException("m_beanServer has not been set!");
        }
        
        m_server.registerMBean(this, new ObjectName(NAME));
    }

    /**
     * {@inheritDoc}
     */
    public void destroy() throws Exception {
        if (m_server != null) {
            m_server.unregisterMBean(new ObjectName(NAME));
        }
    }
    
    /** {@inheritDoc} */
    public String report() {
        DisplayManager manager = new HtmlDisplayManager();
        manager.setTitle("Light Statistics");
        Section section = new Section("Light Statistics");
        HtmlTabulator table = new HtmlTabulator(CAPTIONS);
        Object[][] data = MonitorFactory.getRootMonitor().getBasicData();
        
        if (data == null) {
            section.addWarning("No data.");
            manager.addSection(section);
            return manager.getPage();
        } 
        
        for (Object[] row : data) {
            String[] thisRow = new String[row.length];
            for (int i = 0; i < row.length; i++) {
                thisRow[i] = row[i].toString();
            }
            table.addRow(thisRow);
        }
        section.add(table.tabulate());
        manager.addSection(section);
        
        return manager.getPage();
    }
}
