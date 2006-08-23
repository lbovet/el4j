/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU General Public License (GPL) Version 2.0.
 * http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.monitoring.jmx;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;

/**
 * The interface of the logging proxy class, for setting logging properties via
 * JMX. <script type="text/javascript">printFileStatus ("$URL:
 * https://svn.sourceforge.net/svnroot/el4j/trunk/el4j/framework/module/jmx/java/ch/elca/el4j/services/monitoring/jmx/SpringBeanMBMBean.java
 * $", "$Revision$", "$Date: 2006-03-13 14:15:43 +0100 (Mo, 13 Mrz 2006)
 * $", "$Author$" );</script>
 * 
 * @author Rashid Waraich (RWA)
 */
public interface Log4jConfigMBean {

    /**
     * Getter method for the name member variable.
     * 
     * @return Returns the name.
     */
    String getName();

    /**
     * Changes the log level for a category or creates a new category if the
     * supplied category name doesn't exist.
     * 
     * @param category
     *            The category of the logger.
     * @param level
     *            The level of the logger to be set.
     */
    public void changeLogLevel(String category, String level);

    /**
     * Add appender to the specified logger category.
     * 
     * @param category
     *            The category of the logger.
     * @param appenderName
     *            The name of the appender to be removed.
     */
    public void addAppender(String category, String appenderName);

    /**
     * Remove appender from the specified logger category.
     * 
     * @param category
     *            The category of the logger.
     * @param appenderName
     *            The name of the appender to be removed.
     */
    public void removeAppender(String category, String appenderName);

    /**
     * For all loaded appenders, a String representaion of the appenderName and
     * appenderObject is returned.
     * 
     * @return The available appenders.
     */
    public String[] getAvailableAppendersList();

    /**
     * Return the log level of the Logger category.
     * 
     * @param category
     *            The category.
     * @return The level.
     */
    public Level showLogLevel(String category);

    /**
     * Return the appenders of the specified Logger category.
     * 
     * @param category
     *            The category.
     * @return The appenders.
     */
    public Appender[] showAppenders(String category);

    /**
     * Sets the level of the root logger.
     * 
     * @param level
     *            The logging level.
     */
    public void setRootLoggerLevel(String level);

    /**
     * Gives back the level of the root logger.
     * 
     * @return The logging level.
     */
    public String getRootLoggerLevel();

    /**
     * Shows the XML representation of all changes made to any logger level
     * through the JMX interface.
     * 
     * @return HTML embeddable XML (suitable for pasting in log4j.xml)
     */
    public String showLogLevelCache();
    
    /**
     * Get the path of the log4j configuration file, which was loaded
     * initially (at application start).
     * @return The path of the configuration file.
     */
    public String getInitialConfigurationPath();
}
