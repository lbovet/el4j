/*
 * EL4J, the Enterprise Library for Java, complementing Spring http://el4j.sf.net
 * Copyright (C) 2005 by ELCA Informatique SA, Av. de la Harpe 22-24,
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

package ch.elca.el4j.services.statistics.light;

/**
 * This interface defines the functionality accessible through JMX.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$Source$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Andreas Bur (ABU)
 */
public interface LightStatisticsReporterMBean {

    /**
     * Retrieves the performance measurements and returns a string
     * representation. It's format can be changed through the
     * {@link LightStatisticsReporterMBean#setFormatString(String)} method.
     * 
     * @return Returns the performance measurements.
     */
    public String[] getData();
    
    /**
     * Clears all the measured data.
     */
    public void resetMonitor();

    /**
     * Enables the monitor to gather measurements.
     */
    public void enableMonitor();

    /**
     * Disables the monitor preventing gathering measurements.
     */
    public void disableMonitor();
    
    /**
     * @return Returns the format string used to layout measurements.  
     */
    public String getFormatString();
    
    /**
     * Sets the format string used to layout measurements. The formatter is a
     * {@link java.text.MessageFormat} instance.
     * @param formatString The format string.
     * @see java.text.MessageFormat
     */
    public void setFormatString(String formatString);
    
    /**
     * @return Returns <code>true</code> if class names are shown fully
     * qualified.
     */
    public boolean isFullyQualified();
    
    /**
     * Sets whether the class names should be shown in fully qualified
     * representation.
     * @param fullyQualified <code>true</code> to show fully qualified class
     * names.
     */
    public void setFullyQualified(boolean fullyQualified);
}