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
package ch.elca.el4j.maven.logging;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.logging.AbstractLogger;
import org.codehaus.plexus.logging.AbstractLoggerManager;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

import ch.elca.el4j.maven.logging.console.ColourLogger;
import ch.elca.el4j.maven.logging.html.HtmlLogger;

/**
 * {@link LoggerManager} implementation that returns a ColourLogger or
 * HtmlLogger if a property is set, otherwise a ConsoleLogger. Based on
 * {@link ConsoleLoggerManager}. <script type="text/javascript">printFileStatus
 * ("$URL:
 * https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml
 * $", "$Revision: 2754 $", "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008)
 * $", "$Author: swismer $" );</script>
 * 
 * @author David Bernhard (DBD)
 */
public class FormattedLoggerManager extends AbstractLoggerManager implements
    LoggerManager, Initializable {

    /**
     * Message of this level or higher will be logged. This field is set by the
     * plexus container thus the name is 'threshold'. The field currentThreshold
     * contains the current setting of the threshold.
     */
    private String threshold = "info";

    /**
     * The current logger threshold.
     */
    private int m_currentThreshold;

    /**
     * A map of registered loggers, stores all loggers returned previsously
     * by component. Successive calls to getLoggerForComponent 
     * with the same parameters return the same object.
     */
    private Map<String, Logger> m_loggers;

    /**
     * Default constructor. Set level to info (might be changed leter).
     */
    public FormattedLoggerManager() {
        this("info");
    }

    /**
     * This special constructor is called directly when the container is
     * bootstrapping itself.
     * @param newThreshold The logger threshold.
     */
    public FormattedLoggerManager(String newThreshold) {
        this.threshold = newThreshold;

        // bootTimeLogger = true;

        initialize();
    }

    
    /** {@inheritDoc} */
    public void initialize() {
        m_currentThreshold = parseThreshold(threshold);

        if (m_currentThreshold == -1) {
            m_currentThreshold = Logger.LEVEL_DEBUG;
        }

        m_loggers = new HashMap<String, Logger>();
    }

    /** {@inheritDoc} */
    public void setThreshold(int currentThreshold) {
        this.m_currentThreshold = currentThreshold;
    }

    /**
     * @return Returns the threshold.
     */
    public int getThreshold() {
        return m_currentThreshold;
    }

    /** {@inheritDoc} */
    public void setThreshold(String role, String roleHint, int newThreshold) {
        AbstractLogger logger;
        String name;

        name = toMapKey(role, roleHint);
        logger = (AbstractLogger) m_loggers.get(name);

        if (logger == null) {
            return; 
            // nothing to do
        }

        logger.setThreshold(newThreshold);
    }

    /** {@inheritDoc} */
    public int getThreshold(String role, String roleHint) {
        AbstractLogger logger;
        String name;

        name = toMapKey(role, roleHint);
        logger = (AbstractLogger) m_loggers.get(name);

        if (logger == null) {
            return Logger.LEVEL_DEBUG;
            // does not return null because that could create a NPE
        }

        return logger.getThreshold();
    }

    /** {@inheritDoc} */
    public Logger getLoggerForComponent(String role, String roleHint) {
        Logger logger;
        String name;

        name = toMapKey(role, roleHint);
        logger = (Logger) m_loggers.get(name);

        if (logger != null) {
            return logger;
        }

        // Decide which logger to use based on properties.
        String type = System.getProperty("plexus.logger.type");

        if (type == null) {
            logger = new ConsoleLogger(getThreshold(), name);
        } else if (type.equals("ansi")) {
            logger = new ColourLogger(getThreshold(), name);
        } else if (type.equals("html")) {
            logger = new HtmlLogger(getThreshold(), name);
        } else {
            logger = new ConsoleLogger(getThreshold(), name);
        }

        m_loggers.put(name, logger);

        return logger;
    }

    /** {@inheritDoc} */
    public void returnComponentLogger(String role, String roleHint) {
        String name;

        name = toMapKey(role, roleHint);
        m_loggers.remove(name);
    }

    /** {@inheritDoc} */
    public int getActiveLoggerCount() {
        return m_loggers.size();
    }

    /**
     * Finr the threshold from a string and return it as a numeric value.
     * @param text The threshold as string.
     * @return A numeric constant for this threshold.
     */
    private int parseThreshold(String text) {
        int value = -1;
        String theText = text.trim().toLowerCase();

        if (theText.equals("debug")) {
            value = Logger.LEVEL_DEBUG;
        } else if (theText.equals("info")) {
            value = Logger.LEVEL_INFO;
        } else if (theText.equals("warn")) {
            value = Logger.LEVEL_WARN;
        } else if (theText.equals("error")) {
            value = Logger.LEVEL_ERROR;
        } else if (theText.equals("fatal")) {
            value = Logger.LEVEL_FATAL;
        }
        
        return value;
    }
}