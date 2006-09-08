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
package ch.elca.el4j.plugins.repositoryhelper;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.cli.StreamConsumer;
import org.springframework.util.Assert;

/**
 * Stream consumer to buffer output in a string buffer and directly log it
 * on level "info".
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 *
 * @author Martin Zeltner (MZE)
 */
public class BufferedLoggerConsumer implements StreamConsumer {
    /**
     * Get os specific line separator.
     */
    public static final String LINE_SEPARATOR 
        = System.getProperty("line.separator"); 
    
    /**
     * Buffers the output.
     */
    protected final StringBuffer m_stringBuffer = new StringBuffer();
    
    /**
     * Is the consuming logger.
     */
    protected final Log m_log;
    
    /**
     * Constructor.
     * 
     * @param log Is the logger used to consume lines.
     */
    public BufferedLoggerConsumer(Log log) {
        Assert.notNull(log);
        m_log = log;
    }

    /**
     * {@inheritDoc}
     */
    public void consumeLine(String line) {
        m_stringBuffer.append(line + LINE_SEPARATOR);
        m_log.info(line);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return m_stringBuffer.toString();
    }
}
