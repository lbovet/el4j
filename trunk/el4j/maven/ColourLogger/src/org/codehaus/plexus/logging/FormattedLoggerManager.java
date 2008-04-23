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
package org.codehaus.plexus.logging;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.logging.console.ColourLogger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.codehaus.plexus.logging.html.HtmlLogger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * {@link LoggerManager} implementation that returns a ColourLogger 
 * or HtmlLogger if a property is set, otherwise a ConsoleLogger.
 * Based on {@link ConsoleLoggerManager}.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL: https://el4j.svn.sourceforge.net/svnroot/el4j/trunk/el4j/etc/eclipse/codeTemplates.xml $",
 *    "$Revision: 2754 $",
 *    "$Date: 2008-03-04 09:04:15 +0100 (Tue, 04 Mar 2008) $",
 *    "$Author: swismer $"
 * );</script>
 *
 * @author David Bernhard (DBD)
 */
public class FormattedLoggerManager extends AbstractLoggerManager
    implements LoggerManager, Initializable {
   
    /**
     * Message of this level or higher will be logged. 
     * 
     * This field is set by the plexus container thus the name is 'threshold'. The field
     * currentThreshold contains the current setting of the threshold.
     */
    private String threshold = "info";

    private int currentThreshold;

    private Map<String, Logger> loggers;


    /** The number of active loggers in use. */
    /*
    private int loggerCount;
    */

    /*
    private boolean bootTimeLogger = false;
    */

    public FormattedLoggerManager() {
        this("info");
    }
    
    /**
     * This special constructor is called directly when the container is bootstrapping itself.
     */
    public FormattedLoggerManager( String threshold )
    {
        this.threshold = threshold;

        // bootTimeLogger = true;

        initialize();
    }

    public void initialize()
    {
        currentThreshold = parseThreshold( threshold );

        if ( currentThreshold == -1 )
        {
            currentThreshold = Logger.LEVEL_DEBUG;
        }

        loggers = new HashMap<String, Logger>();
    }

    public void setThreshold( int currentThreshold )
    {
        this.currentThreshold = currentThreshold;
    }

    /**
     * @return Returns the threshold.
     */
    public int getThreshold()
    {
        return currentThreshold;
    }

    // new stuff

    public void setThreshold( String role, String roleHint, int threshold ) {
        AbstractLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (AbstractLogger)loggers.get( name );

        if(logger == null) {
            return; // nothing to do
        }

        logger.setThreshold( threshold );
    }

    public int getThreshold( String role, String roleHint ) {
        AbstractLogger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (AbstractLogger)loggers.get( name );

        if(logger == null) {
            return Logger.LEVEL_DEBUG; 
            // does not return null because that could create a NPE
        }

        return logger.getThreshold();
    }

    public Logger getLoggerForComponent( String role, String roleHint ) 
    {
        Logger logger;
        String name;

        name = toMapKey( role, roleHint );
        logger = (Logger)loggers.get( name );

        if ( logger != null )
            return logger;

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
        
        loggers.put( name, logger );

        return logger;
    }

    public void returnComponentLogger( String role, String roleHint )
    {
        String name;

        name = toMapKey( role, roleHint );
        loggers.remove( name );
    }

    public int getActiveLoggerCount()
    {
        return loggers.size();
    }

    private int parseThreshold( String text )
    {
        text = text.trim().toLowerCase();

        if ( text.equals( "debug" ) )
        {
            return Logger.LEVEL_DEBUG;
        }
        else if ( text.equals( "info" ) )
        {
            return Logger.LEVEL_INFO;
        }
        else if ( text.equals( "warn" ) )
        {
            return Logger.LEVEL_WARN;
        }
        else if ( text.equals( "error" ) )
        {
            return Logger.LEVEL_ERROR;
        }
        else if ( text.equals( "fatal" ) )
        {
            return Logger.LEVEL_FATAL;
        }

        return -1;
    }
}