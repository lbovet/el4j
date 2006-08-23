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

import org.codehaus.plexus.util.cli.StreamConsumer;

/**
 * Stream consumer to buffer output in a string buffer.
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
public class StringBufferConsumer implements StreamConsumer {
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
     * {@inheritDoc}
     */
    public void consumeLine(String line) {
        m_stringBuffer.append(line + LINE_SEPARATOR);
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return m_stringBuffer.toString();
    }
}
